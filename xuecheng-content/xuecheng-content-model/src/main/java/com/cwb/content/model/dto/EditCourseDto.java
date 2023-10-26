package com.cwb.content.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author CWB
 * @version 1.0
 * 添加课程DTO
 */
@SuppressWarnings({"all"})
@Data
public class EditCourseDto extends AddCourseDto{
    @ApiModelProperty(value = "课程id", required = true)
    private Long id;
    @ApiModelProperty(value = "机构id", required = true)
    private Long companyId;

}
