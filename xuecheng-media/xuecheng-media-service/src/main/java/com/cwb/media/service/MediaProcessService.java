package com.cwb.media.service;

import com.cwb.media.model.po.MediaProcess;

import java.util.List;

/**
 * @author CWB
 * @version 1.0
 */
@SuppressWarnings({"all"})
public interface MediaProcessService {
    List<MediaProcess> getAwaitTask(int shardIndex, int shardTotal, int count);
}
