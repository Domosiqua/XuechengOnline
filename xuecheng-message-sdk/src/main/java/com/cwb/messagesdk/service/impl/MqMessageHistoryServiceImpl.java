package com.cwb.messagesdk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.cwb.messagesdk.mapper.MqMessageHistoryMapper;
import com.cwb.messagesdk.model.po.MqMessageHistory;
import com.cwb.messagesdk.service.MqMessageHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author itcast
 */
@Slf4j
@Service
public class MqMessageHistoryServiceImpl extends ServiceImpl<MqMessageHistoryMapper, MqMessageHistory>
        implements MqMessageHistoryService {

}
