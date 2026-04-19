package com.sky.controller.admin;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/admin/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @PostMapping("")
    public Result<String> add(@RequestBody DishDTO dishDTO){
        dishService.add(dishDTO);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult> GetPageDish(DishPageQueryDTO dishPageQueryDTO){
        PageResult pageResult = dishService.GetPageDish(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping("")
    public Result<String> DeleteDish(Long[] ids){
        dishService.DeleteDish(ids);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<DishVO> GetById(@PathVariable Long id){
        DishVO dishVO = dishService.GetById(id);
        return Result.success(dishVO);
    }

    @PutMapping("")
    public Result<String> PutDish(@RequestBody DishDTO dishDTO){
        dishService.PutDish(dishDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    public Result<String> ChangeStatus(@PathVariable Integer status,Long id){
        dishService.ChangeStatus(status,id);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<Dish>> GetDishByCategory(Long categoryId){
        List<Dish> dishes = dishService.GetDishByCategory(categoryId);
        return Result.success(dishes);
    }


}
