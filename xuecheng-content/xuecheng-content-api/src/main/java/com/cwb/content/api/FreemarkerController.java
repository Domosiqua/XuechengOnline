package com.cwb.content.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
/**
 * @author CWB
 * @version 1.0
 */
@Controller
@RequestMapping("/coursepreview")
public class FreemarkerController {

    @GetMapping("/{courseId}")
    public ModelAndView preview(@PathVariable("courseId") Long courseId){

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("model",null);
        modelAndView.setViewName("course_template");
        return modelAndView;
    }


}
