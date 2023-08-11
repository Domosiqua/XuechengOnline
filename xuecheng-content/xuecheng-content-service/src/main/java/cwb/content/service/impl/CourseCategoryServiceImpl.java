package cwb.content.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cwb.content.model.domain.CourseCategory;
import cwb.content.service.CourseCategoryService;
import cwb.content.mapper.CourseCategoryMapper;
import org.springframework.stereotype.Service;

/**
* @author admin
* @description 针对表【course_category(课程分类)】的数据库操作Service实现
* @createDate 2023-08-08 19:03:42
*/
@Service
public class CourseCategoryServiceImpl extends ServiceImpl<CourseCategoryMapper, CourseCategory>
    implements CourseCategoryService{

}




