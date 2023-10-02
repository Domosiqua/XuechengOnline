package com.cwb.content.api;

import com.cwb.content.service.CourseBaseService;
import com.cwb.content.service.CoursePublishService;
import cwb.content.model.dto.CoursePreviewDto;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.PrivateKey;
import java.util.PrimitiveIterator;

/**
 * @author CWB
 * @version 1.0
 */

@Api(value = "课程公开查询接口",tags = "课程公开查询接口")
@RestController
@RequestMapping("/open")
public class CourseOpenController {

    @Autowired
    private CourseBaseService courseBaseService;

    @GetMapping("/course/whole/{courseId}")
    public CoursePreviewDto getPreviewInfo(@PathVariable("courseId") Long courseId) {
        //获取课程预览信息
        CoursePreviewDto coursePreviewInfo = courseBaseService.getbasemodel(courseId);
        return coursePreviewInfo;
    }

}
