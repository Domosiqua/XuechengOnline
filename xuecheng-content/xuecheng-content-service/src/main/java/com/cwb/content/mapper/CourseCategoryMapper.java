package com.cwb.content.mapper;

import com.cwb.content.model.domain.CourseCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cwb.content.model.dto.CourseCategoryTreeDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
* @author admin
* @description 针对表【course_category(课程分类)】的数据库操作Mapper
* @createDate 2023-08-08 19:03:42
*/
@Component
@Mapper
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {

    List<CourseCategoryTreeDto> selectTreeNodes(String id);
}




