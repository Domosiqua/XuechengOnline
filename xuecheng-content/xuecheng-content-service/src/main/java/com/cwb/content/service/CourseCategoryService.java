package com.cwb.content.service;

import com.cwb.content.model.domain.CourseCategory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cwb.content.model.dto.CourseCategoryTreeDto;

import java.util.List;

/**
* @author admin
* @description 针对表【course_category(课程分类)】的数据库操作Service
* @createDate 2023-08-08 19:03:42
*/
public interface CourseCategoryService extends IService<CourseCategory> {

    List<CourseCategoryTreeDto> queryTreeNodes(String id);
}
