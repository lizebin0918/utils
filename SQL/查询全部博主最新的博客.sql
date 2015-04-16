/*
Navicat MySQL Data Transfer

Source Server         : MySQL_lizb
Source Server Version : 50505
Source Host           : 127.0.0.1:3306
Source Database       : test

Target Server Type    : MYSQL
Target Server Version : 50505
File Encoding         : 65001

Date: 2014-04-25 23:32:18
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `t_blog`
-- ----------------------------
DROP TABLE IF EXISTS `t_blog`;
CREATE TABLE `t_blog` (
  `account` varchar(10) DEFAULT NULL,
  `create_date` varchar(8) DEFAULT NULL,
  `create_time` varchar(6) DEFAULT NULL,
  `blogname` varchar(500) DEFAULT NULL,
  `id` int(1) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_blog
-- ----------------------------
INSERT INTO `t_blog` VALUES ('lizebin', '20140425', '232200', 'a', '1');
INSERT INTO `t_blog` VALUES ('lizebin', '20140425', '232203', 'a', '2');
INSERT INTO `t_blog` VALUES ('lizebin', '20140424', '232203', 'b', '3');
INSERT INTO `t_blog` VALUES ('lizebin', '20140423', '232202', 'a', '4');
INSERT INTO `t_blog` VALUES ('lizebin', '20140426', '232202', 'c', '5');
INSERT INTO `t_blog` VALUES ('lizebin', '20140427', '232211', 'd', '6');
INSERT INTO `t_blog` VALUES ('lizebin', '20140428', '232200', 'e', '7');
INSERT INTO `t_blog` VALUES ('lizebin', '20140425', '232200', 'f', '8');
INSERT INTO `t_blog` VALUES ('lzb', '20140425', '232200', 'a', '9');
INSERT INTO `t_blog` VALUES ('lzb', '20140426', '232200', 'b', '10');
INSERT INTO `t_blog` VALUES ('lzb', '20140427', '232200', 'd', '11');
INSERT INTO `t_blog` VALUES ('lzb', '20140428', '232200', 'a', '12');


-- find the lastest blog for each account
/*
SELECT * 
from t_blog as i 
where i.account in ('lizebin','linxiangjun','lzb')
and i.id in 
(select SUBSTRING_INDEX(group_concat(b.id order by b.create_date DESC, b.create_time DESC),',',1) 
from t_blog as b GROUP BY b.account)
order by i.create_date DESC, i.create_time DESC
*/