package com.cwb.content.api;

import com.cwb.content.service.CoursePublishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author CWB
 * @version 1.0
 */
@SuppressWarnings({"all"})
@RestController
@RequestMapping("/courseaudit")
public class CoursePublishController {

    @Autowired
    CoursePublishService coursePublishService;

    @PostMapping("/commit/{courseId}")
    public void commit(@PathVariable Long courseId){
        Long companyId=1232141425L;
        coursePublishService.commit(courseId,companyId);
    }
}
