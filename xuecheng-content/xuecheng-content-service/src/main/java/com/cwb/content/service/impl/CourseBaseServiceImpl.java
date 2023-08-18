package com.cwb.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cwb.base.exception.XcException;
import com.cwb.base.model.PageParams;
import com.cwb.base.model.PageResult;
import com.cwb.content.mapper.CourseBaseMapper;
import com.cwb.content.mapper.CourseCategoryMapper;
import com.cwb.content.mapper.CourseMarketMapper;
import com.cwb.content.service.CourseBaseService;
import cwb.content.model.domain.CourseBase;
import cwb.content.model.domain.CourseCategory;
import cwb.content.model.domain.CourseMarket;
import cwb.content.model.dto.AddCourseDto;
import cwb.content.model.dto.CourseBaseInfoDto;
import cwb.content.model.dto.QueryCourseParamsDto;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
* @author admin
* @description 针对表【course_base(课程基本信息)】的数据库操作Service实现
* @createDate 2023-08-08 19:03:42
*/
@Service
public class CourseBaseServiceImpl extends ServiceImpl<CourseBaseMapper, CourseBase>
    implements CourseBaseService {

    @Autowired
    CourseBaseMapper courseBaseMapper;
    @Autowired
    CourseMarketMapper courseMarketMapper;
    @Autowired
    CourseCategoryMapper courseCategoryMapper;


    @Override
    public PageResult<CourseBase> getPageConditionList(PageParams pageParams, QueryCourseParamsDto queryCourseParams) {

        //测试查询接口
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        //根据课程名称模糊查询  name like '%名称%'
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParams.getCourseName()),CourseBase::getName,queryCourseParams.getCourseName());
        //根据课程审核状态
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParams.getAuditStatus()),CourseBase::getAuditStatus,queryCourseParams.getAuditStatus());
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParams.getStatus()),CourseBase::getStatus,queryCourseParams.getStatus());

        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());

        //分页查询E page 分页参数, @Param("ew") Wrapper<T> queryWrapper 查询条件
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);

        //数据
        List<CourseBase> items = pageResult.getRecords();
        //总记录数
        long total = pageResult.getTotal();

        //准备返回数据 List<T> items, long counts, long page, long pageSize
        PageResult<CourseBase> courseBasePageResult = new PageResult<>(items, total, pageParams.getPageNo(), pageParams.getPageSize());
        return courseBasePageResult;
    }

    @Override
    @Transactional
    public CourseBaseInfoDto createCourseBase(Long companyId,AddCourseDto dto) {
        //合法性校验
        if (StringUtils.isBlank(dto.getName())) {
            XcException.cast("课程名称为空");

        }

        if (StringUtils.isBlank(dto.getMt())) {
            XcException.cast("课程分类为空");
        }

        if (StringUtils.isBlank(dto.getSt())) {
            XcException.cast("课程分类为空");
        }

        if (StringUtils.isBlank(dto.getGrade())) {

            XcException.cast("课程等级为空");
        }

        if (StringUtils.isBlank(dto.getTeachmode())) {

            XcException.cast("教育模式为空");
        }

        if (StringUtils.isBlank(dto.getUsers())) {

            XcException.cast("适应人群为空");
        }

        if (StringUtils.isBlank(dto.getCharge())) {

            XcException.cast("收费规则为空");
        }
        //新增对象
        CourseBase courseBaseNew = new CourseBase();
        //将填写的课程信息赋值给新增对象
        BeanUtils.copyProperties(dto,courseBaseNew);
        //设置审核状态
        courseBaseNew.setAuditStatus("202002");
        //设置发布状态
        courseBaseNew.setStatus("203001");
        //机构id
        courseBaseNew.setCompanyId(companyId);
        //添加时间
        courseBaseNew.setCreateDate(LocalDateTime.now());
        //插入课程基本信息表
        int insert = courseBaseMapper.insert(courseBaseNew);
        if(insert<=0){
            XcException.cast("新增课程基本信息失败");
        }
        //向课程营销表保存课程营销信息
        //课程营销信息
        CourseMarket courseMarketNew = new CourseMarket();
        Long courseId = courseBaseNew.getId();
        BeanUtils.copyProperties(dto,courseMarketNew);
        courseMarketNew.setId(courseId);
        int i = saveCourseMarket(courseMarketNew);
        if(i<=0){
            XcException.cast("保存课程营销信息失败");
        }
        //查询课程基本信息及营销信息并返回
        return getCourseBaseInfo(courseId);

    }

    private CourseBaseInfoDto getCourseBaseInfo(Long courseId) {
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(courseBase == null){
            return null;
        }
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase,courseBaseInfoDto);
        if(courseMarket != null){
            BeanUtils.copyProperties(courseMarket,courseBaseInfoDto);
        }

        //查询分类名称
        CourseCategory courseCategoryBySt = courseCategoryMapper.selectById(courseBase.getSt());
        courseBaseInfoDto.setStName(courseCategoryBySt.getName());
        CourseCategory courseCategoryByMt = courseCategoryMapper.selectById(courseBase.getMt());
        courseBaseInfoDto.setMtName(courseCategoryByMt.getName());
        return courseBaseInfoDto;
    }
    
    private int saveCourseMarket(CourseMarket courseMarketNew) {
        //收费规则
        String charge = courseMarketNew.getCharge();
        if(StringUtils.isBlank(charge)){
            XcException.cast("收费规则没有选择");
        }
        //收费规则为收费
        if(charge.equals("201001")){
            if(courseMarketNew.getPrice() == null || courseMarketNew.getPrice().floatValue()<=0){
                XcException.cast("课程为收费价格不能为空且必须大于0");
            }
        }
        //根据id从课程营销表查询
        CourseMarket courseMarketObj = courseMarketMapper.selectById(courseMarketNew.getId());
        if(courseMarketObj == null){
            return courseMarketMapper.insert(courseMarketNew);
        }else {
            BeanUtils.copyProperties(courseMarketNew, courseMarketObj);
            courseMarketObj.setId(courseMarketNew.getId());
            return courseMarketMapper.updateById(courseMarketObj);
        }
    }
}




