package com.cwb.learning.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cwb.base.exception.XcException;
import com.cwb.content.model.domain.CoursePublish;
import com.cwb.learning.feignclient.ContentServiceClient;
import com.cwb.learning.mapper.XcChooseCourseMapper;
import com.cwb.learning.mapper.XcCourseTablesMapper;
import com.cwb.learning.model.dto.XcChooseCourseDto;
import com.cwb.learning.model.dto.XcCourseTablesDto;
import com.cwb.learning.model.po.XcChooseCourse;
import com.cwb.learning.model.po.XcCourseTables;
import com.cwb.learning.service.MyCourseTablesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author CWB
 * @version 1.0
 * @Date 2023/10/25 17:13
 */
@Slf4j
@Service
public class MyCourseTablesServiceImpl implements MyCourseTablesService {

    @Autowired
    XcCourseTablesMapper xcCourseTablesMapper;
    @Autowired
    XcChooseCourseMapper xcChooseCourseMapper;
    @Autowired
    ContentServiceClient contentServiceClient;
    @Autowired
    MyCourseTablesService myCourseTablesService;


    @Override
    @Transactional
    public XcChooseCourseDto addChooseCourse(String userid, Long courseId) {

        CoursePublish coursepublish = contentServiceClient.getCoursepublish(courseId);
        if (coursepublish==null)
            XcException.cast("课程信息不存在");

        XcChooseCourse chooseCourse = null;
        if ("201000".equals(coursepublish.getCharge())){//201000免费 201001收费
            chooseCourse = myCourseTablesService.addFreeCoruse(userid, coursepublish);
            myCourseTablesService.addCourseTabls(chooseCourse);
        }else{
            chooseCourse = myCourseTablesService.addChargeCoruse(userid, coursepublish);
        }
        XcChooseCourseDto xcChooseCourseDto=new XcChooseCourseDto();
        BeanUtils.copyProperties(chooseCourse,xcChooseCourseDto);
        XcCourseTablesDto learnStatus = getLearnStatus(userid, courseId);
        xcChooseCourseDto.setLearnStatus(learnStatus.getLearnStatus());

        return xcChooseCourseDto;
    }
    //添加免费课程,免费课程加入选课记录表
    @Override
    public XcChooseCourse addFreeCoruse(String userId, CoursePublish coursepublish) {
        XcChooseCourse xcChooseCourses1 = xcChooseCourseMapper.selectOne(new LambdaQueryWrapper<XcChooseCourse>()
                .eq(XcChooseCourse::getCompanyId, coursepublish.getCompanyId())
                .eq(XcChooseCourse::getUserId, userId)
                .eq(XcChooseCourse::getOrderType, "700001")//免费课程
                .eq(XcChooseCourse::getStatus, "701001"));//选课成功

        if (xcChooseCourses1!=null)
            return xcChooseCourses1;

        XcChooseCourse xcChooseCourse=new XcChooseCourse();
        xcChooseCourse.setCompanyId(coursepublish.getCompanyId());
        xcChooseCourse.setCourseId(coursepublish.getId());
        xcChooseCourse.setCourseName(coursepublish.getName());
        xcChooseCourse.setUserId(userId);
        xcChooseCourse.setOrderType("700001");
        xcChooseCourse.setCoursePrice(0F);
        xcChooseCourse.setValidDays(coursepublish.getValidDays());
        xcChooseCourse.setValidtimeEnd(LocalDateTime.now().plusDays(coursepublish.getValidDays()));
        xcChooseCourse.setStatus("701001");
        xcChooseCourse.setValidtimeStart(LocalDateTime.now());
        xcChooseCourseMapper.insert(xcChooseCourse);
        return xcChooseCourse;
    }

