package com.cwb.media.service.jobhandler;

import com.cwb.base.utils.Mp4VideoUtil;
import com.cwb.media.model.po.MediaProcess;
import com.cwb.media.service.MediaFileService;
import com.cwb.media.service.MediaProcessService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 视频处理工具类
 * @author CWB
 */
@Component
@Slf4j
public class VedioTaskXxlJob {
    private static final Logger logger = LoggerFactory.getLogger(VedioTaskXxlJob.class);


    @Autowired
    MediaFileService mediaFileService;

    @Autowired
    MediaProcessService mediaFileProcessService;
    @Value("${videoprocess.ffmpegpath}")
    String ffmpegpath;

    /**
     * 1、简单任务示例（Bean模式）
     */
    @XxlJob("测试")
    public void demoJobHandler() throws Exception {
        XxlJobHelper.log("XXL-JOB, Hello World.");

        for (int i = 0; i < 5; i++) {
            System.out.println("我是傻逼！！！！！！");
        }
        // default success
    }


    /**
     * 2、分片广播任务
     */

    @XxlJob("vedioJobHandler")
    public void vedioJobHandler() throws Exception {

        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        List<MediaProcess> mediaProcessList = null;
        int size=0;

        try {
            int processors = Runtime.getRuntime().availableProcessors();//处理器数量、
            mediaProcessList = mediaFileProcessService.getAwaitTask(shardIndex,shardTotal, processors);
            size = mediaProcessList.size();
            log.debug("取出待处理视频任务{}条", size);
            if (size <= 0) {
                return;
            }


        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        //启动size个线程的线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(size);
        //计数器
        CountDownLatch countDownLatch = new CountDownLatch(size);
        //将处理任务加入线程池
        mediaProcessList.forEach(mediaProcess -> {
            threadPool.execute(() -> {
                try {
                    //任务id
                    Long taskId = mediaProcess.getId();
                    //抢占任务
                    boolean b = mediaFileProcessService.startTask(taskId);
                    if (!b) {
                        return;
                    }
                    log.debug("开始执行任务:{}", mediaProcess);
                    //下边是处理逻辑
                    //桶
                    String bucket = mediaProcess.getBucket();
                    //存储路径
                    String filePath = mediaProcess.getFilePath();
                    //原始视频的md5值
                    String fileId = mediaProcess.getFileId();
                    //原始文件名称
                    String filename = mediaProcess.getFilename();
                    //将要处理的文件下载到服务器上
                    File originalFile = mediaFileService.downloadFileFromMinIO(bucket, filePath);
                    if (originalFile == null) {
                        log.debug("下载待处理文件失败,originalFile:{}", bucket.concat(filePath));
                        mediaFileProcessService.saveProcessFinishStatus(mediaProcess.getId(), "3", fileId, null, "下载待处理文件失败");
                        return;
                    }
                    //处理下载的视频文件
                    File mp4File = null;
                    try {
                        mp4File = File.createTempFile("mp4", ".mp4");
                    } catch (IOException e) {
                        log.error("创建mp4临时文件失败");
                        mediaFileProcessService.saveProcessFinishStatus(mediaProcess.getId(), "3", fileId, null, "创建mp4临时文件失败");
                        return;
                    }
                    //视频处理结果
                    String result = "";
                    try {
                        //开始处理视频
                        Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpegpath, originalFile.getAbsolutePath(), mp4File.getName(), mp4File.getAbsolutePath());
                        //开始视频转换，成功将返回success
                        result = videoUtil.generateMp4();
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("处理视频文件:{},出错:{}", mediaProcess.getFilePath(), e.getMessage());
                    }
                    if (!result.equals("success")) {
                        //记录错误信息
                        log.error("处理视频失败,视频地址:{},错误信息:{}", bucket + filePath, result);
                        mediaFileProcessService.saveProcessFinishStatus(mediaProcess.getId(), "3", fileId, null, result);
                        return;
                    }

                    //将mp4上传至minio
                    //mp4在minio的存储路径
                    String objectName = getFilePath(fileId, ".mp4");
                    //访问url
                    String url = "/" + bucket + "/" + objectName;
                    try {
                        mediaFileService.addFileTobucket(mp4File.getAbsolutePath(), "video/mp4", bucket, objectName);
                        //将url存储至数据，并更新状态为成功，并将待处理视频记录删除存入历史
                        mediaFileProcessService.saveProcessFinishStatus(mediaProcess.getId(), "2", fileId, url, null);
                    } catch (Exception e) {
                        log.error("上传视频失败或入库失败,视频地址:{},错误信息:{}", bucket + objectName, e.getMessage());
                        //最终还是失败了
                        mediaFileProcessService.saveProcessFinishStatus(mediaProcess.getId(), "3", fileId, null, "处理后视频上传或入库失败");
                    }
                }finally {
                    countDownLatch.countDown();
                }
            });
        });
        //等待,给一个充裕的超时时间,防止无限等待，到达超时时间还没有处理完成则结束任务
        countDownLatch.await(30, TimeUnit.MINUTES);
    }

