package com.cwb.content.api;

import com.cwb.base.model.PageParams;
import com.cwb.base.model.PageResult;
import cwb.content.model.domain.CourseBase;
import cwb.content.model.dto.AddCourseDto;
import cwb.content.model.dto.CourseBaseInfoDto;
import cwb.content.model.dto.QueryCourseParamsDto;
import com.cwb.content.service.CourseBaseService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author CWB
 * @version 1.0
 */
@SuppressWarnings({"all"})
@RestController
@RequestMapping("/course")
public class CourseBaseInfoController {
    @Autowired
    CourseBaseService service;

    @ApiOperation("课程查询接口")
    @PostMapping("/list")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required = false) QueryCourseParamsDto queryCourseParams){
        PageResult<CourseBase> pageResult=service.getPageConditionList(pageParams,queryCourseParams);
        return pageResult;
    }
    @ApiOperation("新增课程信息")
    @PostMapping
    public CourseBaseInfoDto createCourseBase(@RequestBody AddCourseDto addCourseDto){
        Long companyId = 1232141425L;
        CourseBaseInfoDto ret = service.createCourseBase(companyId,addCourseDto);
        return ret;
    }

}
