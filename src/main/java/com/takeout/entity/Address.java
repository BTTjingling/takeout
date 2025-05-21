package com.takeout.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("address")
public class Address {
    @TableId(type = IdType.AUTO)
    private Long addressId;
    private Long userId;
    private String recipientName;
    private String recipientPhone;
    private String fullAddress;
    private Boolean isDefault;
    private String addressType;        // 地址类型 (家、公司、学校等)
}
