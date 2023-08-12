package com.cwb.content.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cwb.content.mapper.MqMessageHistoryMapper;
import cwb.content.model.domain.MqMessageHistory;
import com.cwb.content.service.MqMessageHistoryService;
import org.springframework.stereotype.Service;

/**
* @author admin
* @description 针对表【mq_message_history】的数据库操作Service实现
* @createDate 2023-08-08 19:03:42
*/
@Service
public class MqMessageHistoryServiceImpl extends ServiceImpl<MqMessageHistoryMapper, MqMessageHistory>
    implements MqMessageHistoryService{

}




