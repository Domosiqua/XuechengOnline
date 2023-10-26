package com.cwb.content.mapper;

import com.cwb.content.model.domain.CourseTeacher;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
* @author admin
* @description 针对表【course_teacher(课程-教师关系表)】的数据库操作Mapper
* @createDate 2023-08-08 19:03:42
* @Entity generator.domain.CourseTeacher
*/
@Mapper
@Component
public interface CourseTeacherMapper extends BaseMapper<CourseTeacher> {

}




