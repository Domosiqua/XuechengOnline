package com.cwb.content.api;

import com.cwb.content.service.CoursePublishService;
import cwb.content.model.dto.CoursePreviewDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
/**
 * @author CWB
 * @version 1.0
 */
@Controller
public class FreemarkerController {

    @Autowired
    private CoursePublishService coursePublishService;

    @GetMapping("/coursepreview/{courseId}")
    public ModelAndView preview(@PathVariable("courseId") Long courseId){

        CoursePreviewDto model = coursePublishService.getbasemodel(courseId);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("model",model);
        modelAndView.setViewName("course_template");
        return modelAndView;
    }


}
