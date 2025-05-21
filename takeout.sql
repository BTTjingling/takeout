/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 80034
Source Host           : localhost:3306
Source Database       : takeout

Target Server Type    : MYSQL
Target Server Version : 80034
File Encoding         : 65001

Date: 2025-05-20 16:09:18
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for address
-- ----------------------------
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
  `address_id` bigint NOT NULL AUTO_INCREMENT COMMENT '地址唯一标识',
  `user_id` bigint NOT NULL COMMENT '关联的用户ID',
  `recipient_name` varchar(50) NOT NULL COMMENT '收货人姓名',
  `recipient_phone` varchar(20) NOT NULL COMMENT '收货人联系电话',
  `full_address` varchar(255) NOT NULL COMMENT '详细地址（省市区+街道）',
  `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否为默认地址（0否/1是）',
  PRIMARY KEY (`address_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_address_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='地址表';

-- ----------------------------
-- Records of address
-- ----------------------------

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `admin_id` bigint NOT NULL AUTO_INCREMENT COMMENT '管理员唯一标识',
  `username` varchar(50) NOT NULL COMMENT '管理员账号',
  `name` varchar(50) NOT NULL COMMENT '管理员姓名',
  `password` char(64) NOT NULL COMMENT '密码',
  PRIMARY KEY (`admin_id`),
  UNIQUE KEY `idx_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='管理员表';

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES ('1', 'root', '李', '123456');

-- ----------------------------
-- Table structure for dish
-- ----------------------------
DROP TABLE IF EXISTS `dish`;
CREATE TABLE `dish` (
  `dish_id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜品唯一标识',
  `shop_id` bigint NOT NULL COMMENT '所属商家ID（外键）',
  `name` varchar(100) NOT NULL COMMENT '菜品名称',
  `description` text COMMENT '菜品描述（如配料、口味）',
  `price` decimal(10,2) NOT NULL COMMENT '基础价格',
  `category` varchar(50) DEFAULT NULL COMMENT '分类（如主食/饮料/小吃）',
  `is_available` tinyint(1) DEFAULT '1' COMMENT '是否上架（1:是，0:否）',
  `inventory` int DEFAULT NULL COMMENT '菜品库存',
  `image_url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`dish_id`),
  KEY `idx_shop_id` (`shop_id`),
  FULLTEXT KEY `idx_search` (`name`,`description`),
  CONSTRAINT `dish_ibfk_1` FOREIGN KEY (`shop_id`) REFERENCES `shop` (`shop_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜品信息表';

-- ----------------------------
-- Records of dish
-- ----------------------------
INSERT INTO `dish` VALUES ('1', '1', '更新后的菜品名称', '更新后的描述', '99.99', '更新后的分类', '1', '100', null);
INSERT INTO `dish` VALUES ('2', '1', '牛肉饭', '好吃', '25.00', '主食', '1', '111', '/images/1747641192808_R.jpg');
INSERT INTO `dish` VALUES ('3', '2', '烤羊肉串', '香喷喷', '5.00', '肉肉', '1', '111', null);
INSERT INTO `dish` VALUES ('4', '1', '更新', '更新', '99.99', '更新', '1', '100', null);
INSERT INTO `dish` VALUES ('5', '1', '好吃鱼', '阿达伟大', '1.00', '默认分类', '1', '1', null);
INSERT INTO `dish` VALUES ('6', '1', '更新', '更新', '99.99', '更新', '0', '101', null);
INSERT INTO `dish` VALUES ('7', '1', '更新', '很棒', '99.99', '更新', '0', '100', null);
INSERT INTO `dish` VALUES ('8', '1', '更新', '巨好吃', '99.99', '更新', '0', '100', null);
INSERT INTO `dish` VALUES ('10', '1', '更新', '333333', '99.99', '更新', '0', '104', null);
INSERT INTO `dish` VALUES ('11', '1', 'ww', '发我无法无法', '11.00', '默认分类', '0', '2', null);
INSERT INTO `dish` VALUES ('12', '1', '西西', '好吃', '2.00', '默认分类', '0', '1', null);

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
  `order_id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单唯一标识',
  `user_id` bigint NOT NULL COMMENT '下单用户ID（外键）',
  `shop_id` bigint NOT NULL COMMENT '商铺ID（外键）',
  `dish_id` bigint NOT NULL COMMENT '菜品ID（外键）',
  `quantity` int NOT NULL COMMENT '购买数量',
  `total_amount` decimal(10,2) NOT NULL COMMENT '总金额',
  `order_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `Ostatus` varchar(20) DEFAULT '未接单',
  PRIMARY KEY (`order_id`),
  KEY `user_id` (`user_id`),
  KEY `shop_id` (`shop_id`),
  KEY `dish_id` (`dish_id`),
  CONSTRAINT `order_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `order_ibfk_2` FOREIGN KEY (`shop_id`) REFERENCES `shop` (`shop_id`) ON DELETE CASCADE,
  CONSTRAINT `order_ibfk_3` FOREIGN KEY (`dish_id`) REFERENCES `dish` (`dish_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单表';

