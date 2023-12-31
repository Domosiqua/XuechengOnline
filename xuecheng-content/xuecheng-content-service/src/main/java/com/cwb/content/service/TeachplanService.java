package com.cwb.content.service;

import com.cwb.content.model.domain.Teachplan;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cwb.content.model.domain.TeachplanMedia;
import com.cwb.content.model.dto.SaveTeachplanDto;
import com.cwb.content.model.dto.TeachplanBindMediaDto;
import com.cwb.content.model.dto.TeachplanDto;

import java.util.List;

/**
* @author admin
* @description 针对表【teachplan(课程计划)】的数据库操作Service
* @createDate 2023-08-08 19:03:42
*/
public interface TeachplanService extends IService<Teachplan> {

    List<TeachplanDto> getTreeNodes(Long courseId);

    void saveTeachplan(SaveTeachplanDto teachplan);

    void deleteTeachplan(Long id);

    void moveup(Long id);

    void movedown(Long id);

    TeachplanMedia BindMedia(TeachplanBindMediaDto teachplanBindMediaDto);

    void DeleteBindMedia(Long teachplanid, String mediaid);
}