    //添加收费课程
    @Override
    public XcChooseCourse addChargeCoruse(String userId,CoursePublish coursepublish){

        XcChooseCourse xcChooseCourses1 = xcChooseCourseMapper.selectOne(new LambdaQueryWrapper<XcChooseCourse>()
                .eq(XcChooseCourse::getCompanyId, coursepublish.getCompanyId())
                .eq(XcChooseCourse::getUserId, userId)
                .eq(XcChooseCourse::getOrderType, "700002")//收费课程
                .eq(XcChooseCourse::getStatus, "701002"));//待支付

        if(xcChooseCourses1!=null)
            return xcChooseCourses1;


        XcChooseCourse xcChooseCourse = new XcChooseCourse();
        xcChooseCourse.setCourseId(coursepublish.getId());
        xcChooseCourse.setCourseName(coursepublish.getName());

        xcChooseCourse.setCoursePrice(coursepublish.getPrice());
        xcChooseCourse.setUserId(userId);
        xcChooseCourse.setCompanyId(coursepublish.getCompanyId());
        xcChooseCourse.setOrderType("700002");//收费课程
        xcChooseCourse.setStatus("701002");//待支付
        xcChooseCourse.setValidDays(coursepublish.getValidDays());
        xcChooseCourse.setValidtimeStart(LocalDateTime.now());
        xcChooseCourse.setValidtimeEnd(LocalDateTime.now().plusDays(coursepublish.getValidDays()));
        int insert = xcChooseCourseMapper.insert(xcChooseCourse);
        if (insert<=0)
            XcException.cast("添加选课失败");
        return xcChooseCourse;

    }
    //添加到我的课程表
    @Override
    public XcCourseTables addCourseTabls(XcChooseCourse xcChooseCourse){
        String status = xcChooseCourse.getStatus();
        if(!"701001".equals(status))//非选课成功
            XcException.cast("未支付 无法添加至我的课程");
        XcCourseTables xcCourseTables1 = getXcCourseTables(xcChooseCourse.getUserId(), xcChooseCourse.getCourseId());
        if (xcCourseTables1!=null)
            return xcCourseTables1;

        XcCourseTables xcCourseTables=new XcCourseTables();
        BeanUtils.copyProperties(xcChooseCourse,xcCourseTables);
        xcCourseTables.setId(null);
        xcCourseTables.setChooseCourseId(xcChooseCourse.getId());
        xcCourseTables.setCourseType(xcChooseCourse.getOrderType());
        int insert = xcCourseTablesMapper.insert(xcCourseTables);
        if (insert<1)
            XcException.cast("添加到我的课程表失败");
        return xcCourseTables;
    }

    @Override
    public XcCourseTables getXcCourseTables(String userId, Long courseId) {
        XcCourseTables xcCourseTables = xcCourseTablesMapper.selectOne(new LambdaQueryWrapper<XcCourseTables>()
                .eq(XcCourseTables::getUserId, userId)
                .eq(XcCourseTables::getCourseId, courseId));
        return xcCourseTables;
    }

    /**
     * @description 判断学习资格
     * @param userId
     * @param courseId
     * @return XcCourseTablesDto 学习资格状态 [{"code":"702001","desc":"正常学习"},{"code":"702002","desc":"没有选课或选课后没有支付"},{"code":"702003","desc":"已过期需要申请续期或重新支付"}]
     */
    @Override
    public XcCourseTablesDto getLearnStatus(String userId,Long courseId){

        //查询我的课程表
        XcCourseTables xcCourseTables = getXcCourseTables(userId, courseId);
        if(xcCourseTables==null){
            XcCourseTablesDto xcCourseTablesDto = new XcCourseTablesDto();
            //没有选课或选课后没有支付
            xcCourseTablesDto.setLearnStatus("702002");
            return xcCourseTablesDto;
        }
        XcCourseTablesDto xcCourseTablesDto = new XcCourseTablesDto();
        BeanUtils.copyProperties(xcCourseTables,xcCourseTablesDto);
        //是否过期,true过期，false未过期
        boolean isExpires = xcCourseTables.getValidtimeEnd().isBefore(LocalDateTime.now());
        if(!isExpires){
            //正常学习
            xcCourseTablesDto.setLearnStatus("702001");
            return xcCourseTablesDto;

        }else{
            //已过期
            xcCourseTablesDto.setLearnStatus("702003");
            return xcCourseTablesDto;
        }

    }

    @Override
    @Transactional
    public boolean saveChooseCourseStauts(String choosecourseId) {
        XcChooseCourse xcChooseCourse = xcChooseCourseMapper.selectById(choosecourseId);
        if (xcChooseCourse==null){
            log.debug("接收到购买课程的消息,但找不到选课记录{}",choosecourseId);
            return false;
        }
        String status = xcChooseCourse.getStatus();
        if ("701002".equals(status)){//待支付
            xcChooseCourse.setStatus("701001");
            int i = xcChooseCourseMapper.updateById(xcChooseCourse);
            if(i<1){
                log.debug("添加选课记录失败{}",xcChooseCourse);
                XcException.cast("添加选课记录失败");
            }
            addCourseTabls(xcChooseCourse);
        }




        return true;
    }
}
