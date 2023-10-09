package com.cwb.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cwb.base.exception.XcException;
import com.cwb.base.model.PageParams;
import com.cwb.base.model.PageResult;
import com.cwb.base.model.RestResponse;
import com.cwb.base.utils.StringUtil;
import com.cwb.media.mapper.MediaFilesMapper;
import com.cwb.media.mapper.MediaProcessMapper;
import com.cwb.media.model.dto.QueryMediaParamsDto;
import com.cwb.media.model.dto.UploadFileParamsDto;
import com.cwb.media.model.dto.UploadFileResultDto;
import com.cwb.media.model.po.MediaFiles;
import com.cwb.media.model.po.MediaProcess;
import com.cwb.media.service.MediaFileService;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author CWB
 * @version 1.0
 */
@Service
@Slf4j
public class MediaFileServiceImpl implements MediaFileService {

 @Autowired
 MediaFilesMapper mediaFilesMapper;
 @Autowired
 MediaFileServiceImpl current;
 @Autowired
 MinioClient minioClient;
 @Autowired
 MediaProcessMapper mediaProcessMapper;
 @Value("${minio.bucket.files}")
 private String bucket_Files;
 @Value("${minio.bucket.videofiles}")
 private String bucket_vedioFiles;
 @Override
 public PageResult<MediaFiles> queryMediaFiels(Long companyId,PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

  //构建查询条件对象
  LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();

  if(queryMediaParamsDto!=null){
      String filename=queryMediaParamsDto.getFilename();
      String filetype=queryMediaParamsDto.getFileType();
      String status=queryMediaParamsDto.getAuditStatus();
      queryWrapper.like(filename!=null&&!"".equals(filename),MediaFiles::getFilename,filename);
      queryWrapper.eq(filetype!=null&&!"".equals(filetype),MediaFiles::getFileType,filetype);
      queryWrapper.eq(status!=null&&!"".equals(status),MediaFiles::getAuditStatus,status);
  }
  //分页对象
  Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
  // 查询数据内容获得结果
  Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
  // 获取数据列表
  List<MediaFiles> list = pageResult.getRecords();
  // 获取数据总数
  long total = pageResult.getTotal();
  // 构建结果集
  PageResult<MediaFiles> mediaListResult = new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
  return mediaListResult;

 }

