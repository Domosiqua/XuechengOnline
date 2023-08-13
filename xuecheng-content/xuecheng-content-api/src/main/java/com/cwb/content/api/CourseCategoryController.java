package com.cwb.content.api;

import com.cwb.content.service.CourseCategoryService;
import cwb.content.model.domain.CourseCategory;
import cwb.content.model.dto.CourseCategoryTreeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author CWB
 * @version 1.0
 */
@SuppressWarnings({"all"})
@RestController
public class CourseCategoryController {

    @Autowired
    CourseCategoryService service;

    @GetMapping("/course-category/tree-nodes")
    public List<CourseCategoryTreeDto> queryTreeNodes(){
        List<CourseCategoryTreeDto> ret=service.queryTreeNodes("1");
        return ret;
    }


}
