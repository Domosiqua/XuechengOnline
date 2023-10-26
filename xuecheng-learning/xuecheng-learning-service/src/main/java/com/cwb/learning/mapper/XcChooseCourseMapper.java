package com.cwb.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cwb.learning.model.po.XcChooseCourse;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author itcast
 */
@Mapper
@Component
public interface XcChooseCourseMapper extends BaseMapper<XcChooseCourse> {

}
