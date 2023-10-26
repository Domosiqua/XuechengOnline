package com.cwb.content.service;

import com.cwb.base.model.PageParams;
import com.cwb.base.model.PageResult;
import com.cwb.content.model.domain.CourseBase;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cwb.content.model.dto.*;

/**
* @author admin
* @description 针对表【course_base(课程基本信息)】的数据库操作Service
* @createDate 2023-08-08 19:03:42
*/
public interface CourseBaseService extends IService<CourseBase> {

    PageResult<CourseBase> getPageConditionList(Long CompanyId,PageParams pageParams, QueryCourseParamsDto queryCourseParams);

    CourseBaseInfoDto createCourseBase(Long companyid, AddCourseDto addCourseDto);

    CourseBaseInfoDto updateCourse(Long companyId, EditCourseDto addCourseDto);

    CourseBaseInfoDto getCourseBaseInfo(Long id);

    void deleteCourseByid(Long courseId);

    CoursePreviewDto getbasemodel(Long courseId);
}
