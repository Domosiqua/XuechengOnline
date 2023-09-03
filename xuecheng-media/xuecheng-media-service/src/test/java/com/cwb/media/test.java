package com.cwb.media;

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
        boolean found =
                minioClient.bucketExists(BucketExistsArgs.builder().bucket("testbucket").build());
        if (!found) {
            // Make a new bucket called 'asiatrip'.
            minioClient.makeBucket(MakeBucketArgs.builder().bucket("testbucket").build());
        } else {
            System.out.println("Bucket 'testbucket' already exists.");
        }
        minioClient.uploadObject(
                UploadObjectArgs.builder()
                        .bucket("testbucket")
                        .object("JJP.jpg")
//                        .object("001/XJP.jpg")
                        .filename("C:\\Users\\admin\\Pictures\\Saved Pictures\\XJP.jpg")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE)
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
        minioClient.getObject(args);
        FilterInputStream inputStream = minioClient.getObject(args);
        FileOutputStream outputStream = new FileOutputStream(new File("D:\\jjh.jpg"));
        IOUtils.copy(inputStream,outputStream);



    }
}
