package com.cwb.media;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.*;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;

/**
 * @author CWB
 * @version 1.0
 */
@SuppressWarnings({"all"})

public class test {

    MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://192.168.88.128:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();

    @Test
    public void TestUpload() throws Exception{
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(".mp4");
        String mimetype="application/octet-stream";
        if (extensionMatch!=null)
            mimetype=extensionMatch.getMimeType();
        minioClient.uploadObject(
                UploadObjectArgs.builder()
                        .bucket("testbucket")
                        .object("001/yx.mp4")
                        .filename("C:\\Users\\admin\\Videos\\原神？启动！.mp4")
                        .contentType(mimetype)
                        .build());
    }
    @Test
    public void TestDelete() throws Exception{
        RemoveObjectArgs arg= RemoveObjectArgs.builder()
                .bucket("testbucket")
                .object("JJP.jpg")
                .build();
        minioClient.removeObject(arg);
    }
    @Test
    public void TestGet() throws Exception{
        GetObjectArgs args= GetObjectArgs.builder()
                            .bucket("testbucket")
                            .object("XJP.jpg")
                            .build();

        FilterInputStream inputStream = minioClient.getObject(args);
        FileOutputStream outputStream = new FileOutputStream(new File("D:\\jjh.jpg"));
        IOUtils.copy(inputStream,outputStream);
    }
    @Test
    public void  getMimeType() {
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch("qewqas.avi");
        String mimetype="application/octet-stream";
        if (extensionMatch!=null)
            mimetype=extensionMatch.getMimeType();
        System.out.println(mimetype);
    }
}