    /**
     *
     * @param companyId
     * @param uploadFileParamsDto
     * @param localFilePath 本地路径
     * @param objectName 如果传入objectname 则上传到该目录
     * @return
     */
 @Override
 public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath, String objectName) {
  String filename = uploadFileParamsDto.getFilename();
  String extension = filename.substring(filename.lastIndexOf("."));
  String mimeType = getMimeType(extension);
  String fileMd5 = getFileMd5(new File(localFilePath));
  if (StringUtil.isEmpty(objectName)){
      String defaultFolderPath = getDefaultFolderPath();
      objectName=defaultFolderPath+fileMd5+extension;
  }
  boolean b = false;
  try {
        b = addFileTobucket(localFilePath, mimeType, bucket_Files, objectName);
  } catch (Exception e) {
        e.printStackTrace();
  }
  if(!b){
        XcException.cast("文件上传失败");
  }
  MediaFiles mediaFiles = current.addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucket_Files, objectName);

  UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
  BeanUtils.copyProperties(mediaFiles, uploadFileResultDto);
  return uploadFileResultDto;

 }

 @Override
 public RestResponse<Boolean> checkFile(String fileMd5) {
  MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
  if(mediaFiles==null){
      return RestResponse.success(false);
  }else{
      String filePath=mediaFiles.getFilePath();
      String bucket = mediaFiles.getBucket();
      try {
          InputStream vedio = minioClient.getObject(GetObjectArgs.
                  builder().
                  bucket(bucket).
                  object(filePath).
                  build());
          if (vedio!=null)
              return RestResponse.success(true);
      } catch (Exception e) {
          e.printStackTrace();
      }
  }

     return RestResponse.success(false);

 }

 @Override
 public RestResponse<Boolean> checkChunk(String fileMd5, int chunk) {

         String filePath=getChunkFileFolderPath(fileMd5)+chunk;

         try {
             InputStream vedio = minioClient.getObject(GetObjectArgs.
                     builder().
                     bucket(bucket_vedioFiles).
                     object(filePath).
                     build());
             if (vedio!=null)
                 return RestResponse.success(true);
         } catch (Exception e) {
             e.printStackTrace();
         }
     return RestResponse.success(false);
 }



    @Override
    public RestResponse uploadchunk(String fileMd5, int chunk, String localChunkPath) {
        String chunkFilePath=getChunkFileFolderPath(fileMd5)+chunk;

        boolean b = addFileTobucket(localChunkPath, getMimeType(null), bucket_vedioFiles, chunkFilePath);

        if (!b) {
            log.debug("上传分块文件失败:{}", chunkFilePath);
            return RestResponse.validfail(false, "上传分块失败");
        }
        log.debug("上传分块文件成功:{}",chunkFilePath);
        return RestResponse.success(true);

    }

    @Override
    public RestResponse mergechunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto) {
     String filePath=getChunkFileFolderPath(fileMd5);

     List<ComposeSource> sources = Stream.iterate(0, i -> ++i)
                .limit(chunkTotal)
                .map(i -> ComposeSource.builder()
                        .bucket(bucket_vedioFiles)
                        .object(filePath.concat(Integer.toString(i)))
                        .build())
                .collect(Collectors.toList());
        String filename=uploadFileParamsDto.getFilename();
        String extName=filename.substring(filename.lastIndexOf('.'));
        String objectname=getFilePathByMd5(fileMd5,extName);
        ComposeObjectArgs composeObjectArgs = ComposeObjectArgs.builder().bucket(bucket_vedioFiles).object(objectname).sources(sources).build();

        try {
            ObjectWriteResponse objectWriteResponse = minioClient.composeObject(composeObjectArgs);
            log.debug("合并文件成功:{}",objectname);
        } catch (Exception e) {
            log.debug("合并文件失败,fileMd5:{},异常:{}",fileMd5,e.getMessage(),e);
            return RestResponse.validfail(false, "合并文件失败");
        }
        File minioFile = downloadFileFromMinIO(bucket_vedioFiles,objectname);
        if(minioFile == null){
            log.debug("下载合并后文件失败,mergeFilePath:{}",objectname);
            return RestResponse.validfail(false, "下载合并后文件失败。");
        }

        try (InputStream newFileInputStream = new FileInputStream(minioFile)) {
            //minio上文件的md5值
            String md5Hex = DigestUtils.md5Hex(newFileInputStream);
            //比较md5值，不一致则说明文件不完整
            if(!fileMd5.equals(md5Hex)){
                return RestResponse.validfail(false, "文件合并校验失败，最终上传失败。");
            }
            //文件大小
            uploadFileParamsDto.setFileSize(minioFile.length());
        }catch (Exception e){
            log.debug("校验文件失败,fileMd5:{},异常:{}",fileMd5,e.getMessage(),e);
            return RestResponse.validfail(false, "文件合并校验失败，最终上传失败。");
        }finally {
            if(minioFile!=null){
                minioFile.delete();
            }
        }
        current.addMediaFilesToDb(companyId,fileMd5,uploadFileParamsDto,bucket_vedioFiles,objectname);
        //=====清除分块文件=====
        clearChunkFiles(filePath,chunkTotal);


        return RestResponse.success(true);
    }

    @Override
    public MediaFiles getFileById(String mediaId) {
        return mediaFilesMapper.selectById(mediaId);
    }

    private void clearChunkFiles(String chunkFileFolderPath,int chunkTotal){

        try {
            List<DeleteObject> deleteObjects = Stream.iterate(0, i -> ++i)
                    .limit(chunkTotal)
                    .map(i -> new DeleteObject(chunkFileFolderPath.concat(Integer.toString(i))))
                    .collect(Collectors.toList());

            RemoveObjectsArgs removeObjectsArgs = RemoveObjectsArgs.builder().bucket(bucket_vedioFiles).objects(deleteObjects).build();
            Iterable<Result<DeleteError>> results = minioClient.removeObjects(removeObjectsArgs);
            results.forEach(r->{
                DeleteError deleteError = null;
                try {
                    deleteError = r.get();
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("清除分块文件失败,objectname:{}",deleteError.objectName(),e);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            log.error("清除分块文件失败,chunkFileFolderPath:{}",chunkFileFolderPath,e);
        }
    }

    /**
     * 从minio下载文件
     * @param bucket 桶
     * @param objectName 对象名称
     * @return 下载后的文件
     */
    @Override
    public File downloadFileFromMinIO(String bucket,String objectName){
        //临时文件
        File minioFile = null;
        FileOutputStream outputStream = null;
        try{
            InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build());
            //创建临时文件
            minioFile=File.createTempFile("minio", ".merge");
            outputStream = new FileOutputStream(minioFile);
            IOUtils.copy(stream,outputStream);
            return minioFile;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(outputStream!=null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public boolean addFileTobucket (String localFilePath,String mimeType,String bucket, String objectName) {

        try {
   UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
           .bucket(bucket)
           .object(objectName)
           .filename(localFilePath)
           .contentType(mimeType)
           .build();
   minioClient.uploadObject(uploadObjectArgs);
   log.debug("上传文件到minio成功,bucket:{},objectName:{}",bucket,objectName);
   System.out.println("上传成功");
   return true;
  } catch (Exception e) {
   e.printStackTrace();
   log.error("上传文件到minio出错,bucket:{},objectName:{},错误原因:{}",bucket,objectName,e.getMessage(),e);
   XcException.cast("上传文件到文件系统失败");
  }
  return false;

 }
 @Transactional
 public MediaFiles addMediaFilesToDb(Long companyId,String fileMd5,UploadFileParamsDto uploadFileParamsDto,String bucket,String objectName){
  //从数据库查询文件
  MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
  if (mediaFiles == null) {
   mediaFiles = new MediaFiles();
   //拷贝基本信息
   BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
   mediaFiles.setId(fileMd5);
   mediaFiles.setFileId(fileMd5);
   mediaFiles.setCompanyId(companyId);
   mediaFiles.setUrl("/" + bucket + "/" + objectName);
   mediaFiles.setBucket(bucket);
   mediaFiles.setFilePath(objectName);
   mediaFiles.setCreateDate(LocalDateTime.now());
   mediaFiles.setAuditStatus("002003");
   mediaFiles.setStatus("1");
   //保存文件信息到文件表
   int insert = mediaFilesMapper.insert(mediaFiles);
   if (insert < 0) {
    log.error("保存文件信息到数据库失败,{}",mediaFiles.toString());
    XcException.cast("保存文件信息失败");
   }
      //添加到待处理任务表
      addWaitingTask(mediaFiles);
      log.debug("保存文件信息到数据库成功,{}", mediaFiles.toString());
  }

  return mediaFiles;

 }

    /**
     * 添加待处理任务
     * @param mediaFiles 媒资文件信息
     */

    private void addWaitingTask(MediaFiles mediaFiles) {
        String filename = mediaFiles.getFilename();
        //文件扩展名
        String extension = filename.substring(filename.lastIndexOf("."));
        //文件mimeType
        String mimeType = getMimeType(extension);
        if (mimeType.equals("video/x-msvideo")){//avi
            MediaProcess mediaProcess = new MediaProcess();
            BeanUtils.copyProperties(mediaFiles,mediaProcess);
            mediaProcess.setFailCount(0);
            mediaProcess.setStatus("1");
            mediaProcess.setCreateDate(LocalDateTime.now());
            mediaProcessMapper.insert(mediaProcess);
        }
    }

 private String getMimeType(String filename) {
     String mimetype= MediaType.APPLICATION_OCTET_STREAM_VALUE;
     if (filename==null)
         return mimetype;
  ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(filename);

  if (extensionMatch!=null)
    mimetype=extensionMatch.getMimeType();
  return mimetype;
 }

    /**
     * @return 2023/8/8/
     */
 private String getDefaultFolderPath() {
  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
  String folder = sdf.format(new Date()).replace("-", "/")+"/";
  return folder;
 }

 //获取文件的md5
 private String getFileMd5(File file) {
  try (FileInputStream fileInputStream = new FileInputStream(file)) {
   String fileMd5 = DigestUtils.md5Hex(fileInputStream);
   return fileMd5;
  } catch (Exception e) {
   e.printStackTrace();
   return null;
  }
 }
    private String getFilePathByMd5(String fileMd5,String fileExt){
        return   fileMd5.substring(0,1) + "/" + fileMd5.substring(1,2) + "/" + fileMd5 + "/" +fileMd5 +fileExt;
    }
    private String getChunkFileFolderPath(String fileMd5) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/chunk/";
    }


}
