package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    void add(DishDTO dishDTO);

    PageResult GetPageDish(DishPageQueryDTO dishPageQueryDTO);

    void DeleteDish(Long[] ids);

    DishVO GetById(Long id);

    void PutDish(DishDTO dishDTO);

    void ChangeStatus(Integer status, Long id);

    List<Dish> GetDishByCategory(Long categoryId);

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);
}


