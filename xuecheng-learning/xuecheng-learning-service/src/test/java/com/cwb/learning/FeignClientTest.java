package com.cwb.learning;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.cwb.content.model.domain.CoursePublish;
import com.cwb.learning.feignclient.ContentServiceClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Mr.M
 * @version 1.0

 * @date 2023/2/22 20:14
 */
@SpringBootTest
public class FeignClientTest {

    @Autowired
    ContentServiceClient contentServiceClient;


    @Test
    public void testContentServiceClient() {
        CoursePublish coursepublish = contentServiceClient.getCoursepublish(1L);
        Assertions.assertNotNull(coursepublish);
        System.out.println(coursepublish);
    }
}