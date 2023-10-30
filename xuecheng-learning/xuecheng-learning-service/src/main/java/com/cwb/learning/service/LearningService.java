package com.cwb.learning.service;

import com.cwb.base.model.RestResponse;

/**
 * @author CWB
 * @version 1.0
 * @Date 2023/10/30 16:52
 */
public interface LearningService {
    public RestResponse<String> getVideo(String userId, Long courseId, Long teachplanId, String mediaId);
}
