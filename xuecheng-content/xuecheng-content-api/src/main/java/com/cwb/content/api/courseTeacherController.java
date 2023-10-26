package com.cwb.content.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cwb.content.service.CourseTeacherService;
import com.cwb.content.model.domain.CourseTeacher;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author CWB
 * @version 1.0
 */
@SuppressWarnings({"all"})
@RestController
@RequestMapping("/courseTeacher")
public class courseTeacherController {


    @Autowired
    CourseTeacherService service;

    @ApiOperation("获取课程老师列表")
    @GetMapping("/list/{courseid}")
    public List<CourseTeacher> list(@PathVariable Long courseid){
       return service.list(new LambdaQueryWrapper<CourseTeacher>().eq(CourseTeacher::getCourseId,courseid));
    }
    @ApiOperation("新增课程老师")
    @PostMapping
    public void insert(@RequestBody CourseTeacher teacher){
        CourseTeacher byId = service.getById(teacher.getId());
        if(byId==null)
            service.save(teacher);
        else
            service.updateById(teacher);
        return ;
    }
    @ApiOperation("删除课程老师")
    @DeleteMapping("/course/{courseId}/{teacherId}")
    public void deleteTeacher(@PathVariable Long courseId,@PathVariable Long teacherId){
        LambdaQueryWrapper<CourseTeacher> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(CourseTeacher::getCourseId,courseId).eq(CourseTeacher::getId,teacherId);
        service.remove(wrapper);

        return ;
    }
}
