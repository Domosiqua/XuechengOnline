package com.cwb.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cwb.base.exception.XcException;
import com.cwb.content.mapper.TeachplanMapper;
import com.cwb.content.mapper.TeachplanMediaMapper;
import cwb.content.model.domain.Teachplan;
import com.cwb.content.service.TeachplanService;
import cwb.content.model.domain.TeachplanMedia;
import cwb.content.model.dto.SaveTeachplanDto;
import cwb.content.model.dto.TeachplanDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Autowired
    TeachplanMediaMapper mediaMapper;

    @Override
    public List<TeachplanDto> getTreeNodes(Long courseId) {
        return mapper.selectTreeNodes(courseId);
    }

    @Override
    @Transactional
    public void saveTeachplan(SaveTeachplanDto teachplan) {
        Long id=teachplan.getId();
        if(id!=null){
            Teachplan update = this.getById(id);
            BeanUtils.copyProperties(teachplan,update);
            mapper.updateById(update);
        }else{
            Teachplan insert=new Teachplan();
            BeanUtils.copyProperties(teachplan,insert);
            int count = getTeachplanCount(teachplan.getCourseId(), teachplan.getParentid());
            insert.setOrderby(count+1);
            mapper.insert(insert);
        }

    }

    @Override
    @Transactional
    public void deleteTeachplan(Long id) {
        Teachplan byId = this.getById(id);
        if(byId.getParentid()==0){
            if(isHaveChild(id))
                XcException.cast("该章节还有子章节存在 无法删除");
            else
                mapper.deleteById(id);
        }else{
            LambdaQueryWrapper<TeachplanMedia> wrapper=new LambdaQueryWrapper<>();
            wrapper.eq(TeachplanMedia::getTeachplanId,id);
            mediaMapper.delete(wrapper);
            mapper.deleteById(id);
        }

    }

    @Override
    public void moveup(Long id) {
        Teachplan byId = this.getById(id);
        Teachplan last = mapper.getlast(byId.getOrderby(),byId.getParentid(),byId.getCourseId());
        if(last==null) {
            XcException.cast("该课程已经为最先 无法上移");
        }else{
            int tmp= last.getOrderby();
            last.setOrderby(byId.getOrderby());
            byId.setOrderby(tmp);
            mapper.updateById(last);
            mapper.updateById(byId);
        }
    }

    @Override
    public void movedown(Long id) {
        Teachplan byId = this.getById(id);
        Teachplan last = mapper.getnext(byId.getOrderby(),byId.getParentid(),byId.getCourseId());
        if(last==null) {
            XcException.cast("该课程已经为最后 无法下移");
        }else{
            int tmp= last.getOrderby();
            last.setOrderby(byId.getOrderby());
            byId.setOrderby(tmp);
            mapper.updateById(last);
            mapper.updateById(byId);
        }
    }

    private int getTeachplanCount(Long courseId, Long parentid) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId,courseId);
        queryWrapper.eq(Teachplan::getParentid,parentid);
        Integer count = mapper.selectCount(queryWrapper);
        return count;
    }
    public boolean isHaveChild(Long id){
        LambdaQueryWrapper<Teachplan> wrapper=new LambdaQueryWrapper();
        wrapper.eq(Teachplan::getParentid,id);
        List list = this.list(wrapper);
        if(list.isEmpty())
            return false;
        return true;
    }
}




