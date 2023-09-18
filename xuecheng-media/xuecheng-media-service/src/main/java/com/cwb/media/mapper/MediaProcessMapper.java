package com.cwb.media.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cwb.media.model.po.MediaProcess;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author itcast
 */
@Mapper
@Component
public interface MediaProcessMapper extends BaseMapper<MediaProcess> {

    List<MediaProcess> getAwaitTask(@Param("shardIndex")int shardIndex,@Param("shardTotal") int shardTotal,@Param("shardTotal") int count);
}
