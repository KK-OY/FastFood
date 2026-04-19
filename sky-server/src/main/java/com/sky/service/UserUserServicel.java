package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.vo.UserLoginVO;

public interface UserUserServicel {
    UserLoginVO login(UserLoginDTO userLoginDTO);
}
