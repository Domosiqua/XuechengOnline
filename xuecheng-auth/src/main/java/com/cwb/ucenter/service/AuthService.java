package com.cwb.ucenter.service;

import com.cwb.ucenter.model.dto.AuthParamsDto;
import com.cwb.ucenter.model.dto.XcUserExt;
import com.cwb.ucenter.model.po.XcUser;

/**
 * @author CWB
 * @version 1.0
 */
@SuppressWarnings({"all"})
public interface AuthService {

    XcUserExt execute(AuthParamsDto authParamsDto);
}
