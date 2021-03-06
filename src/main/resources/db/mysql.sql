SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS `TBL_INTERCEPTOR_FIELD` CASCADE;

DROP TABLE IF EXISTS `TBL_API_FIELD` CASCADE;

DROP TABLE IF EXISTS `TBL_INTERFACE_ERROR` CASCADE;

DROP TABLE IF EXISTS `TBL_DATABASE_CONNECTION` CASCADE;

CREATE TABLE `TBL_INTERCEPTOR_FIELD`(
 `ID` VARCHAR(32) NOT NULL COMMENT '逻辑主键',
 `API_URL` VARCHAR(255)   COMMENT '接口名称',
 `FIELD_NAME` VARCHAR(50) COMMENT '接口返回的字段名称',
 `KEY_NAME` VARCHAR(50)  COMMENT '过滤方式对应的申请KEY',
 `CREATE_DT` VARCHAR(50)  COMMENT '创建时间',
 `UPDATE_DT` VARCHAR(50) DEFAULT NULL COMMENT '修改时间',
 `INTERCEPTOR_TYPE` TINYINT COMMENT '字段的过滤方式，只支持字符串过滤',
 `DEL_FLAG` TINYINT DEFAULT 0 COMMENT '删除标记',
 	CONSTRAINT `PK_TBL_INTERCEPTOR_FIELD` PRIMARY KEY (`ID`),
 	CONSTRAINT `UQ_TBL_INTERCEPTOR_FIELD` UNIQUE (`API_URL`,`FIELD_NAME`,`KEY_NAME`)
) COMMENT '接口脱敏字段表';

CREATE TABLE `TBL_API_FIELD`(
 `ID` VARCHAR(32) NOT NULL COMMENT '逻辑主键',
 `FIELD_NAME` VARCHAR(50)   COMMENT '该接口返回的字段名称',
 `API_URL` VARCHAR(50) COMMENT '接口名称,controller mapping + method mapping,例如：/road/getPage',
 `DESCRIPTION` VARCHAR(255)  COMMENT '过滤字段的中文描述信息',
 	CONSTRAINT `PK_TBL_API_FIELD` PRIMARY KEY (`ID`),
 	CONSTRAINT `UQ_TBL_API_FIELD` UNIQUE (`FIELD_NAME`,`API_URL`)
) COMMENT '接口返回的字段名称';

CREATE TABLE `TBL_INTERFACE_ERROR`(
 `ID` VARCHAR(32) NOT NULL COMMENT '逻辑主键',
 `CHECK_TIME` VARCHAR(50)  COMMENT '检测到的时间',
 `INTERFACE_NAME` VARCHAR(255) COMMENT '连接异常的接口名称',
 	CONSTRAINT `PK_TBL_INTERFACE_ERROR` PRIMARY KEY (`ID`)
) COMMENT '接口连接异常信息表';

CREATE TABLE `TBL_DATABASE_CONNECTION` (
  `ID` varchar(32) NOT NULL COMMENT '逻辑主键',
  `URL` varchar(100) DEFAULT NULL COMMENT '数据库连接url',
  `USERNAME` varchar(50) DEFAULT NULL COMMENT '数据库用户',
  `PASSWORD` varchar(50) DEFAULT NULL COMMENT '连接密码',
  `SYSTEM_FROM` varchar(32) DEFAULT NULL COMMENT '表所属的系统',
  `STATUS` tinyint(4) DEFAULT '0' COMMENT '状态标记：0正常，1异常',
  CONSTRAINT `PK_TBL_INTERFACE_ERROR` PRIMARY KEY (`ID`),
  UNIQUE KEY `UQ_TBL_DATABASE_CONNECTION` (`URL`)
) COMMENT '数据库连接状态表';

SET FOREIGN_KEY_CHECKS=1;



