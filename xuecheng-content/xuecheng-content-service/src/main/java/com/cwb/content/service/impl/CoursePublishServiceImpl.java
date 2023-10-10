package com.cwb.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cwb.base.exception.CommonError;
import com.cwb.base.exception.XcException;
import com.cwb.content.config.MultipartSupportConfig;
import com.cwb.content.feignClient.MediaServiceClient;
import com.cwb.content.mapper.*;
import com.cwb.content.service.CourseBaseService;
import com.cwb.content.service.CourseTeacherService;
import com.cwb.content.service.TeachplanService;
import com.cwb.messagesdk.model.po.MqMessage;
import com.cwb.messagesdk.service.MqMessageService;
import cwb.content.model.domain.*;
import com.cwb.content.service.CoursePublishService;
import cwb.content.model.dto.CourseBaseInfoDto;
import cwb.content.model.dto.CoursePreviewDto;
import cwb.content.model.dto.TeachplanDto;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author admin
* @description 针对表【course_publish(课程发布)】的数据库操作Service实现
* @createDate 2023-08-08 19:03:42
*/
@Service
@Slf4j
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
    @Autowired
    MediaServiceClient mediaServiceClient;


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
        MqMessage course_publish = mqMessageService.addMessage("course_publish", String.valueOf(courseId), null, null);

        if (course_publish==null){
            XcException.cast(CommonError.UNKOWN_ERROR);
        }
        return;
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

    @Override
    public File generateCourseHtml(Long courseId){

        //配置freemarker
        Configuration configuration = new Configuration(Configuration.getVersion());

        //加载模板
        //选指定模板路径,classpath下templates下
        //得到classpath路径
        File htmlFile=null;
        try {
            String classpath = this.getClass().getResource("/").getPath();
            configuration.setDirectoryForTemplateLoading(new File(classpath + "/templates/"));
            //设置字符编码
            configuration.setDefaultEncoding("utf-8");

            //指定模板文件名称
            Template template = configuration.getTemplate("course_template.ftl");

            //准备数据
            CoursePreviewDto coursePreviewInfo = courseBaseService.getbasemodel(courseId);

            Map<String, Object> map = new HashMap<>();
            map.put("model", coursePreviewInfo);

            //静态化
            //参数1：模板，参数2：数据模型
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
            System.out.println(content);
            //将静态化内容输出到文件中
            InputStream inputStream = IOUtils.toInputStream(content,"utf-8");
            //输出流
            htmlFile=File.createTempFile(courseId.toString()+"my_temp_file_prefix",".html");
            FileOutputStream outputStream = new FileOutputStream(htmlFile);
            IOUtils.copy(inputStream, outputStream);
        } catch (Exception e) {
            log.error("页面静态化出现问题 课程ID: {}",courseId);
            e.printStackTrace();
        }
        return htmlFile;
    }



    @Override
    public void uploadCourseHtml(Long courseId, File file) {

        try {
            MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);
            String upload = mediaServiceClient.upload(multipartFile, "course/" + courseId + ".html");

            if (StringUtils.isEmpty(upload)){
                log.error("上传失败 走降级逻辑得到NULL 课程id:{}",courseId);
                XcException.cast("上传静态资源过程出现问题");
            }
        } catch (IOException e) {
            log.error("上传失败 课程id:{}",courseId);
            e.printStackTrace();
            XcException.cast("上传静态资源过程出现问题");
        }
    }



}




