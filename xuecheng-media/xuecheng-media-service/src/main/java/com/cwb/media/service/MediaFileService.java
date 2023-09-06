package com.cwb.media.service;

import com.cwb.base.model.PageParams;
import com.cwb.base.model.PageResult;
import com.cwb.media.model.dto.QueryMediaParamsDto;
import com.cwb.media.model.dto.UploadFileParamsDto;
import com.cwb.media.model.dto.UploadFileResultDto;
import com.cwb.media.model.po.MediaFiles;
import org.springframework.web.bind.annotation.RequestBody;

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
  * @author Mr.M
 */
  PageResult<MediaFiles> queryMediaFiels(Long companyId,PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);


    UploadFileResultDto uploadCoursefile();

    UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath);
}
