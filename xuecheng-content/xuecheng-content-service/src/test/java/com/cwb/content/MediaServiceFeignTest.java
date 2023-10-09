package com.cwb.content;

import com.cwb.content.config.MultipartSupportConfig;
import com.cwb.content.feignClient.MediaServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @author CWB
 * @version 1.0
 */
@SuppressWarnings({"all"})
@SpringBootTest
public class MediaServiceFeignTest {

    @Autowired
    MediaServiceClient client;

    @Test
    public void test() throws IOException {
        File file = new File("D:\\XC\\test.html");
        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);
        client.upload(multipartFile, "course/2.html");

    }
}
