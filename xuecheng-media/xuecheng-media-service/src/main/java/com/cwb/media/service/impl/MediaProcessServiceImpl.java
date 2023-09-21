package com.cwb.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cwb.media.mapper.MediaFilesMapper;
import com.cwb.media.mapper.MediaProcessHistoryMapper;
import com.cwb.media.mapper.MediaProcessMapper;
import com.cwb.media.model.po.MediaFiles;
import com.cwb.media.model.po.MediaProcess;
import com.cwb.media.model.po.MediaProcessHistory;
import com.cwb.media.service.MediaProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author CWB
 * @version 1.0
 */
@SuppressWarnings({"all"})
@Slf4j
@Service
public class MediaProcessServiceImpl implements MediaProcessService {
    @Autowired
    MediaProcessMapper mediaProcessMapper;
    @Autowired
    MediaProcessHistoryMapper mediaProcessHistoryMapper;
    @Autowired
    MediaFilesMapper mediaFilesMapper;

    public List<MediaProcess> getAwaitTask(int shardIndex,  int shardTotal,  int count){
        return mediaProcessMapper.getAwaitTask(shardIndex,shardTotal,count);
    }

    public boolean startTask(Long id){
        return mediaProcessMapper.startTask(id)>0;
    }

    @Override
    @Transactional
    public void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg) {
        MediaProcess mediaProcess = mediaProcessMapper.selectById(taskId);
        if (mediaProcess==null){
            return;
        }
        LambdaQueryWrapper<MediaProcess> queryWrapperById = new LambdaQueryWrapper<MediaProcess>().eq(MediaProcess::getId, taskId);
        //处理失败
        if(status.equals("3")){
            MediaProcess mediaProcess_u = new MediaProcess();
            mediaProcess_u.setStatus("3");
            mediaProcess_u.setErrormsg(errorMsg);
            mediaProcess_u.setFailCount(mediaProcess.getFailCount()+1);
            mediaProcessMapper.update(mediaProcess_u,queryWrapperById);
            log.debug("更新任务处理状态为失败，任务信息:{}",mediaProcess_u);
            return ;
        }
        //任务处理成功
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileId);
        if(mediaFiles!=null){
            //更新媒资文件中的访问url
            mediaFiles.setUrl(url);
            mediaFilesMapper.updateById(mediaFiles);
        }
        //处理成功，更新url和状态
        mediaProcess.setUrl(url);
        mediaProcess.setStatus("2");
        mediaProcess.setFinishDate(LocalDateTime.now());
        mediaProcessMapper.updateById(mediaProcess);

        //添加到历史记录
        MediaProcessHistory mediaProcessHistory = new MediaProcessHistory();
        System.out.println(mediaProcess);
        BeanUtils.copyProperties(mediaProcess, mediaProcessHistory);
        System.out.println(mediaProcessHistory);
        mediaProcessHistoryMapper.insert(mediaProcessHistory);
        System.out.println("ok");
        //删除mediaProcess
        mediaProcessMapper.deleteById(mediaProcess.getId());
    }

}
