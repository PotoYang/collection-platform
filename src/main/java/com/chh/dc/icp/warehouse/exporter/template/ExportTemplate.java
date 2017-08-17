package com.chh.dc.icp.warehouse.exporter.template;

import com.chh.dc.icp.warehouse.exporter.Exporter;

/**
 * Created by Niow on 2016/7/4.
 */
public class ExportTemplate {


    /**
     * 模板ID,全局唯一
     */
    private int id;

    /**
     * 模板类型：mysql,oracle,mssql,file等
     */
    private String type;

    /**
     * 数据类型：自定义描述
     */
    private String dataType;

    /**
     * 输出目标：<br/>
     * 如果是数据库模板，则填写数据库exporter的bean id<br/>
     * 如果是文件，则填写文件路径
     */
    private String target;

    /**
     * 模板是否启用
     */
    private boolean enable;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isDBExport() {
        return Exporter.TYPE_JDBC_BATCH.equalsIgnoreCase(this.type) || Exporter.TYPE_JDBC_POOL.equalsIgnoreCase(this.type) || Exporter.TYPE_JDBC_POOL_FAULT_TOLERANT.equalsIgnoreCase(this.type);
    }


}
