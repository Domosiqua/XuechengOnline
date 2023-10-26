package com.cwb.content.mapper;

import com.cwb.content.model.domain.CourseMarket;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
* @author admin
* @description 针对表【course_market(课程营销信息)】的数据库操作Mapper
* @createDate 2023-08-08 19:03:42
* @Entity generator.domain.CourseMarket
*/
@Mapper
@Component
public interface CourseMarketMapper extends BaseMapper<CourseMarket> {

}




