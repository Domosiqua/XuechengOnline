package com.cwb.ucenter.service;

import com.cwb.ucenter.model.po.XcUser;

/**
 * @author CWB
 * @version 1.0
 */
@SuppressWarnings({"all"})
public interface WxAuthService {
    public XcUser wxAuth(String code);
}
