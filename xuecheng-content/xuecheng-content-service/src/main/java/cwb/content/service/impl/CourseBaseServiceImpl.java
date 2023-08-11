package cwb.content.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cwb.content.model.domain.CourseBase;
import cwb.content.service.CourseBaseService;
import cwb.content.mapper.CourseBaseMapper;
import org.springframework.stereotype.Service;

/**
* @author admin
* @description 针对表【course_base(课程基本信息)】的数据库操作Service实现
* @createDate 2023-08-08 19:03:42
*/
@Service
public class CourseBaseServiceImpl extends ServiceImpl<CourseBaseMapper, CourseBase>
    implements CourseBaseService {

}




