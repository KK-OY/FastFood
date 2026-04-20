package com.sky.service.impl;


import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.BaseException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShopCartMapper;
import com.sky.service.OrderService;
import com.sky.utils.BaseContext;
import com.sky.vo.OrderSubmitVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ShopCartMapper shopCartMapper;

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public OrderSubmitVO orderSubmit(OrdersSubmitDTO ordersSubmitDTO) {
        //查地址与用户是否存在
        AddressBook address = addressBookMapper.findAddress(ordersSubmitDTO.getAddressBookId());
        if (address == null ) {
          throw  new BaseException("地址异常");
        }

        List<ShoppingCart> shopCart = shopCartMapper.getShopCart(BaseContext.getCurrentId());
        if (shopCart == null || shopCart.isEmpty() ) {
            throw  new BaseException("购物车异常");
        }

        //总金额
//        BigDecimal sum = BigDecimal.ZERO;
//        BigDecimal otherSum = BigDecimal.ZERO;
//        BigDecimal box = new BigDecimal("2");
//        for(ShoppingCart s : shopCart){
//            BigDecimal bigDecimal =new BigDecimal( s.getNumber());
//            BigDecimal total = s.getAmount().multiply(bigDecimal);
//            sum = sum.add(total);
//            otherSum = otherSum.add(bigDecimal.multiply(box));
//        }
//        sum = sum.add(otherSum);

        //插入数据向订单表
        Orders orders =new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(address.getPhone());
        orders.setConsignee(address.getConsignee());
        orders.setUserId(BaseContext.getCurrentId());

        orderMapper.add(orders);
        //插入n条数据向订单明细表
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (ShoppingCart cart :shopCart){
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart,orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetails.add(orderDetail);
        }

        orderMapper.addDetail(orderDetails);

        //清空购物车
        shopCartMapper.cleanShopCart(BaseContext.getCurrentId());

        //装箱准备发给前端
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();

            return orderSubmitVO;
    }
}
