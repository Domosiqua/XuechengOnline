package com.cwb.learning.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.cwb.base.model.RestResponse;
import com.cwb.base.utils.StringUtil;
import com.cwb.content.model.domain.CoursePublish;
import com.cwb.content.model.domain.Teachplan;
import com.cwb.learning.feignclient.ContentServiceClient;
import com.cwb.learning.feignclient.MediaServiceClient;
import com.cwb.learning.model.dto.XcCourseTablesDto;
import com.cwb.learning.service.LearningService;
import com.cwb.learning.service.MyCourseTablesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author CWB
 * @version 1.0
 * @Date 2023/10/30 16:52
 */
@Slf4j
@Service
public class LearningServiceImpl implements LearningService {

    @Autowired
    MyCourseTablesService myCourseTablesService;
    @Autowired
    ContentServiceClient contentServiceClient;
    @Autowired
    MediaServiceClient mediaServiceClient;
    @Override
    public RestResponse<String> getVideo(String userId, Long courseId, Long teachplanId, String mediaId) {
        CoursePublish coursepublish = contentServiceClient.getCoursepublish(courseId);
        if (coursepublish==null)
            return RestResponse.validfail("课程不存在");

        String teachplan = coursepublish.getTeachplan();
        List<Teachplan> teachplans = JSON.parseArray(teachplan,Teachplan.class);
        for (Teachplan teachplan1 : teachplans) {
            if (teachplan1.getId().equals(teachplanId)){
                if (teachplan1.getIsPreview().equals("1")){//支持试学
                    RestResponse<String> playUrlByMediaId = mediaServiceClient.getPlayUrlByMediaId(mediaId);
                    return playUrlByMediaId;
                }else{
                    return RestResponse.validfail("该课程暂不支持试学 请购买课程后学习");
                }
            }
        }

        if (StringUtil.isNotEmpty(userId)) {
            XcCourseTablesDto learnStatus = myCourseTablesService.getLearnStatus(userId, courseId);
            if (learnStatus.getLearnStatus().equals("702002")){
                return RestResponse.validfail("没有选课或选课后没有支付");
            }else if (learnStatus.getLearnStatus().equals("702003")){
                return RestResponse.validfail("已过期需要申请续期或重新支付");
            }else{
                RestResponse<String> playUrlByMediaId = mediaServiceClient.getPlayUrlByMediaId(mediaId);
                return playUrlByMediaId;
            }
        }else{
            String charge = coursepublish.getCharge();
            if ("201000".equals(charge)) { //免费
                RestResponse<String> playUrlByMediaId = mediaServiceClient.getPlayUrlByMediaId(mediaId);
                return playUrlByMediaId;
            }

        }



        return RestResponse.validfail("请登陆后选课学习");
    }
}
