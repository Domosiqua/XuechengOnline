package cwb.content.model.dto;

import lombok.Data;
import lombok.ToString;

/**
 * @author CWB
 * @version 1.0
 * 课程查询参数Dto
 */
@SuppressWarnings({"all"})
@Data
@ToString
public class QueryCourseParamsDto {

    //审核状态
    private String auditStatus;
    //课程名称
    private String courseName;
    //发布状态
    private String publishStatus;

}