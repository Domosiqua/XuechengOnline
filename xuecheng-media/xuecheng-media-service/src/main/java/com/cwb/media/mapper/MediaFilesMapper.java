package com.cwb.media.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cwb.media.model.po.MediaFiles;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 媒资信息 Mapper 接口
 * </p>
 *
 * @author itcast
 */
@Mapper
@Component
public interface MediaFilesMapper extends BaseMapper<MediaFiles> {

}
