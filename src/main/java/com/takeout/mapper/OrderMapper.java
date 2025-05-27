package com.takeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.takeout.entity.Order;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;


@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    @Insert("INSERT INTO `order` (user_id, shop_id, dish_id, quantity, total_amount, " +
            "order_time, merchant_name, dish_name, recipient_name, recipient_phone, full_address) " +
            "VALUES (#{userId}, #{shopId}, #{dishId}, #{quantity}, #{totalAmount}, " +
            "#{orderTime}, #{merchantName}, #{dishName}, #{recipientName}, #{recipientPhone}, #{fullAddress})")
    int insertOrder(Order order);
    @Select("SELECT COUNT(*) FROM `order` WHERE shop_id = #{shopId} AND DATE(order_time) = #{today}")
    int countTodayOrdersByShopId(@Param("shopId") Long shopId, @Param("today") LocalDate today);
}
