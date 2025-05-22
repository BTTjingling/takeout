package com.takeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.takeout.entity.Order;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    @Insert("INSERT INTO `order` (user_id, shop_id, dish_id, quantity, total_amount, order_time, merchant_name, dish_name) " +
            "VALUES (#{userId}, #{shopId}, #{dishId}, #{quantity}, #{totalAmount}, #{orderTime}, #{merchantName}, #{dishName})")
    int insertOrder(Order order);
}