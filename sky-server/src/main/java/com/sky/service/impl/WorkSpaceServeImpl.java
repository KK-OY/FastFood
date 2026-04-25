package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class WorkSpaceServeImpl implements WorkSpaceService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private UserMapper userMapper;
    @Override
    public BusinessDataVO todatData() {
        //查新增加用户
        //查订单状态为5
        //查订单状态为5/总订单

        //查所有订单amout
        //客单价  营业额/订单状态为5
        LocalDate t = LocalDate.now();

        Integer todayUser = userMapper.getTodayUser(t); //用户
        Integer goodOrder = orderMapper.geTodayOrders(t, 5);//有效订单数
        Integer allOrder = orderMapper.geTodayOrders(t,null);//全部订单
        Double todayOrderAmount = orderMapper.getTodayOrderAmount(t);//营业额

        Double finishOrder = null;
        if (goodOrder!=0 && allOrder!=0) {
            finishOrder = (double) (goodOrder/allOrder);
        }else {finishOrder = 0.0;}

        Double v = todayOrderAmount / goodOrder;

        return new BusinessDataVO(todayOrderAmount,goodOrder,finishOrder,v,todayUser);
    }

    public BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end) {
        Map map = new HashMap();
        map.put("begin",begin);
        map.put("end",end);

        //查询总订单数
        Integer totalOrderCount = orderMapper.countByMap(map);

        map.put("status", Orders.COMPLETED);
        //营业额
        Double turnover = orderMapper.sumByMap(map);
        turnover = turnover == null? 0.0 : turnover;

        //有效订单数
        Integer validOrderCount = orderMapper.countByMap(map);

        Double unitPrice = 0.0;

        Double orderCompletionRate = 0.0;
        if(totalOrderCount != 0 && validOrderCount != 0){
            //订单完成率
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
            //平均客单价
            unitPrice = turnover / validOrderCount;
        }

        //新增用户数
        Integer newUsers = userMapper.countByMap(map);

        return BusinessDataVO.builder()
                .turnover(turnover)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .newUsers(newUsers)
                .build();
    }
}
