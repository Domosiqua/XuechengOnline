package com.cwb.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cwb.base.exception.XcException;
import com.cwb.content.mapper.*;
import com.cwb.content.service.CourseBaseService;
import com.cwb.content.service.CourseTeacherService;
import com.cwb.content.service.TeachplanService;
import com.cwb.messagesdk.service.MqMessageService;
import cwb.content.model.domain.*;
import com.cwb.content.service.CoursePublishService;
import cwb.content.model.dto.CourseBaseInfoDto;
import cwb.content.model.dto.TeachplanDto;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author admin
* @description 针对表【course_publish(课程发布)】的数据库操作Service实现
* @createDate 2023-08-08 19:03:42
*/
@Service
public class CoursePublishServiceImpl extends ServiceImpl<CoursePublishMapper, CoursePublish>
    implements CoursePublishService{
    @Autowired
    CourseBaseMapper courseBaseMapper;
    @Autowired
    TeachplanService teachplanService;
    @Autowired
    CoursePublishMapper coursePublishMapper;
    @Autowired
    CourseBaseService courseBaseService;
    @Autowired
    CourseMarketMapper courseMarketMapper;
    @Autowired
    CourseTeacherMapper courseTeacherMapper;
    @Autowired
    CourseTeacherService courseTeacherService;
    @Autowired
    CoursePublishPreMapper coursePublishPreMapper;
    @Autowired
    MqMessageService mqMessageService;


    @Override
    @Transactional
    public void commit(Long courseId, Long companyId) {
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        String auditStatus = courseBase.getAuditStatus();
        if("202003".equals(auditStatus)){
            XcException.cast("当前为等待审核状态，审核完成可以再次提交");
        }
        //本机构只允许提交本机构的课程
        if(!courseBase.getCompanyId().equals(companyId)){
            XcException.cast("不允许提交其它机构的课程。");
        }
        //课程图片是否填写
        if(StringUtils.isEmpty(courseBase.getPic())){
            XcException.cast("提交失败，请上传课程图片");
        }
        CoursePublishPre coursePublish=new CoursePublishPre();
        CourseBaseInfoDto courseBaseInfo = courseBaseService.getCourseBaseInfo(courseId);
        BeanUtils.copyProperties(courseBaseInfo,coursePublish);
        List<TeachplanDto> teachplansTreenode = teachplanService.getTreeNodes(courseId);
        if (teachplansTreenode.size()<1){
            XcException.cast("不存在课程计划无法提交");
        }
        String teachplanJSON = JSON.toJSONString(teachplansTreenode);
        coursePublish.setTeachplan(teachplanJSON);
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        String courseMarketJSON = JSON.toJSONString(courseMarket);
        coursePublish.setMarket(courseMarketJSON);
        List<CourseTeacher> list = courseTeacherService.list(new LambdaQueryWrapper<CourseTeacher>().eq(CourseTeacher::getCourseId, courseId));
        String teacherListJSON = JSON.toJSONString(list);
        coursePublish.setTeachers(teacherListJSON);
        //设置预发布记录状态,已提交
        coursePublish.setStatus("202003");
        //教学机构id
        coursePublish.setCompanyId(companyId);
        CoursePublishPre coursePublishPreUpdate = coursePublishPreMapper.selectById(courseId);
        if(coursePublishPreUpdate == null){
            //添加课程预发布记录
            coursePublishPreMapper.insert(coursePublish);
        }else{
            coursePublishPreMapper.updateById(coursePublish);
        }
        //更新课程基本表的审核状态
        courseBase.setAuditStatus("202003");
        courseBaseMapper.updateById(courseBase);
        return;
    }

    @Override
    @Transactional
    public void publish(Long companyId, Long courseId) {
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPre == null) {
            XcException.cast("请先提交课程审核，审核通过才可以发布");
        }
        if(!coursePublishPre.getCompanyId().equals(companyId)){
            XcException.cast("不允许提交其它机构的课程。");
        }
        String auditStatus = coursePublishPre.getStatus();
        //审核通过方可发布
        if(!"202004".equals(auditStatus)){
            XcException.cast("操作失败，课程审核通过方可发布。");
        }
        saveCoursePublish(courseId);

        saveCoursePublishMessage(courseId);

        coursePublishPreMapper.deleteById(courseId);

        return ;
    }

    private void saveCoursePublishMessage(Long courseId) {
        mqMessageService.addMessage("course_publish",String.valueOf(courseId),null,null);
    }

    private void saveCoursePublish(Long courseId) {
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        CoursePublish coursePublish=new CoursePublish();
        BeanUtils.copyProperties(coursePublishPre,coursePublish);
        coursePublish.setStatus("203002");
        CoursePublish coursePublish1 = coursePublishMapper.selectById(courseId);
        if(coursePublish1==null){
            coursePublishMapper.insert(coursePublish);
        }else{
            coursePublishMapper.updateById(coursePublish);
        }
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        courseBase.setStatus("203002");
        courseBaseMapper.updateById(courseBase);
        return ;
    }

}




