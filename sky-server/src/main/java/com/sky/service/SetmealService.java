package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

public interface SetmealService {
    PageResult GetPageAll(SetmealPageQueryDTO setmealPageQueryDTO);

    void add(SetmealDTO setmealDTO);

    void Delete(Long[] ids);

    void Put(SetmealDTO setmealDTO);

    SetmealVO GetByid(Long id);

    void ChangeStatus(Integer status, Long id);
}
