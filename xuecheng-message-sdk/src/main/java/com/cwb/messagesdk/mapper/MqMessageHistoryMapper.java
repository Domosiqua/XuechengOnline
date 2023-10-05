package com.cwb.messagesdk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cwb.messagesdk.model.po.MqMessageHistory;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author
 */
@Repository
@Mapper
public interface MqMessageHistoryMapper extends BaseMapper<MqMessageHistory> {

}
