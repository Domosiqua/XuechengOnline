package com.cwb.content;

import com.cwb.content.mapper.TeachplanMapper;
import cwb.content.model.domain.TeachplanMedia;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author CWB
 * @version 1.0
 */
@SuppressWarnings({"all"})
@SpringBootTest
public class test {
    @Autowired
    TeachplanMapper mapper;

    @Test
    public void test(){
        Integer maxOrderby = mapper.getMaxOrderby(266L, 117L);
        System.out.println(maxOrderby);
        System.out.println(maxOrderby);
        System.out.println(maxOrderby);
        System.out.println(maxOrderby);

    }
}
