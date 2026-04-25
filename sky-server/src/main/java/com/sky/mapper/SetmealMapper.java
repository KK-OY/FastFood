package com.sky.mapper;

import com.sky.annotion.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.entity.ShoppingCart;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealOverViewVO;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    List<SetmealVO> GetPageAll(SetmealPageQueryDTO setmealPageQueryDTO);

    @AutoFill(value = OperationType.INSERT)
    void addSeatMeal(Setmeal setmeal);


    void addSeatDish(List<SetmealDish> setmealDishes);

    void DeleteSetmeal_dish(Long[] ids);

    void DeleteSetmeal(Long[] ids);

    @AutoFill(value = OperationType.UPDATE)
    void Put(Setmeal setmeal);

    Setmeal GetByid(Long id);

    List<SetmealDish> GetByidDish(Long id);

    Integer CheckStatus(Long[] ids);

    /**
     * 动态条件查询套餐
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据套餐id查询菜品选项
     * @param setmealId
     * @return
     */
    @Select("select sd.name, sd.copies, d.image, d.description " +
            "from setmeal_dish sd left join dish d on sd.dish_id = d.id " +
            "where sd.setmeal_id = #{setmealId}")
    List<DishItemVO> getDishItemBySetmealId(Long setmealId);

    SetmealOverViewVO getoverviewSetmeals();
}
