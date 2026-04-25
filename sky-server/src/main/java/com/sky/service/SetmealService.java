package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealOverViewVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    PageResult GetPageAll(SetmealPageQueryDTO setmealPageQueryDTO);

    void add(SetmealDTO setmealDTO);

    void Delete(Long[] ids);

    void Put(SetmealDTO setmealDTO);

    SetmealVO GetByid(Long id);

    void ChangeStatus(Integer status, Long id);

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    List<DishItemVO> getDishItemById(Long id);

    SetmealOverViewVO getoverviewSetmeals();
}