-- ----------------------------
-- Records of order
-- ----------------------------
INSERT INTO `order` VALUES ('1', '1', '1', '1', '1', '11.00', '2025-05-13 16:12:48', '未接单');
INSERT INTO `order` VALUES ('2', '1', '1', '1', '1', '11.00', '2025-05-13 08:40:07', '未接单');
INSERT INTO `order` VALUES ('3', '1', '1', '2', '1', '25.00', '2025-05-13 08:40:07', '未接单');
INSERT INTO `order` VALUES ('4', '1', '1', '2', '2', '50.00', '2025-05-13 08:40:28', '未接单');
INSERT INTO `order` VALUES ('5', '1', '1', '1', '1', '11.00', '2025-05-13 10:22:05', '未接单');
INSERT INTO `order` VALUES ('6', '1', '1', '1', '1', '11.00', '2025-05-15 08:10:30', '未接单');
INSERT INTO `order` VALUES ('7', '1', '1', '5', '2', '2.00', '2025-05-17 06:30:18', '未接单');
INSERT INTO `order` VALUES ('8', '1', '1', '1', '1', '99.99', '2025-05-17 10:33:57', '未接单');
INSERT INTO `order` VALUES ('9', '1', '1', '1', '1', '99.99', '2025-05-17 10:51:09', '未接单');
INSERT INTO `order` VALUES ('10', '1', '1', '2', '2', '50.00', '2025-05-17 10:51:10', '未接单');
INSERT INTO `order` VALUES ('11', '1', '1', '5', '2', '2.00', '2025-05-17 10:52:06', '未接单');
INSERT INTO `order` VALUES ('12', '1', '1', '5', '2', '2.00', '2025-05-19 08:02:29', '未接单');

-- ----------------------------
-- Table structure for shop
-- ----------------------------
DROP TABLE IF EXISTS `shop`;
CREATE TABLE `shop` (
  `shop_id` bigint NOT NULL AUTO_INCREMENT COMMENT '商家唯一标识',
  `phone` varchar(20) NOT NULL COMMENT '手机号（登录账号）',
  `password` char(64) NOT NULL COMMENT '密码',
  `name` varchar(100) NOT NULL COMMENT '店铺名称',
  `description` text COMMENT '店铺描述（支持长文本）',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '店铺状态（0关闭/1营业）',
  `address` varchar(255) NOT NULL COMMENT '店铺地址',
  `rating` decimal(2,1) DEFAULT '5.0' COMMENT '店铺评分（0-5分）',
  `minprice` decimal(10,2) DEFAULT '0.00',
  `devfee` decimal(10,2) DEFAULT '0.00',
  `avatar` varchar(255) DEFAULT NULL COMMENT '商家头像路径',
  PRIMARY KEY (`shop_id`),
  UNIQUE KEY `idx_phone` (`phone`),
  FULLTEXT KEY `idx_name_desc` (`name`,`description`) COMMENT '全文索引（支持名称和描述搜索）'
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商家信息表';

-- ----------------------------
-- Records of shop
-- ----------------------------
INSERT INTO `shop` VALUES ('1', '15814810322', '123123', '泰式达抛饭', '嗡嗡嗡', '1', '嗡嗡嗡', '4.5', '2.00', '2.00', 'Avatar1.png');
INSERT INTO `shop` VALUES ('2', '111111', '111111', '阿达西烧烤', '美味美味', '1', '新疆大道', '5.0', '0.00', '0.00', null);
INSERT INTO `shop` VALUES ('3', '13800138000', '123123', 'lili', '', '0', '', '5.0', '0.00', '0.00', '');
INSERT INTO `shop` VALUES ('4', '13800138001', '123123', 'wwww', '', '0', '', '5.0', '0.00', '0.00', '');
INSERT INTO `shop` VALUES ('5', '13800138002', '123123', 'www', '', '0', '', '5.0', '0.00', '0.00', '');
INSERT INTO `shop` VALUES ('6', '13800138', 'test123', 'testmerchant', '', '0', '', '5.0', '0.00', '0.00', '');
INSERT INTO `shop` VALUES ('7', '1380013', 'test123', 'test', '', '0', '', '5.0', '0.00', '0.00', '');
INSERT INTO `shop` VALUES ('8', '138001', 'test123', 'test4', '', '0', '', '5.0', '0.00', '0.00', '');
INSERT INTO `shop` VALUES ('9', '13800138009', '123123', 'titi', '', '0', '', '5.0', '0.00', '0.00', '');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户唯一标识',
  `phone` varchar(20) NOT NULL COMMENT '手机号（登录账号）',
  `password` char(64) NOT NULL COMMENT '密码',
  `username` varchar(50) DEFAULT NULL COMMENT '用户姓名',
  `email` varchar(100) DEFAULT NULL COMMENT '用户邮箱',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `idx_phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', '123123', '123123', '花花', '2333@qq.conm');
INSERT INTO `user` VALUES ('2', '13800138000', 'test123', 'testuser', null);
INSERT INTO `user` VALUES ('3', '13800138002', '123123', 'lkli', null);
