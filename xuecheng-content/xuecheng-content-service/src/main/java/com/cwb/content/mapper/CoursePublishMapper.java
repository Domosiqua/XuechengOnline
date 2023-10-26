package com.cwb.content.mapper;

import com.cwb.content.model.domain.CoursePublish;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
* @author admin
* @description 针对表【course_publish(课程发布)】的数据库操作Mapper
* @createDate 2023-08-08 19:03:42
* @Entity generator.domain.CoursePublish
*/
@Mapper
@Component
public interface CoursePublishMapper extends BaseMapper<CoursePublish> {

}




