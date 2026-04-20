package com.sky.controller.user.controller;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShopCartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("user/shoppingCart")
@RestController
@Slf4j
public class ShopCartController {
    @Autowired
    private ShopCartService  shopCartService;
    @PostMapping("/add")
    public Result<String> addCart(@RequestBody ShoppingCartDTO shoppingCartDTO){
        shopCartService.addCart(shoppingCartDTO);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<ShoppingCart>> getShopCart(){
        List<ShoppingCart> shopCart = shopCartService.getShopCart();
        return Result.success(shopCart);
    }

    @PostMapping("/sub")
    public Result<String> subOne(@RequestBody ShoppingCartDTO shoppingCartDTO){
        shopCartService.subOne(shoppingCartDTO);
        return Result.success();
    }

    @DeleteMapping("/clean")
    public Result<String> clean(){
        shopCartService.cleanShopCart();
        return Result.success();
    }
}
