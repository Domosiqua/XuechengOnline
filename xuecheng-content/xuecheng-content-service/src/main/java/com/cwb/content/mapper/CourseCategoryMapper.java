package com.cwb.content.mapper;

import cwb.content.model.domain.CourseCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
* @author admin
* @description 针对表【course_category(课程分类)】的数据库操作Mapper
* @createDate 2023-08-08 19:03:42
* @Entity generator.domain.CourseCategory
*/
@Component
@Mapper
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {

}




