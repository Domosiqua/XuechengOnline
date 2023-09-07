package com.cwb.media.api;

import com.cwb.base.model.PageParams;
import com.cwb.base.model.PageResult;
import com.cwb.media.model.dto.QueryMediaParamsDto;
import com.cwb.media.model.dto.UploadFileParamsDto;
import com.cwb.media.model.dto.UploadFileResultDto;
import com.cwb.media.model.po.MediaFiles;
import com.cwb.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @description 媒资文件管理接口
 * @author CWB
 * @version 1.0
 */
 @Api(value = "媒资文件管理接口",tags = "媒资文件管理接口") @RestController
public class MediaFilesController {


  @Autowired
  MediaFileService mediaFileService;


     @ApiOperation("媒资列表查询接口")
     @PostMapping("/files")
     public PageResult<MediaFiles> list(PageParams pageParams, @RequestBody(required = false) QueryMediaParamsDto queryMediaParamsDto){
      Long companyId = 1232141425L;
      return mediaFileService.queryMediaFiels(companyId,pageParams,queryMediaParamsDto);
     }

    @ApiOperation("上传文件")
    @RequestMapping(value = "/upload/coursefile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public UploadFileResultDto upload(@RequestPart("filedata") MultipartFile multipartFile,@RequestParam(value = "folder",required=false) String folder,@RequestParam(value = "objectName",required=false) String objectName) throws IOException {

         Long companyId = 1232141425L;
         UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
         uploadFileParamsDto.setFileSize(multipartFile.getSize());
         uploadFileParamsDto.setFileType("001001");
         uploadFileParamsDto.setFilename(multipartFile.getOriginalFilename());
         File tempFile = File.createTempFile("minio", "temp");
         //上传的文件拷贝到临时文件
         multipartFile.transferTo(tempFile);
         String localFilePath=tempFile.getAbsolutePath();

        UploadFileResultDto ret=mediaFileService.uploadFile(companyId,uploadFileParamsDto,localFilePath);
         return ret;
    }


}
