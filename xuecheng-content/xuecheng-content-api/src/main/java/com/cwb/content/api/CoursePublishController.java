package com.cwb.content.api;

import com.cwb.content.service.CoursePublishService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author CWB
 * @version 1.0
 */
@SuppressWarnings({"all"})
@RestController
public class CoursePublishController {

    @Autowired
    CoursePublishService coursePublishService;

    @ApiOperation("课程提交审核")
    @PostMapping("/courseaudit/commit/{courseId}")
    public void commit(@PathVariable Long courseId){
        Long companyId=1232141425L;
        coursePublishService.commit(courseId,companyId);
    }
    @ApiOperation("课程发布")
    @ResponseBody
    @PostMapping ("/coursepublish/{courseId}")
    public void coursepublish(@PathVariable("courseId") Long courseId){
        Long companyId = 1232141425L;
        coursePublishService.publish(companyId,courseId);

    }
}
