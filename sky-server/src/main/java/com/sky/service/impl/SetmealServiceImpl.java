package com.sky.service.impl;

import com.fasterxml.jackson.databind.util.BeanUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.BaseException;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealOverViewVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public PageResult GetPageAll(SetmealPageQueryDTO setmealPageQueryDTO) {

        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());

        List<SetmealVO> setmealVOS = setmealMapper.GetPageAll(setmealPageQueryDTO);

        Page<SetmealVO> p = (Page<SetmealVO>) setmealVOS;

        return new PageResult(p.getTotal(),p.getResult());
    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void add(SetmealDTO setmealDTO) {
        //拆分DTO，将DTO拆分为套餐和对应菜品 ，分别入库，注意主键注入
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);

        setmealMapper.addSeatMeal(setmeal);

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();

        if(setmealDishes !=null && !setmealDishes.isEmpty()){
            setmealDishes.forEach(s -> s.setSetmealId(setmeal.getId()));
            setmealMapper.addSeatDish(setmealDishes);
        }
    }

    //TODO        //先删除子表，再删除主表,判断删除时状态是否为旗手
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void Delete(Long[] ids) {
        Integer i = setmealMapper.CheckStatus(ids);
        if(i>0){
            throw  new BaseException("售卖状态不可删除!!!");
        }

        //先删除子表，再删除主表,判断删除时状态是否为旗手
        setmealMapper.DeleteSetmeal_dish(ids);
        setmealMapper.DeleteSetmeal(ids);
    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void Put(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.Put(setmeal);
        //先删除子表再添加新表
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if(setmealDishes != null && !setmealDishes.isEmpty()){

            Long[] n = {setmealDTO.getId()};
             setmealMapper.DeleteSetmeal_dish(n);

             setmealDishes.forEach(s -> s.setSetmealId(setmeal.getId()));
             setmealMapper.addSeatDish(setmealDishes);
        }
    }

    @Override
    public SetmealVO GetByid(Long id) {
        Setmeal setmeal = setmealMapper.GetByid(id);
        List<SetmealDish> setmealDishes = setmealMapper.GetByidDish(id);

        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal,setmealVO);

        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    @Override
    public void ChangeStatus(Integer status, Long id) {
        Setmeal setmeal = new Setmeal();
        setmeal.setId(id);
        setmeal.setStatus(status);
        setmealMapper.Put(setmeal);
    }

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }

    @Override
    public SetmealOverViewVO getoverviewSetmeals() {
        //获取已经启动的套餐与未启动的套餐
        return setmealMapper.getoverviewSetmeals();
    }
}
