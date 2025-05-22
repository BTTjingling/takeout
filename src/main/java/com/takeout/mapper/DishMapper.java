package com.takeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.takeout.entity.Dish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
    @Select("SELECT * FROM dish WHERE dish_id = #{dishId}")
    Dish selectById(Long dishId);
    // 自定义查询例如：全文搜索、按商家查询菜品等（可在 XML 中定义）
}