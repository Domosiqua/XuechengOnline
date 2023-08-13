package cwb.content.model.dto;

import cwb.content.model.domain.CourseCategory;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author CWB
 * @version 1.0
 */
@SuppressWarnings({"all"})
@Data
@ToString
public class CourseCategoryTreeDto extends CourseCategory implements Serializable {


    List<CourseCategory> childrenTreeNodes;
}
