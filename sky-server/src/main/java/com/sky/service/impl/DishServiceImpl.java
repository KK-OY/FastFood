package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.BaseException;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void add(DishDTO dishDTO) {
        log.info("添加的菜品{}",dishDTO);
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.addDish(dish);

        List<DishFlavor> flavors = dishDTO.getFlavors();
        //判断是否有填写口味
        if(!CollectionUtils.isEmpty(flavors)){
            flavors.forEach(s -> {s.setDishId(dish.getId());});
            dishMapper.addFlavors(flavors);
        }
    }

    @Override
    public PageResult GetPageDish(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());

        List<DishVO> dishes = dishMapper.GetPageDish(dishPageQueryDTO);

        Page<DishVO> dishes1 =(Page<DishVO>) dishes;

        return new PageResult(dishes1.getTotal(),dishes1.getResult());
    }

    //TODO 删除菜品记得把检查对应套餐逻辑写完！！
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void DeleteDish(Long[] ids) {
        //判断菜品状态是否为0
        Integer i = dishMapper.GetStatusById(ids);
        if(i>0){
            throw new BaseException("起售状态不可删除");
        }
        //判断对应套餐是否存在
        Integer i1 = dishMapper.CheckSetmealExist(ids);
        if(i1>0){
            throw new BaseException("已经绑定了套餐!!");
        }

        dishMapper.DeleteDishFlavor(ids);
        dishMapper.DeleteDish(ids);
    }
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public DishVO GetById(Long id) {
        DishVO dishVO = dishMapper.GetById(id);
        dishVO.setFlavors(dishMapper.GetByIdFavor(dishVO.getId()));
        return dishVO;
    }


    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void PutDish(DishDTO dishDTO) {
        Dish d = new Dish();
        BeanUtils.copyProperties(dishDTO,d);
        //修改菜品，删除口味，添加口味
        dishMapper.PutDish(d);

        Long[] n = new Long[]{d.getId()};
        dishMapper.DeleteDishFlavor(n);

        if (dishDTO.getFlavors() != null && !dishDTO.getFlavors().isEmpty()) {
            List<DishFlavor> flavors = dishDTO.getFlavors();
            flavors.forEach(s -> s.setDishId(dishDTO.getId()));
            dishMapper.addFlavors(flavors);
        }

    }

    @Override
    public void ChangeStatus(Integer status, Long id) {
        Dish dish = new Dish();
        dish.setStatus(status);
        dish.setId(id);

        dishMapper.PutDish(dish);

    }

    @Override
    public List<Dish> GetDishByCategory(Long categoryId) {
        List<Dish> dishes = dishMapper.GetDishByCategory(categoryId);
        return dishes;
    }
}
