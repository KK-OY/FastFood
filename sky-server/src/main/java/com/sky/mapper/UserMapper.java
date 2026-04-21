package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    User GetUser(String openid);

    void add(User user);

    User getById(Long userId);
}
