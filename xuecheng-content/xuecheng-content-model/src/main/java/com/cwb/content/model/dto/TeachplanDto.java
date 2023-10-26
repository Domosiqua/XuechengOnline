package com.cwb.content.model.dto;

import com.cwb.content.model.domain.Teachplan;
import com.cwb.content.model.domain.TeachplanMedia;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author CWB
 * @version 1.0
 * 课程计划树型结构DTO
 */
@SuppressWarnings({"all"})
@Data
@ToString
public class TeachplanDto extends Teachplan {

    //课程计划关联的媒资信息
    TeachplanMedia teachplanMedia;

    //子结点
    List<TeachplanDto> teachPlanTreeNodes;


}
