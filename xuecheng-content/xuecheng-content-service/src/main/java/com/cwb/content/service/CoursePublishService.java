package com.cwb.content.service;

import com.cwb.content.model.domain.CoursePublish;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.File;

/**
* @author admin
* @description 针对表【course_publish(课程发布)】的数据库操作Service
* @createDate 2023-08-08 19:03:42
*/
public interface CoursePublishService extends IService<CoursePublish> {

    void commit(Long courseId, Long companyid);

    void publish(Long companyId, Long courseId);

    File generateCourseHtml(Long courseId);

    void uploadCourseHtml(Long courseId,File file);


}
