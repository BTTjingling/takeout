package com.takeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.takeout.entity.Address;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface AddressMapper extends BaseMapper<Address> {

    @Update("UPDATE address SET is_default = 0 WHERE user_id = #{userId} AND address_id != #{addressId}")
    int cancelOtherDefault(@Param("userId") Long userId, @Param("addressId") Long addressId);
}
