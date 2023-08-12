package com.cwb.content.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cwb.content.mapper.CourseTeacherMapper;
import cwb.content.model.domain.CourseTeacher;
import com.cwb.content.service.CourseTeacherService;
import org.springframework.stereotype.Service;

/**
* @author admin
* @description 针对表【course_teacher(课程-教师关系表)】的数据库操作Service实现
* @createDate 2023-08-08 19:03:42
*/
@Service
public class CourseTeacherServiceImpl extends ServiceImpl<CourseTeacherMapper, CourseTeacher>
    implements CourseTeacherService{

}




