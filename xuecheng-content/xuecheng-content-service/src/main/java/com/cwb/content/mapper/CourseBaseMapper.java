package com.cwb.content.mapper;

import cwb.content.model.domain.CourseBase;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cwb.content.model.dto.CourseCategoryTreeDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
* @author admin
* @description 针对表【course_base(课程基本信息)】的数据库操作Mapper
* @createDate 2023-08-08 19:03:42
* @Entity generator.domain.CourseBase
*/
@Mapper
@Component
public interface CourseBaseMapper extends BaseMapper<CourseBase> {
    public List<CourseCategoryTreeDto> seleceTreeNode(String id);
}




