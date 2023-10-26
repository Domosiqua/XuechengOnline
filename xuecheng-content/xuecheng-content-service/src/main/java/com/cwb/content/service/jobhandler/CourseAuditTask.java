package com.cwb.content.service.jobhandler;

import com.cwb.content.mapper.CourseBaseMapper;
import com.cwb.content.mapper.CoursePublishPreMapper;
import com.cwb.content.model.domain.CourseBase;
import com.cwb.content.model.domain.CoursePublishPre;
import com.cwb.messagesdk.model.po.MqMessage;
import com.cwb.messagesdk.service.MessageProcessAbstract;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author CWB
 * @version 1.0
 * @Date 2023/10/26 15:51
 */
@Component
@Slf4j
public class CourseAuditTask extends MessageProcessAbstract {

    @Autowired
    CoursePublishPreMapper coursePublishPreMapper;
    @Autowired
    CourseBaseMapper courseBaseMapper;

    public static final String Message_type="course_audit";
    @XxlJob("CourseAuditJobHandle")
    public void CourseAuditJobHandle() throws Exception{
        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        process(shardIndex,shardTotal,Message_type,30,60);
    }

    @Override
    public boolean execute(MqMessage mqMessage) {
        String businessKey1 = mqMessage.getBusinessKey1();
        Long courseId=Long.parseLong(businessKey1);
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        coursePublishPre.setStatus("202004");
        coursePublishPreMapper.updateById(coursePublishPre);
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        courseBase.setAuditStatus("202004");
        courseBaseMapper.updateById(courseBase);
        return true;
    }
}
