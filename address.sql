/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 80012
Source Host           : localhost:3306
Source Database       : takeout

Target Server Type    : MYSQL
Target Server Version : 80012
File Encoding         : 65001

Date: 2025-05-24 23:46:49
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for address
-- ----------------------------
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
  `address_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '地址唯一标识',
  `user_id` bigint(20) NOT NULL COMMENT '关联的用户ID',
  `recipient_name` varchar(50) NOT NULL COMMENT '收货人姓名',
  `recipient_phone` varchar(20) NOT NULL COMMENT '收货人联系电话',
  `full_address` varchar(255) NOT NULL COMMENT '详细地址（省市区+街道）',
  `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否为默认地址（0否/1是）',
  `address_type` varchar(255) NOT NULL,
  PRIMARY KEY (`address_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_address_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='地址表';
