package com.sky.controller.user.controller;

import com.sky.result.Result;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.Cacheable;

import java.io.IOException;

@RestController("userShopStatus")
@RequestMapping("user/shop")
public class ShopController {

    @Autowired
    private RedisTemplate redisTemplate;

    private final static String ShopStatus = "Shop_Status" ;

    @GetMapping ("/status")
    public Result<Integer> GetShopStatus(){
        Integer o = (Integer) redisTemplate.opsForValue().get(ShopStatus);
        return Result.success(o);
    }



    public void Get() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet("http://localhost:8080");

        CloseableHttpResponse response = httpClient.execute(httpGet);

        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println("状态码");
    }
}
