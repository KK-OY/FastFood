package com.sky.mapper;

import com.sky.annotion.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
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
}
