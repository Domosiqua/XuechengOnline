package com.cwb.ucenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cwb.ucenter.model.po.XcUser;
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
public interface XcUserMapper extends BaseMapper<XcUser> {

}
