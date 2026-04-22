package com.sky.controller.admin;


import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/order")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/conditionSearch")
    public Result<PageResult> GetOrdersPage( OrdersPageQueryDTO ordersPageQueryDTO){
        PageResult pageResult = orderService.GetOrdersPage(ordersPageQueryDTO);
        return Result.success(pageResult);
    }


    @GetMapping("/statistics")
    public Result<OrderStatisticsVO> getOrdersStatus(){
        OrderStatisticsVO ordersStatus = orderService.getOrdersStatus();
        return Result.success(ordersStatus);
    }

    @GetMapping("/details/{id}")
    public Result<OrderVO> getOrdersDetailById(@PathVariable Long id){
        OrderVO orderVO = orderService.GetOrderById(id);
        return Result.success(orderVO);
    }

    @PutMapping("/confirm")
    public Result<String> takeOrders(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        orderService.takeOrders(ordersConfirmDTO);
        return Result.success();
    }
    @PutMapping("/rejection")
    public Result<String> refuseOrders(@RequestBody OrdersRejectionDTO ordersRejectionDTO){
        orderService.refuseOrders(ordersRejectionDTO);
        return Result.success();
    }

    @PutMapping("/cancel")
    public Result<String> cancelOrders(@RequestBody OrdersCancelDTO ordersCancelDTO){
        orderService.adminCancelOrders(ordersCancelDTO);
        return Result.success();
    }

    //派送，完成
    @PutMapping("/delivery/{id}")
    public Result<String> sendOrders(@PathVariable Long id){
        orderService.sendOrders(id);
        return  Result.success();
    }
@PutMapping("/complete/{id}")
    public Result<String> finishOrders(@PathVariable Long id){
        orderService.finishOrders(id);
        return Result.success();
}
}
