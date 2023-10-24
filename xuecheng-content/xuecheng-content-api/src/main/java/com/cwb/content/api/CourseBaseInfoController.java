package com.cwb.content.api;

import com.cwb.base.exception.ValidationGroups;
import com.cwb.base.model.PageParams;
import com.cwb.base.model.PageResult;
import com.cwb.content.service.CourseMarketService;
import com.cwb.content.util.SecurityUtil;
import cwb.content.model.domain.CourseBase;
import cwb.content.model.domain.CourseMarket;
import cwb.content.model.dto.*;
import com.cwb.content.service.CourseBaseService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/whole/{courseId}")
    public CoursePreviewDto getPreviewInfo(@PathVariable("courseId") Long courseId) {
        //获取课程预览信息
        CoursePreviewDto coursePreviewInfo = service.getbasemodel(courseId);
        return coursePreviewInfo;
    }

    @ApiOperation("课程查询接口")
    @PreAuthorize("hasAuthority('xc_teachmanager_course_list')")
    @PostMapping("/list")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required = false) QueryCourseParamsDto queryCourseParams){
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        System.out.println(user);
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
        Long companyId = 1232141425L;
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        String companyId1 = user.getCompanyId();
        if (StringUtils.isNotEmpty(companyId1))
            companyId=Long.parseLong(companyId1);
        else
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
        Long companyId = 1232141425L;
        CourseBaseInfoDto ret = service.updateCourse(companyId,dto);
        return ret;
    }
    @ApiOperation("修改课程信息")
    @DeleteMapping("/{courseId}")
    public void deleteCourse(@PathVariable Long courseId){
       service.deleteCourseByid(courseId);
       return;
    }

}
