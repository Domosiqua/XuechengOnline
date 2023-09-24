package com.cwb.content.api;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author CWB
 * @version 1.0
 */
@SuppressWarnings({"all"})
@RestController
@RequestMapping("/coursepreview")
public class FreemarkerController {

    @GetMapping("/{id}")
    public ModelAndView test(@PathVariable int id){
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("test");
        modelAndView.addObject("name","CWB");

        return modelAndView;
    }


}
