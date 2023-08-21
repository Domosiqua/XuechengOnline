package com.cwb.content.api;

import com.cwb.content.service.TeachplanService;
import cwb.content.model.dto.TeachplanDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author CWB
 * @version 1.0
 * 课程计划编辑接口
 */
@SuppressWarnings({"all"})
@Api(value = "课程计划编辑接口",tags = "课程计划编辑接口")
@RestController
@RequestMapping("/teachplan")
public class TeachplanController {

    @Autowired
    TeachplanService service;

    @ApiOperation("查询课程计划树形结构")
    @ApiImplicitParam(value = "courseId",name = "课程Id",required = true,dataType = "Long",paramType = "path")
    @GetMapping("/{courseId}/tree-nodes")
    public List<TeachplanDto> getTreeNodes(@PathVariable Long courseId){

        List<TeachplanDto> ret = service.getTreeNodes(courseId);

        return ret;
    }
}
