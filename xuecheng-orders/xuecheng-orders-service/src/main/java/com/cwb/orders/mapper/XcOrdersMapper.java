package com.cwb.orders.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cwb.orders.model.po.XcOrders;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author itcast
 */
@Mapper
@Component
public interface XcOrdersMapper extends BaseMapper<XcOrders> {

}
