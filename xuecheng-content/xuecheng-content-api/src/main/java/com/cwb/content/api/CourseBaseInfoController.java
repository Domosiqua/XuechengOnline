package com.cwb.content.api;

import com.cwb.base.model.PageParams;
import com.cwb.base.model.PageResult;
import cwb.content.model.domain.CourseBase;
import cwb.content.model.dto.QueryCourseParamsDto;
import com.cwb.content.service.CourseBaseService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author CWB
 * @version 1.0
 */
@SuppressWarnings({"all"})
@RestController
public class CourseBaseInfoController {
    @Autowired
    CourseBaseService service;

    @ApiOperation("课程查询接口")
    @PostMapping("/course/list")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required=false) QueryCourseParamsDto queryCourseParams){

        PageResult<CourseBase> pageResult=service.getPageConditionList(pageParams,queryCourseParams);

        return pageResult;

    }
}
