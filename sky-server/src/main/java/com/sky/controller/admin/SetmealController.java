package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @GetMapping("/page")
    //分页查询
    public Result<PageResult> GetPageAll(SetmealPageQueryDTO setmealPageQueryDTO){
        PageResult pageResult = setmealService.GetPageAll(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping("")
    public  Result<String> add(@RequestBody SetmealDTO setmealDTO){
        setmealService.add(setmealDTO);
        return Result.success();
    }

    @DeleteMapping("")
    public Result<String> Delete(Long[] ids){
        setmealService.Delete(ids);
        return Result.success();
    }

    @PutMapping("")
    public Result<String> Put(@RequestBody SetmealDTO setmealDTO){
        setmealService.Put(setmealDTO);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<SetmealVO> GetByid(@PathVariable Long id){
        SetmealVO setmealVO = setmealService.GetByid(id);
        return Result.success(setmealVO);
    }

    @PostMapping("/status/{status}")
    public Result<String> ChangeStatus(@PathVariable Integer status,Long id){
        setmealService.ChangeStatus(status,id);
        return Result.success();
    }
}
