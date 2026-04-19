package com.sky.controller.admin;

import com.sky.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopStatus")
@RequestMapping("admin/shop")
public class ShopController {

    @Autowired
    private RedisTemplate redisTemplate;

    private final static String ShopStatus = "Shop_Status" ;

    @GetMapping ("/status")
    public Result<Integer> GetShopStatus(){
        Integer o = (Integer) redisTemplate.opsForValue().get(ShopStatus);
        return Result.success(o);
    }

    @PutMapping("/{status}")
    public Result<String> PutShopStatus(@PathVariable Integer status){
        redisTemplate.opsForValue().set(ShopStatus,status);
        return Result.success();
    }
}
