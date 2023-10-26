package com.cwb.content.api;

import com.cwb.content.model.domain.CoursePublish;
import com.cwb.content.service.CoursePublishService;
import com.cwb.content.util.SecurityUtil;
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
        Long companyId=SecurityUtil.getUserCompanyID();
        coursePublishService.commit(courseId,companyId);
    }
    @ApiOperation("课程发布")
    @ResponseBody
    @PostMapping ("/coursepublish/{courseId}")
    public void coursepublish(@PathVariable("courseId") Long courseId){
        Long companyId = SecurityUtil.getUserCompanyID();
        coursePublishService.publish(companyId,courseId);
    }
    @ResponseBody
    @GetMapping("/r/coursepublish/{courseId}")
    public CoursePublish getCoursepublish(@PathVariable("courseId") Long courseId){
        CoursePublish byId = coursePublishService.getById(courseId);
        return byId;
    }
}
