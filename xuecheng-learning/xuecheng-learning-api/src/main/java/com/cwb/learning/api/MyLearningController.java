package com.cwb.learning.api;

import com.cwb.base.exception.XcException;
import com.cwb.base.model.RestResponse;
import com.cwb.learning.model.dto.XcCourseTablesDto;
import com.cwb.learning.service.LearningService;
import com.cwb.learning.service.MyCourseTablesService;
import com.cwb.learning.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @version 1.0
 * @description 我的学习接口
 * @date 2023/10/25 15:39
 */
@Api(value = "学习过程管理接口", tags = "学习过程管理接口")
@Slf4j
@RestController
public class MyLearningController {

    @Autowired
    MyCourseTablesService myCourseTablesService;
    @Autowired
    LearningService learningService;


    @ApiOperation("获取视频")
    @GetMapping("/open/learn/getvideo/{courseId}/{teachplanId}/{mediaId}")
    public RestResponse<String> getvideo(@PathVariable("courseId") Long courseId, @PathVariable("teachplanId") Long teachplanId, @PathVariable("mediaId") String mediaId) {
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        String userId = null;
        if(user != null){
            userId = user.getId();
        }
        RestResponse<String> video = learningService.getVideo(userId, courseId, teachplanId, mediaId);



        return video;

    }

}
