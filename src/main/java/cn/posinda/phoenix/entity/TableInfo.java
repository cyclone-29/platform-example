package cn.posinda.phoenix.entity;

import cn.posinda.phoenix.base.PhoenixDataEntity;
import scala.MatchError;

@SuppressWarnings("all")
/**
 * hbase表描述信息
 */
public class TableInfo extends PhoenixDataEntity {

    private static final long serialVersionUID = 7006798173812109138L;

    private String tableName;//表名称

    private Long recordCount;//记录条数

    private String description;//表描述信息

    private String systemFrom;//数据来源系统名称

    private String url;//对应的连接信息

    public static TableInfo.TableInfoBuilder builder() {
        return new TableInfo.TableInfoBuilder();
    }

    public String getTableName() {
        return this.tableName;
    }

    public Long getRecordCount() {
        return this.recordCount;
    }

    public String getDescription() {
        return this.description;
    }

    public String getSystemFrom() {
        return this.systemFrom;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setRecordCount(Long recordCount) {
        this.recordCount = recordCount;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSystemFrom(String systemFrom) {
        this.systemFrom = systemFrom;
    }

    public TableInfo() {
    }

    public TableInfo(String tableName, Long recordCount, String description, String systemFrom, String url) {
        this.tableName = tableName;
        this.recordCount = recordCount;
        this.description = description;
        this.systemFrom = systemFrom;
        this.url = url;
    }

    public static class TableInfoBuilder {

        private String tableName;

        private Long recordCount;

        private String description;

        private String systemFrom;

        private String url;

        TableInfoBuilder() {
        }

        public TableInfo.TableInfoBuilder tableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public TableInfo.TableInfoBuilder recordCount(Long recordCount) {
            this.recordCount = recordCount;
            return this;
        }

        public TableInfo.TableInfoBuilder description(String description) {
            this.description = description;
            return this;
        }

        public TableInfo.TableInfoBuilder systemFrom(String systemFrom) {
            this.systemFrom = systemFrom;
            return this;
        }

        public TableInfo.TableInfoBuilder url(String url) {
            this.url = url;
            return this;
        }

        public TableInfo build() {
            return new TableInfo(this.tableName, this.recordCount, this.description, this.systemFrom,this.url);
        }
    }

    public static enum SystemFrom {
        JCZH("集成指挥平台","JCZH"),
        YWGL("运维管理平台","YWGL"),
        ZDCL("重点车辆监管平台","ZDCL"),
        BZBX("标志标线管理系统","BZBX"),
        TCGL("拖车管理系统","TCGL"),
        SGDA("事故档案管理系统","SGDA"),
        SGAJ("8+X事故案件处理平台","SGAJ"),
        GAJW("公安网","GAJW");

        private String desc;

        private String shorthand;

        SystemFrom(String desc,String shorthand) {
            this.desc = desc;
            this.shorthand = shorthand;
        }

        public String getDesc() {
            return desc;
        }

        public String getShorthand() {
            return shorthand;
        }

        @Override
        public String toString() {
            return this.shorthand.concat("->").concat(this.desc);
        }

        public static SystemFrom fromDesc(String desc) {
            switch (desc) {
                case "集成指挥平台":
                    return JCZH;
                case "运维管理平台":
                    return YWGL;
                case "重点车辆监管平台":
                    return ZDCL;
                case "标志标线管理系统":
                    return BZBX;
                case "拖车管理系统":
                    return TCGL;
                case "事故档案管理系统":
                    return SGDA;
                case "8+X事故案件处理平台":
                    return SGAJ;
                case "公安网":
                    return GAJW;
                default:
                    throw new MatchError("匹配异常");
            }
        }

        public static SystemFrom fromShorthand(String shorthand){
            switch (shorthand){
                case "JCZH":
                    return JCZH;
                case "YWGL":
                    return YWGL;
                case "ZDCL":
                    return ZDCL;
                case "BZBX":
                    return BZBX;
                case "TCGL":
                    return TCGL;
                case "SGDA":
                    return SGDA;
                case "SGAJ":
                    return SGAJ;
                case "GAJW":
                    return GAJW;
                default:
                    throw new MatchError("匹配异常");
            }
        }

    }

}
