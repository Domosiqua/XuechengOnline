package com.cwb.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cwb.ucenter.mapper.XcUserMapper;
import com.cwb.ucenter.mapper.XcUserRoleMapper;
import com.cwb.ucenter.model.dto.AuthParamsDto;
import com.cwb.ucenter.model.po.XcUser;
import com.cwb.ucenter.model.po.XcUserRole;
import com.cwb.ucenter.service.feignclient.CheckCodeClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * @author CWB
 * @version 1.0
 */
@SuppressWarnings({"all"})
@Slf4j
@RestController
public class RegisterController {

    @Autowired
    CheckCodeClient checkCodeClient;
    @Autowired
    XcUserMapper xcUserMapper;
    @Autowired
    XcUserRoleMapper xcUserRoleMapper;
    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    @Transactional
    public XcUser register(@RequestBody AuthParamsDto registerUser){

        Boolean verify = checkCodeClient.verify(registerUser.getCheckcodekey(), registerUser.getCheckcode());
        if(!verify){
            throw new RuntimeException("验证码错误");
        }
        if (!registerUser.getPassword().equals(registerUser.getConfirmpwd())){
            throw new RuntimeException("两次密码输入不一致");
        }
        Integer isExist = xcUserMapper.isExistByusername(registerUser.getUsername());
        if (isExist>0)
            throw new RuntimeException("用户已经存在");
        XcUser xcUser=new XcUser();
        BeanUtils.copyProperties(registerUser,xcUser);
        String password=passwordEncoder.encode(xcUser.getPassword());
        xcUser.setPassword(password);
        String userId = UUID.randomUUID().toString();
        xcUser.setId(userId);
        xcUser.setName(xcUser.getNickname());
        xcUser.setUtype("101001");//学生类型
        xcUser.setStatus("1");//用户状态
        xcUserMapper.insert(xcUser);
        XcUserRole xcUserRole = new XcUserRole();
        xcUserRole.setId(UUID.randomUUID().toString());
        xcUserRole.setUserId(userId);
        xcUserRole.setRoleId("17");//学生角色
        xcUserRoleMapper.insert(xcUserRole);
        return xcUser;
    }
}
