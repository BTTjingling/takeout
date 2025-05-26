/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 80012
Source Host           : localhost:3306
Source Database       : takeout

Target Server Type    : MYSQL
Target Server Version : 80012
File Encoding         : 65001

Date: 2025-05-24 23:46:38
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
  `order_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单唯一标识',
  `user_id` bigint(20) NOT NULL COMMENT '下单用户ID（外键）',
  `shop_id` bigint(20) NOT NULL COMMENT '商铺ID（外键）',
  `dish_id` bigint(20) NOT NULL COMMENT '菜品ID（外键）',
  `quantity` int(11) NOT NULL COMMENT '购买数量',
  `total_amount` decimal(10,2) NOT NULL COMMENT '总金额',
  `order_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `Ostatus` varchar(20) DEFAULT '未接单',
  `merchant_name` varchar(255) DEFAULT NULL COMMENT '商家名称',
  `dish_name` varchar(255) DEFAULT NULL COMMENT '菜品名称',
  PRIMARY KEY (`order_id`),
  KEY `user_id` (`user_id`),
  KEY `shop_id` (`shop_id`),
  KEY `dish_id` (`dish_id`),
  CONSTRAINT `order_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `order_ibfk_2` FOREIGN KEY (`shop_id`) REFERENCES `shop` (`shop_id`) ON DELETE CASCADE,
  CONSTRAINT `order_ibfk_3` FOREIGN KEY (`dish_id`) REFERENCES `dish` (`dish_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单表';
