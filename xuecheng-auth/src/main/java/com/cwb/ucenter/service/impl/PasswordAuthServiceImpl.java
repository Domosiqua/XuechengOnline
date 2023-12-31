package com.cwb.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cwb.ucenter.mapper.XcUserMapper;
import com.cwb.ucenter.model.dto.AuthParamsDto;
import com.cwb.ucenter.model.dto.XcUserExt;
import com.cwb.ucenter.model.po.XcUser;
import com.cwb.ucenter.service.AuthService;
import com.cwb.ucenter.service.feignclient.CheckCodeClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author CWB
 * @version 1.0
 */
@SuppressWarnings({"all"})
@Service("password_authService")
@Slf4j
public class PasswordAuthServiceImpl implements AuthService {
    @Autowired
    XcUserMapper xcUserMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    CheckCodeClient checkCodeClient;


    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {

        Boolean verify = checkCodeClient.verify(authParamsDto.getCheckcodekey(), authParamsDto.getCheckcode());
        if(!verify){
            throw new RuntimeException("验证码错误");
        }
        //账号
        String username = authParamsDto.getUsername();
        XcUser user = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, username));
        if(user==null){
            //返回空表示用户不存在
            throw new RuntimeException("账号不存在");
        }
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(user,xcUserExt);
        //校验密码
        //取出数据库存储的正确密码
        String passwordDb  =user.getPassword();
        String passwordForm = authParamsDto.getPassword();
        boolean matches = passwordEncoder.matches(passwordForm, passwordDb);
        if(!matches){
            throw new RuntimeException("账号或密码错误");
        }
        return xcUserExt;
    }

}
