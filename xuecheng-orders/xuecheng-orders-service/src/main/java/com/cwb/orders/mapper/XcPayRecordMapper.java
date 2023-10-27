package com.cwb.orders.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cwb.orders.model.po.XcPayRecord;
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
public interface XcPayRecordMapper extends BaseMapper<XcPayRecord> {

}
