package com.cwb.media.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cwb.media.model.po.MediaProcess;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
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
    @Update("update media_process m set m.status='4' where  m.id=#{id} and (m.status='1'or m.status='3') and m.fail_count<3")
    int startTask(@Param("id") Long id);

}

