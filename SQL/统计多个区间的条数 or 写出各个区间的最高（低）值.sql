/*
Navicat MySQL Data Transfer

Source Server         : 16.16.16.9@yjtmng
Source Server Version : 50530
Source Host           : 16.16.16.9:3306
Source Database       : db_moment_favourite

Target Server Type    : MYSQL
Target Server Version : 50530
File Encoding         : 65001

Date: 2015-12-21 15:19:35
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `t`
-- ----------------------------
DROP TABLE IF EXISTS `t`;
CREATE TABLE `t` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `subject_name` char(20) NOT NULL DEFAULT '',
  `student_name` char(20) NOT NULL DEFAULT '',
  `subject_score` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t
-- ----------------------------
INSERT INTO `t` VALUES ('1', 'A', 'a', '98');
INSERT INTO `t` VALUES ('2', 'B', 'a', '97');
INSERT INTO `t` VALUES ('3', 'C', 'a', '96');
INSERT INTO `t` VALUES ('4', 'A', 'b', '95');
INSERT INTO `t` VALUES ('5', 'B', 'b', '99');
INSERT INTO `t` VALUES ('6', 'C', 'b', '93');
INSERT INTO `t` VALUES ('7', 'A', 'c', '100');
INSERT INTO `t` VALUES ('8', 'B', 'c', '91');
INSERT INTO `t` VALUES ('9', 'C', 'c', '90');
INSERT INTO `t` VALUES ('10', 'D', 'a', '99');

-- 一条语句统计多个区间的条数
select subject_name,sum(case when subject_score>80 then 1 else 0 end) as '80分数段',sum(case when subject_score>90 then 1 else 0 end) as '90分数段' from t group by subject_name;

-- 一条语句写出各个区间的最高（低）值
SELECT * 
from t 
where t.subject_name in (select t1.subject_name from t as t1 GROUP BY t1.subject_name)
and t.id in 
(select SUBSTRING_INDEX(group_concat(b.id order by b.subject_score DESC),',',1) 
from t as b GROUP BY b.subject_name);

select * from t a where subject_score = (select max(subject_score) from t where subject_name=a.subject_name);
