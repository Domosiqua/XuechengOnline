package com.cwb.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cwb.content.model.domain.CourseCategory;
import com.cwb.content.service.CourseCategoryService;
import com.cwb.content.mapper.CourseCategoryMapper;
import cwb.content.model.dto.CourseCategoryTreeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @author admin
* @description 针对表【course_category(课程分类)】的数据库操作Service实现
* @createDate 2023-08-08 19:03:42
*/
@Service
public class CourseCategoryServiceImpl extends ServiceImpl<CourseCategoryMapper, CourseCategory>
    implements CourseCategoryService{

    @Autowired
    CourseCategoryMapper categoryMapper;

    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
        List<CourseCategoryTreeDto> ret=new ArrayList<>();
        List<CourseCategory> list = this.list();
        LambdaQueryWrapper<CourseCategory> wrapper=new LambdaQueryWrapper();
        wrapper.eq(CourseCategory::getParentid,id);
        List<CourseCategory> tmp=new ArrayList<>();
        for(CourseCategory x:list){
            if(id.equals(x.getId()))
                tmp.add(x);
        }
        CourseCategoryTreeDto courseCategoryTreeDto = new CourseCategoryTreeDto();
        courseCategoryTreeDto.getChildrenTreeNodes().addAll(tmp);

        ret.add(courseCategoryTreeDto);
        return ret;

    }
}




