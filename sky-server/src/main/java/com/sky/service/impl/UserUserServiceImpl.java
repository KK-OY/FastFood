package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.BaseException;
import com.sky.mapper.UserMapper;
import com.sky.properties.JwtProperties;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserUserServicel;
import com.sky.utils.HttpClientUtil;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Service
public class UserUserServiceImpl implements UserUserServicel {
    @Autowired
    private  WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;

    private final static  String wx_login = "https://api.weixin.qq.com/sns/jscode2session";
    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public UserLoginVO login(UserLoginDTO userLoginDTO) {
        //获得openid用于检查user对象
        String openid = getOpendid(userLoginDTO.getCode());

        //判断是否为空  注意 == null判断有没有对象  isempty 判断有没有内容
        if(openid == null){
            throw  new BaseException(MessageConstant.LOGIN_FAILED);
        }

        //判断该用户是否为新用户，有就返回数据库里的user，没有就创建一个user
        User user = userMapper.GetUser(openid);

        if(user == null){
            user = User.builder()
                   .openid(openid)
                   .createTime(LocalDateTime.now())
                   .build();
            userMapper.add(user);
        }

        //生成jwt令牌添加到vo
        Map<String,Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID,user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims
        );

        return UserLoginVO.builder()
                .openid(openid)
                .token(token)
                .id(user.getId())
                .build();
    }



    private  String getOpendid(String code){
        Map m = new HashMap<>();
        m.put("appid",weChatProperties.getAppid());
        m.put("secret",weChatProperties.getSecret());
        m.put("js_code",code);
        m.put("grant_type","authorization_code");

        String s = HttpClientUtil.doGet(wx_login, m);
        JSONObject jsonObject = JSON.parseObject(s);
        String openid = jsonObject.getString("openid");
        return openid;
    }
}
