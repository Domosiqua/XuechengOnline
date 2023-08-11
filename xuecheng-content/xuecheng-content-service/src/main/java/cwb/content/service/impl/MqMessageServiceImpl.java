package cwb.content.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cwb.content.model.domain.MqMessage;
import cwb.content.service.MqMessageService;
import cwb.content.mapper.MqMessageMapper;
import org.springframework.stereotype.Service;

/**
* @author admin
* @description 针对表【mq_message】的数据库操作Service实现
* @createDate 2023-08-08 19:03:42
*/
@Service
public class MqMessageServiceImpl extends ServiceImpl<MqMessageMapper, MqMessage>
    implements MqMessageService{

}




