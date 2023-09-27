package com.cwb.content.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cwb.content.mapper.CourseBaseMapper;
import com.cwb.content.mapper.TeachplanMapper;
import com.cwb.content.service.CourseBaseService;
import com.cwb.content.service.TeachplanService;
import cwb.content.model.domain.CoursePublish;
import com.cwb.content.service.CoursePublishService;
import com.cwb.content.mapper.CoursePublishMapper;
import cwb.content.model.domain.Teachplan;
import cwb.content.model.dto.CourseBaseInfoDto;
import cwb.content.model.dto.CoursePreviewDto;
import cwb.content.model.dto.TeachplanDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    CourseBaseService courseBaseService;
    @Autowired
    TeachplanMapper teachplanMapper;

    @Override
    public CoursePreviewDto getbasemodel(Long courseId) {
        CoursePreviewDto ret=new CoursePreviewDto();
        List<TeachplanDto> teachplanDtos = teachplanMapper.selectTreeNodes(courseId);
        ret.setTeachplans(teachplanDtos);
        CourseBaseInfoDto courseBaseInfo = courseBaseService.getCourseBaseInfo(courseId);
        ret.setCourseBase(courseBaseInfo);
        return ret;
    }
}




