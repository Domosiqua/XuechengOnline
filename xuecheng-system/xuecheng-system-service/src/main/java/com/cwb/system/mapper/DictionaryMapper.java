package com.cwb.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cwb.system.model.po.Dictionary;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 数据字典 Mapper 接口
 * </p>
 *
 * @author cwb
 */
@Mapper
@Component
public interface DictionaryMapper extends BaseMapper<Dictionary> {

}
