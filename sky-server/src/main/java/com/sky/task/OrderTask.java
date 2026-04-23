package com.sky.task;


import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {
    //2个问题  未支付15min超时自动取消订单 凌晨一点刷新所有超时订单


    @Autowired
    OrderMapper orderMapper;

    @Scheduled(cron = "0 * * * * ?")//每分钟触发一次
    public  void executeTask(){
        //判断下单时间是否大于15min ： now - 下单时间 > 15 = 下单时间 < now-15，并且状态处于待支付
        //查询所有上述条件的订单，返回订单列表，for更改
        log.info("开始每分钟的清算");
        LocalDateTime Time = LocalDateTime.now().plusMinutes(-15);
        Integer status = Orders.PENDING_PAYMENT;
        List<Orders> statusAndTime = orderMapper.getStatusAndTime(Time,status );

        if(statusAndTime != null && !statusAndTime.isEmpty()){
            for(Orders o : statusAndTime){
                o.setStatus(Orders.CANCELLED);
                o.setCancelTime(LocalDateTime.now());
                o.setCancelReason("订单超时未支付，自动取消");
                orderMapper.update(o);
            }
        }
    }

    //每天凌晨一点取消所有超时订单
    @Scheduled(cron = "0 0 1 * * ?")
    public  void outTimeOrdesClean(){
        LocalDateTime Time = LocalDateTime.now().plusMinutes(-60);

        Integer status = Orders.DELIVERY_IN_PROGRESS;
        List<Orders> statusAndTime = orderMapper.getStatusAndTime(Time,status);

        if(statusAndTime != null && !statusAndTime.isEmpty()){
            for(Orders o : statusAndTime){
                o.setStatus(Orders.COMPLETED);
                o.setCancelTime(LocalDateTime.now());
                orderMapper.update(o);
            }
        }
    }
}
