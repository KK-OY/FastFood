package com.sky.controller.admin;

import com.sky.mapper.OrderMapper;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.service.OrderService;
import com.sky.service.SetmealService;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/workspace")
public class WorkSpaceController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private WorkSpaceService workSpaceService;
    @Autowired
    private DishService dishService;
    @Autowired
    private OrderService orderService;
    @GetMapping("/businessData")
    public Result<BusinessDataVO> todatData(){
        BusinessDataVO businessDataVO = workSpaceService.todatData();
        return Result.success(businessDataVO);
    }

    @GetMapping("/overviewSetmeals")
    public Result<SetmealOverViewVO> overviewSetmeals(){
        SetmealOverViewVO setmealOverViewVO = setmealService.getoverviewSetmeals();
        return Result.success(setmealOverViewVO);
    }

    @GetMapping("/overviewDishes")
    public Result<DishOverViewVO> dishOverViewVO(){
        DishOverViewVO dishOverViewVO = dishService.dishOverViewVO();
        return Result.success(dishOverViewVO);
    }

    @GetMapping("/overviewOrders")
    public Result<OrderOverViewVO> orderOverView(){
        return Result.success(orderService.getOrderOverView());
    }
}