    private String getFilePath(String fileMd5,String fileExt){
        return   fileMd5.substring(0,1) + "/" + fileMd5.substring(1,2) + "/" + fileMd5 + "/" +fileMd5 +fileExt;
    }

}
//
//
//    /**
//     * 3、命令行任务
//     */
//    @XxlJob("commandJobHandler")
//    public void commandJobHandler() throws Exception {
//        String command = XxlJobHelper.getJobParam();
//        int exitValue = -1;
//
//        BufferedReader bufferedReader = null;
//        try {
//            // command process
//            ProcessBuilder processBuilder = new ProcessBuilder();
//            processBuilder.command(command);
//            processBuilder.redirectErrorStream(true);
//
//            Process process = processBuilder.start();
//            //Process process = Runtime.getRuntime().exec(command);
//
//            BufferedInputStream bufferedInputStream = new BufferedInputStream(process.getInputStream());
//            bufferedReader = new BufferedReader(new InputStreamReader(bufferedInputStream));
//
//            // command log
//            String line;
//            while ((line = bufferedReader.readLine()) != null) {
//                XxlJobHelper.log(line);
//            }
//
//            // command exit
//            process.waitFor();
//            exitValue = process.exitValue();
//        } catch (Exception e) {
//            XxlJobHelper.log(e);
//        } finally {
//            if (bufferedReader != null) {
//                bufferedReader.close();
//            }
//        }
//
//        if (exitValue == 0) {
//            // default success
//        } else {
//            XxlJobHelper.handleFail("command exit value("+exitValue+") is failed");
//        }
//
//    }
//
//
//    /**
//     * 4、跨平台Http任务
//     *  参数示例：
//     *      "url: http://www.baidu.com\n" +
//     *      "method: get\n" +
//     *      "data: content\n";
//     */
//    @XxlJob("httpJobHandler")
//    public void httpJobHandler() throws Exception {
//
//        // param parse
//        String param = XxlJobHelper.getJobParam();
//        if (param==null || param.trim().length()==0) {
//            XxlJobHelper.log("param["+ param +"] invalid.");
//
//            XxlJobHelper.handleFail();
//            return;
//        }
//
//        String[] httpParams = param.split("\n");
//        String url = null;
//        String method = null;
//        String data = null;
//        for (String httpParam: httpParams) {
//            if (httpParam.startsWith("url:")) {
//                url = httpParam.substring(httpParam.indexOf("url:") + 4).trim();
//            }
//            if (httpParam.startsWith("method:")) {
//                method = httpParam.substring(httpParam.indexOf("method:") + 7).trim().toUpperCase();
//            }
//            if (httpParam.startsWith("data:")) {
//                data = httpParam.substring(httpParam.indexOf("data:") + 5).trim();
//            }
//        }
//
//        // param valid
//        if (url==null || url.trim().length()==0) {
//            XxlJobHelper.log("url["+ url +"] invalid.");
//
//            XxlJobHelper.handleFail();
//            return;
//        }
//        if (method==null || !Arrays.asList("GET", "POST").contains(method)) {
//            XxlJobHelper.log("method["+ method +"] invalid.");
//
//            XxlJobHelper.handleFail();
//            return;
//        }
//        boolean isPostMethod = method.equals("POST");
//
//        // request
//        HttpURLConnection connection = null;
//        BufferedReader bufferedReader = null;
//        try {
//            // connection
//            URL realUrl = new URL(url);
//            connection = (HttpURLConnection) realUrl.openConnection();
//
//            // connection setting
//            connection.setRequestMethod(method);
//            connection.setDoOutput(isPostMethod);
//            connection.setDoInput(true);
//            connection.setUseCaches(false);
//            connection.setReadTimeout(5 * 1000);
//            connection.setConnectTimeout(3 * 1000);
//            connection.setRequestProperty("connection", "Keep-Alive");
//            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
//            connection.setRequestProperty("Accept-Charset", "application/json;charset=UTF-8");
//
//            // do connection
//            connection.connect();
//
//            // data
//            if (isPostMethod && data!=null && data.trim().length()>0) {
//                DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
//                dataOutputStream.write(data.getBytes("UTF-8"));
//                dataOutputStream.flush();
//                dataOutputStream.close();
//            }
//
//            // valid StatusCode
//            int statusCode = connection.getResponseCode();
//            if (statusCode != 200) {
//                throw new RuntimeException("Http Request StatusCode(" + statusCode + ") Invalid.");
//            }
//
//            // result
//            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
//            StringBuilder result = new StringBuilder();
//            String line;
//            while ((line = bufferedReader.readLine()) != null) {
//                result.append(line);
//            }
//            String responseMsg = result.toString();
//
//            XxlJobHelper.log(responseMsg);
//
//            return;
//        } catch (Exception e) {
//            XxlJobHelper.log(e);
//
//            XxlJobHelper.handleFail();
//            return;
//        } finally {
//            try {
//                if (bufferedReader != null) {
//                    bufferedReader.close();
//                }
//                if (connection != null) {
//                    connection.disconnect();
//                }
//            } catch (Exception e2) {
//                XxlJobHelper.log(e2);
//            }
//        }
//
//    }
//
//    /**
//     * 5、生命周期任务示例：任务初始化与销毁时，支持自定义相关逻辑；
//     */
//    @XxlJob(value = "demoJobHandler2", init = "init", destroy = "destroy")
//    public void demoJobHandler2() throws Exception {
//        XxlJobHelper.log("XXL-JOB, Hello World.");
//    }
//    public void init(){
//        logger.info("init");
//    }
//    public void destroy(){
//        logger.info("destroy");
//    }



