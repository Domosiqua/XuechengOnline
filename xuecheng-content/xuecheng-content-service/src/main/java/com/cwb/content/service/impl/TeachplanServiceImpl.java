package com.cwb.content.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cwb.content.mapper.TeachplanMapper;
import cwb.content.model.domain.Teachplan;
import com.cwb.content.service.TeachplanService;
import cwb.content.model.dto.TeachplanDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author admin
* @description 针对表【teachplan(课程计划)】的数据库操作Service实现
* @createDate 2023-08-08 19:03:42
*/
@Service
public class TeachplanServiceImpl extends ServiceImpl<TeachplanMapper, Teachplan>
    implements TeachplanService{

    @Autowired
    TeachplanMapper mapper;

    @Override
    public List<TeachplanDto> getTreeNodes(Long courseId) {
        return mapper.selectTreeNodes(courseId);
    }
}




