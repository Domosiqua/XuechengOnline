package com.cwb.content.api;

import com.cwb.base.exception.ValidationGroups;
import com.cwb.base.model.PageParams;
import com.cwb.base.model.PageResult;
import com.cwb.content.model.dto.*;
import com.cwb.content.service.CourseMarketService;
import com.cwb.content.util.SecurityUtil;
import com.cwb.content.model.domain.CourseBase;
import com.cwb.content.service.CourseBaseService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Security;

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
    @Autowired
    CourseMarketService marketService;
    @ApiOperation("课程查询接口")
    @PreAuthorize("hasAuthority('xc_teachmanager_course_list')")
    @PostMapping("/list")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required = false) QueryCourseParamsDto queryCourseParams){
        SecurityUtil.XcUser user = SecurityUtil.getUser();

        String companyId=user.getCompanyId();
        PageResult<CourseBase> pageResult;
        if(StringUtils.isNotEmpty(companyId))
            pageResult=service.getPageConditionList(Long.parseLong(companyId),pageParams,queryCourseParams);
        else
            pageResult=service.getPageConditionList(0L,pageParams,queryCourseParams);
        return pageResult;
    }
    @ApiOperation("新增课程信息")
    @PreAuthorize("hasAuthority('xc_teachmanager_course_add')")
    @PostMapping
    public CourseBaseInfoDto createCourse(@RequestBody @Validated(ValidationGroups.Insert.class) AddCourseDto addCourseDto){
        Long companyId = SecurityUtil.getUserCompanyID();
        if (companyId==null)
            companyId=0L;

        CourseBaseInfoDto ret = service.createCourseBase(companyId,addCourseDto);
        return ret;
    }
    @ApiOperation("查询课程信息")
    @GetMapping("/{id}")
    public CourseBaseInfoDto GetByID(@PathVariable("id") Long id){

        return service.getCourseBaseInfo(id);
    }
    @ApiOperation("修改课程信息")
    @PutMapping
    public CourseBaseInfoDto updateCourse(@RequestBody @Validated(ValidationGroups.Update.class) EditCourseDto dto){
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        Long companyId = Long.parseLong(user.getCompanyId());
        CourseBaseInfoDto ret = service.updateCourse(companyId,dto);
        return ret;
    }
    @ApiOperation("删除课程")
    @DeleteMapping("/{courseId}")
    public void deleteCourse(@PathVariable Long courseId){
       service.deleteCourseByid(courseId);
       return;
    }

}
