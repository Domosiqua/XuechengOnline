package com.cwb.content.service;

import cwb.content.model.domain.CoursePublish;
import com.baomidou.mybatisplus.extension.service.IService;
import cwb.content.model.dto.CoursePreviewDto;

/**
* @author admin
* @description 针对表【course_publish(课程发布)】的数据库操作Service
* @createDate 2023-08-08 19:03:42
*/
public interface CoursePublishService extends IService<CoursePublish> {

    CoursePreviewDto getbasemodel(Long courseId);
}
