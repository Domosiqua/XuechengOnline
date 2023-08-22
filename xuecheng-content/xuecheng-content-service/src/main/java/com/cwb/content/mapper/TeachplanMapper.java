package com.cwb.content.mapper;

import cwb.content.model.domain.Teachplan;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cwb.content.model.dto.TeachplanDto;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
* @author admin
* @description 针对表【teachplan(课程计划)】的数据库操作Mapper
* @createDate 2023-08-08 19:03:42
* @Entity domain.Teachplan
*/
@Mapper
@Component
public interface TeachplanMapper extends BaseMapper<Teachplan> {

    List<TeachplanDto> selectTreeNodes(long courseId);

    Teachplan getlast(@Param("orderby")Integer orderby,@Param("parentid") Long parentid, @Param("courseId")Long courseId);

    Teachplan getnext(@Param("orderby")Integer orderby, @Param("parentid")Long parentid, @Param("courseId")Long courseId);

    Integer getMaxOrderby(@Param("parentid")Long parentid, @Param("courseId")Long courseId);
}




