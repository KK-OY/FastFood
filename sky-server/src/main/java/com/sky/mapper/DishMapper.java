package com.sky.mapper;

import com.sky.annotion.AutoFill;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);


    void addFlavors(List<DishFlavor> flavors);

    @AutoFill(value = OperationType.INSERT)
    void addDish(Dish dish);

    List<DishVO> GetPageDish(DishPageQueryDTO dishPageQueryDTO);

    void DeleteDishFlavor(Long[] ids);

    void DeleteDish(Long[] ids);

    DishVO GetById(Long id);

    List<DishFlavor> GetByIdFavor(long id);

    @AutoFill(value = OperationType.UPDATE)
    void PutDish(Dish dish);


    void ChangeStatus(Integer enable, Long id);

    Integer GetStatusById(Long[] ids);

    List<Dish> GetDishByCategory(Long categoryId);

    Integer CheckSetmealExist(Long[] ids);

    List<Dish> GetDishByCategoryUser(Dish dish);
}
