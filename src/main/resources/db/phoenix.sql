create table TBL_ERROR_MESSAGE(occursInstant VARCHAR PRIMARY KEY NOT NULL,message VARCHAR,url VARCHAR)DEFAULT_COLUMN_FAMILY='F';
错误记录表
occursInstant:错误的发生时间,时间格式为yyyy-MM-dd HH:mm:ss.SSS
message:错误的信息
url:发生错误的请求地址

CREATE TABLE TBL_TABLE_INFO(tableName VARCHAR PRIMARY KEY NOT NULL, recordCount BIGINT, description VARCHAR,systemFrom VARCHAR,url VARCHAR)DEFAULT_COLUMN_FAMILY='F';
phoenix表的属性信息
tableName:表名称
recordCount:表的记录条数
description:表的描述信息
sysFrom:数据表来源的系统信息
url:拉取该表数据使用的url信息