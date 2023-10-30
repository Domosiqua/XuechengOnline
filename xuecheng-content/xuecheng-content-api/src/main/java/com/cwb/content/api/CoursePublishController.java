package com.cwb.content.api;

import com.alibaba.fastjson.JSON;
import com.cwb.content.model.domain.CoursePublish;
import com.cwb.content.model.dto.CourseBaseInfoDto;
import com.cwb.content.model.dto.CoursePreviewDto;
import com.cwb.content.model.dto.TeachplanDto;
import com.cwb.content.service.CoursePublishService;
import com.cwb.content.util.SecurityUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
     * /r 专供别的微服务进行远程调用
     * @param courseId
     * @return
     */
    @ResponseBody
    @GetMapping("/r/coursepublish/{courseId}")
    public CoursePublish getCoursepublish(@PathVariable("courseId") Long courseId){
        CoursePublish byId = coursePublishService.getById(courseId);
        return byId;
    }
    @ApiOperation("获取课程发布信息")
    @ResponseBody
    @GetMapping("/course/whole/{courseId}")
    public CoursePreviewDto getCoursePublish(@PathVariable("courseId") Long courseId) {
        //查询课程发布信息
        CoursePublish coursePublish = coursePublishService.getById(courseId);
        if (coursePublish == null) {
            return new CoursePreviewDto();
        }

        //课程基本信息
        CourseBaseInfoDto courseBase = new CourseBaseInfoDto();
        BeanUtils.copyProperties(coursePublish, courseBase);
        //课程计划
        List<TeachplanDto> teachplans = JSON.parseArray(coursePublish.getTeachplan(), TeachplanDto.class);
        CoursePreviewDto coursePreviewInfo = new CoursePreviewDto();
        coursePreviewInfo.setCourseBase(courseBase);
        coursePreviewInfo.setTeachplans(teachplans);
        return coursePreviewInfo;
    }
}
