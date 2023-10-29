package com.cwb.learning.service;

import com.cwb.content.model.domain.CoursePublish;
import com.cwb.learning.model.dto.XcChooseCourseDto;
import com.cwb.learning.model.dto.XcCourseTablesDto;
import com.cwb.learning.model.po.XcChooseCourse;
import com.cwb.learning.model.po.XcCourseTables;

/**
 * @author CWB
 * @version 1.0
 */
@SuppressWarnings({"all"})
public interface MyCourseTablesService {
    /**
     *
     * @param id 用户Id
     * @param courseId 课程Id
     * @return
     */
    XcChooseCourseDto addChooseCourse(String id,Long courseId);

    XcChooseCourse addFreeCoruse(String userid, CoursePublish coursepublish);

    XcCourseTables addCourseTabls(XcChooseCourse xcChooseCourse);

    XcCourseTables getXcCourseTables(String userId,Long courseId);

    XcChooseCourse addChargeCoruse(String userId,CoursePublish coursepublish);

    XcCourseTablesDto getLearnStatus(String userId, Long CourseId);

    boolean saveChooseCourseStauts(String choosecourseId);
}
