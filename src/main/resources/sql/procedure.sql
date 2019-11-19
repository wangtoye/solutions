CREATE TABLE `user` (
    `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(30) NOT NULL DEFAULT '' COMMENT '姓名',
    `age` INT(11) NOT NULL DEFAULT '0' COMMENT '年龄',
    `email` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '邮箱',
    `add_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `del_flag` TINYINT(2) NOT NULL DEFAULT '0' COMMENT '删除标记位 0有效 1删除',
    PRIMARY KEY (`id`)
) ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

DROP PROCEDURE IF EXISTS my_insert;
DELIMITER $$
CREATE PROCEDURE my_insert(
IN begin_index INT,
IN end_index INT
)
BEGIN
DECLARE v1 VARCHAR(30);
DECLARE v2 VARCHAR(50);
WHILE begin_index!=end_index DO
SET v1 = CONCAT('聂风', begin_index);
SET v2 = CONCAT('niefeng', begin_index, '@qq.com');
INSERT INTO `user` (`name`, `age`, `email`)VALUES(v1, begin_index, v2);
SET begin_index = begin_index + 1;
END WHILE;
END $$

#调用
#call my_insert(1,20000000);