package com.cwb.media.service;

import com.cwb.base.model.PageParams;
import com.cwb.base.model.PageResult;
import com.cwb.base.model.RestResponse;
import com.cwb.media.model.dto.QueryMediaParamsDto;
import com.cwb.media.model.dto.UploadFileParamsDto;
import com.cwb.media.model.dto.UploadFileResultDto;
import com.cwb.media.model.po.MediaFiles;

import java.io.File;

/**
 * @description 媒资文件管理业务类
 * @author CWB
 * @version 1.0
 */
public interface MediaFileService {

 /**
  * @description 媒资文件查询方法
  * @param pageParams 分页参数
  * @param queryMediaParamsDto 查询条件
  * @return com.cwb.base.model.PageResult<com.cwb.media.model.po.MediaFiles>
  *
 */
 PageResult<MediaFiles> queryMediaFiels(Long companyId,PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);




    UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath, String objectName);

    RestResponse<Boolean> checkFile(String fileMd5);

    RestResponse<Boolean> checkChunk(String fileMd5, int chunk);

    File downloadFileFromMinIO(String bucket, String objectName);

    boolean addFileTobucket (String localFilePath,String mimeType,String bucket, String objectName);

    RestResponse uploadchunk(String fileMd5, int chunk, String localChunkPath);

    RestResponse mergechunks(Long companyId,String fileMd5,int chunkTotal,UploadFileParamsDto uploadFileParamsDto);

    MediaFiles getFileById(String mediaId);
}
