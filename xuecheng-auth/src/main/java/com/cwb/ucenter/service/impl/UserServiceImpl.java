package com.cwb.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cwb.ucenter.mapper.XcMenuMapper;
import com.cwb.ucenter.mapper.XcUserMapper;
import com.cwb.ucenter.model.dto.AuthParamsDto;
import com.cwb.ucenter.model.dto.XcUserExt;
import com.cwb.ucenter.model.po.XcMenu;
import com.cwb.ucenter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.AutoPopulatingList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CWB
 * @version 1.0
 */
@SuppressWarnings({"all"})
@Component
@Slf4j
public class UserServiceImpl implements UserDetailsService {


    @Autowired
    XcUserMapper xcUserMapper;
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    XcMenuMapper xcMenuMapper;

    /**
     *
     * @param s username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        AuthParamsDto authParamsDto=null;
        try {
            authParamsDto = JSON.parseObject(s,AuthParamsDto.class);
        } catch (Exception e) {
            throw new RuntimeException("请求认证参数不符合要求");
        }

        String authType = authParamsDto.getAuthType();
        AuthService authService = applicationContext.getBean(authType + "_authService", AuthService.class);
        XcUserExt execute = authService.execute(authParamsDto);
        UserDetails userDetails = getUserPrincipal(execute);
        return userDetails;
    }
    /**
     * @description 查询用户信息
     * @param user  用户id，主键
     * @return com.cwb.ucenter.model.po.XcUser 用户信息
     * @author CWB
     * @date 2023/10/11 10:36
     */
    public UserDetails getUserPrincipal(XcUserExt user){
        //用户权限,如果不加报Cannot pass a null GrantedAuthority collection
        String[] authorities = xcMenuMapper.selectPermissionByUserId(user.getId());
        String password = user.getPassword();
        //为了安全在令牌中不放密码
        user.setPassword(null);
        //将user对象转json
        String userString = JSON.toJSONString(user);
        //创建UserDetails对象
        UserDetails userDetails = User.withUsername(userString).password(password ).authorities(authorities).build();
        return userDetails;
    }
}
