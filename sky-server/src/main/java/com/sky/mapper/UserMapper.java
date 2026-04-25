package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.Map;

@Mapper
public interface UserMapper {

    User GetUser(String openid);

    void add(User user);

    User getById(Long userId);

    Integer countByMap(Map<String, Object> map);

    Integer getTodayUser(LocalDate t);
}
