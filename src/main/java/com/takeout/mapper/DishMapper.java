package com.takeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.takeout.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
    // 自定义查询例如：全文搜索、按商家查询菜品等（可在 XML 中定义）
}