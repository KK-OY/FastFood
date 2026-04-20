package com.sky.service.impl;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.exception.BaseException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShopCartMapper;
import com.sky.service.ShopCartService;
import com.sky.utils.BaseContext;
import com.sky.vo.DishVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShopCartServiceImpl implements ShopCartService {
    @Autowired
    private ShopCartMapper shopCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Override
    public void addCart(ShoppingCartDTO shoppingCartDTO) {
        //先装货
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .dishId(shoppingCartDTO.getDishId())
                .setmealId(shoppingCartDTO.getSetmealId())
                .dishFlavor(shoppingCartDTO.getDishFlavor())
                .userId(BaseContext.getCurrentId())
                .build();

        //查询有没有对应的购物车
        ShoppingCart oldshopCart = shopCartMapper.findShopCart(shoppingCart);

        //没有就增加
        if(oldshopCart==null){
             //判断是哪个对象，然后继续装货
            if(shoppingCartDTO.getSetmealId()!=null){
                Setmeal setmeal = setmealMapper.GetByid(shoppingCartDTO.getSetmealId());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setNumber(1);
                shoppingCart.setAmount(setmeal.getPrice());
                shoppingCart.setCreateTime(LocalDateTime.now());
            }else{
                DishVO dishVO = dishMapper.GetById(shoppingCartDTO.getDishId());
                shoppingCart.setImage(dishVO.getImage());
                shoppingCart.setName(dishVO.getName());
                shoppingCart.setNumber(1);
                shoppingCart.setAmount(dishVO.getPrice());
                shoppingCart.setCreateTime(LocalDateTime.now());
            }

            //创建对应购物车
            shopCartMapper.addShopCart(shoppingCart);
        }else {
            oldshopCart.setNumber(oldshopCart.getNumber()+1);
            shopCartMapper.updateShopCart(oldshopCart);
        }

    }

    @Override
    public List<ShoppingCart> getShopCart() {
        Long userId = BaseContext.getCurrentId();
        List<ShoppingCart> shopCart = shopCartMapper.getShopCart(userId);
        return shopCart;
    }

    @Override
    public void subOne(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(BaseContext.getCurrentId())
                .dishFlavor(shoppingCartDTO.getDishFlavor())
                .dishId(shoppingCartDTO.getDishId())
                .setmealId(shoppingCartDTO.getSetmealId())
                .build();

        ShoppingCart oldshopCart = shopCartMapper.findShopCart(shoppingCart);
        if(oldshopCart ==null){
            throw new BaseException("商品减少异常");
        }

        if(oldshopCart.getNumber() >1){
            oldshopCart.setNumber(oldshopCart.getNumber()-1);
            shopCartMapper.updateShopCart(oldshopCart);

        }else {
            shopCartMapper.delShopCart(oldshopCart);
        }
    }

    @Override
    public void cleanShopCart() {
        Long userId = BaseContext.getCurrentId();
        shopCartMapper.cleanShopCart(userId);
    }
}
