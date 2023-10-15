package com.cwb.ucenter.service.impl;

import com.cwb.ucenter.model.dto.AuthParamsDto;
import com.cwb.ucenter.model.dto.XcUserExt;
import com.cwb.ucenter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author CWB
 * @version 1.0
 */
@SuppressWarnings({"all"})
@Service("wx_authService")
@Slf4j
public class WxAuthServiceImpl implements AuthService {
    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        return null;
    }
}
