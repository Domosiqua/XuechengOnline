package cwb.content.model.dto;

import cwb.content.model.domain.Teachplan;
import lombok.Data;

import java.util.List;

/**
 * @author CWB
 * @version 1.0
 */
@SuppressWarnings({"all"})
@Data
public class CoursePreviewDto {

    CourseBaseInfoDto courseBase;
    List<TeachplanDto> teachplans;

}
