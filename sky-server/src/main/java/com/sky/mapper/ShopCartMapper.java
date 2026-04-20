package com.sky.mapper;


import com.sky.annotion.AutoFill;
import com.sky.entity.ShoppingCart;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper
public interface ShopCartMapper  {

    ShoppingCart findShopCart(ShoppingCart shoppingCart) ;


    void addShopCart(ShoppingCart shopCart);


    void updateShopCart(ShoppingCart oldshopCart);

    List<ShoppingCart> getShopCart(Long userId);

    void delShopCart(ShoppingCart oldshopCart);

    void cleanShopCart(Long userId);
}
