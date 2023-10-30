package com.cwb.learning.api;

import com.cwb.base.exception.XcException;
import com.cwb.base.model.PageResult;
import com.cwb.learning.model.dto.MyCourseTableParams;
import com.cwb.learning.model.dto.XcChooseCourseDto;
import com.cwb.learning.model.dto.XcCourseTablesDto;
import com.cwb.learning.model.po.XcCourseTables;
import com.cwb.learning.service.MyCourseTablesService;
import com.cwb.learning.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Security;

/**
 * @author Mr.M
 * @version 1.0
 * @description 我的课程表接口
 * @date 2022/10/25 9:40
 */

@Api(value = "我的课程表接口", tags = "我的课程表接口")
@Slf4j
@RestController
public class MyCourseTablesController {

    @Autowired
    MyCourseTablesService myCourseTablesService;

    @ApiOperation("添加选课")
    @PostMapping("/choosecourse/{courseId}")
    public XcChooseCourseDto addChooseCourse(@PathVariable("courseId") Long courseId) {

        SecurityUtil.XcUser user = SecurityUtil.getUser();
        if(user==null)
            XcException.cast("请登陆");
        String id = user.getId();
         XcChooseCourseDto xcChooseCourseDto = myCourseTablesService.addChooseCourse(id, courseId);
        return xcChooseCourseDto;
    }

    @ApiOperation("查询学习资格")
    @PostMapping("/choosecourse/learnstatus/{courseId}")
    public XcCourseTablesDto getLearnstatus(@PathVariable("courseId") Long courseId) {
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        if(user==null)
            XcException.cast("请登陆");
        String id = user.getId();
        XcCourseTablesDto learnStatus = myCourseTablesService.getLearnStatus(id, courseId);
        return learnStatus;

    }

    @ApiOperation("我的课程表")
    @GetMapping("/mycoursetable")
    public PageResult<XcCourseTables> mycoursetable(MyCourseTableParams params) {
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        String id = user.getId();
        params.setUserId(id);
        PageResult<XcCourseTables> ret = myCourseTablesService.getMyCourseTable(params);

        return ret;
    }

}
