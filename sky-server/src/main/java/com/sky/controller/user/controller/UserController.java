package com.sky.controller.user.controller;

import com.sky.dto.UserLoginDTO;
import com.sky.result.Result;
import com.sky.service.UserUserServicel;
import com.sky.vo.UserLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user/user")
public class UserController {
    @Autowired
    private UserUserServicel userUserServicel;

    @PostMapping("/login")
    public Result<UserLoginVO> userLogin(@RequestBody UserLoginDTO userLoginDTO){
        UserLoginVO login = userUserServicel.login(userLoginDTO);

        return Result.success(login);
    }
}
