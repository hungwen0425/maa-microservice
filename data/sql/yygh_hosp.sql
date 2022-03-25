/*
SQLyog Community v13.1.7 (64 bit)
MySQL - 5.7.34-log : Database - yygh_hosp
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`yygh_hosp` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

USE `yygh_hosp`;

/*Table structure for table `hospital_set` */

DROP TABLE IF EXISTS `hospital_set`;

CREATE TABLE `hospital_set` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `hosname` varchar(100) DEFAULT NULL COMMENT '医院名称',
  `hoscode` varchar(30) DEFAULT NULL COMMENT '医院编号',
  `api_url` varchar(100) DEFAULT NULL COMMENT 'api基础路径',
  `sign_key` varchar(50) DEFAULT NULL COMMENT '签名秘钥',
  `contacts_name` varchar(20) DEFAULT NULL COMMENT '联系人',
  `contacts_phone` varchar(11) DEFAULT NULL COMMENT '联系人手机',
  `status` tinyint(3) NOT NULL DEFAULT '0' COMMENT '状态',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(3) NOT NULL DEFAULT '0' COMMENT '逻辑删除(1:已删除，0:未删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_hoscode` (`hoscode`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='医院设置表';

/*Data for the table `hospital_set` */

insert  into `hospital_set`(`id`,`hosname`,`hoscode`,`api_url`,`sign_key`,`contacts_name`,`contacts_phone`,`status`,`create_time`,`update_time`,`is_deleted`) values 
(1,'北京协和医院','1000_0','http://localhost:9999','eac53fae12dbe5d1404d42e9651ede05','张三','15011111111',1,'2020-07-14 08:45:39','2020-07-16 15:11:46',0);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
