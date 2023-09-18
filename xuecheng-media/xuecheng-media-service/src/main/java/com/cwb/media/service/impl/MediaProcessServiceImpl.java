package com.cwb.media.service.impl;

import com.cwb.media.mapper.MediaProcessMapper;
import com.cwb.media.model.po.MediaProcess;
import com.cwb.media.service.MediaProcessService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author CWB
 * @version 1.0
 */
@SuppressWarnings({"all"})
public class MediaProcessServiceImpl implements MediaProcessService {
    @Autowired
    MediaProcessMapper mediaProcessMapper;

    public List<MediaProcess> getAwaitTask(int shardIndex,  int shardTotal,  int count){
        return mediaProcessMapper.getAwaitTask(shardIndex,shardTotal,count);
    }
}
