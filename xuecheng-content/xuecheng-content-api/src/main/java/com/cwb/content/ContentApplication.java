package com.cwb.content;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author CWB
 * @version 1.0
 */
@SuppressWarnings({"all"})
@SpringBootApplication
@EnableSwagger2Doc
@MapperScan("com.cwb.content.mapper")
@EnableTransactionManagement
public class ContentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContentApplication.class, args);
    }
}
