package com.cwb.content.service.jobhandler;

import com.cwb.content.feignClient.CourseIndex;
import com.cwb.content.feignClient.MediaServiceClient;
import com.cwb.content.feignClient.SearchServiceClient;
import com.cwb.content.service.CourseBaseService;
import com.cwb.content.service.CoursePublishService;
import com.cwb.messagesdk.model.po.MqMessage;
import com.cwb.messagesdk.service.MessageProcessAbstract;
import com.cwb.messagesdk.service.MqMessageService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import cwb.content.model.domain.CoursePublish;
import cwb.content.model.dto.CourseBaseInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * @author CWB
 * @version 1.0
 */
@SuppressWarnings({"all"})
@Component
@Slf4j
public class CoursePublishTask extends MessageProcessAbstract {



    @Autowired
    CoursePublishService coursePublishService;

    @Autowired
    SearchServiceClient searchServiceClient;

    public static final String Message_type="course_publish";

    @XxlJob("CoursePublishJobHandle")
    public void CoursePublishJobHandle() throws Exception{
        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();

        process(shardIndex,shardTotal,Message_type,30,60);

    }

    @Override
    public boolean execute(MqMessage mqMessage) {
        //获取消息相关的业务信息
        String businessKey1 = mqMessage.getBusinessKey1();
        long courseId = Integer.parseInt(businessKey1);
        //课程静态化
        generateCourseHtml(mqMessage,courseId);
        //课程索引
        saveCourseIndex(mqMessage,courseId);
        //课程缓存
        saveCourseCache(mqMessage,courseId);

        return true;
    }

    //生成课程静态化页面并上传至文件系统
    public void generateCourseHtml(MqMessage mqMessage,long courseId){

        log.debug("开始进行课程静态化,课程id:{}",courseId);
        //消息id
        Long id = mqMessage.getId();
        //消息处理的service
        MqMessageService mqMessageService = this.getMqMessageService();
        //消息幂等性处理
        int stageOne = mqMessageService.getStageOne(id);
        if(stageOne >0){
            log.debug("课程静态化已处理直接返回，课程id:{}",courseId);
            return ;
        }
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        File file = coursePublishService.generateCourseHtml(courseId);
        coursePublishService.uploadCourseHtml(courseId,file);


        //保存第一阶段状态
        mqMessageService.completedStageOne(id);

    }

    //将课程信息缓存至redis
    public void saveCourseCache(MqMessage mqMessage,long courseId){
        log.debug("将课程信息缓存至redis,课程id:{}",courseId);
        Long id = mqMessage.getId();
        //消息处理的service
        MqMessageService mqMessageService = this.getMqMessageService();
        int stageTwo = mqMessageService.getStageTwo(id);
        if(stageTwo>0){
            log.debug("课程缓存已处理直接返回，课程id:{}",courseId);
            return ;
        }
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        CoursePublish coursePublish = coursePublishService.getById(courseId);


        CourseIndex courseindex=new CourseIndex();
        BeanUtils.copyProperties(coursePublish,courseindex);
        searchServiceClient.add(courseindex);

        mqMessageService.completedStageTwo(id);

    }
    //保存课程索引信息
    public void saveCourseIndex(MqMessage mqMessage,long courseId){
        log.debug("保存课程索引信息,课程id:{}",courseId);
        Long id = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        int stageThree = mqMessageService.getStageThree(id);
        if(stageThree>0){
            log.debug("课程索引信息已处理直接返回，课程id:{}",courseId);
            return ;
        }
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        mqMessageService.completedStageThree(id);
    }


}
