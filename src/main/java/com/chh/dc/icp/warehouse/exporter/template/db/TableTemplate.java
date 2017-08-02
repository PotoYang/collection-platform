package com.chh.dc.icp.warehouse.exporter.template.db;

/**
 * Created by Niow on 2016/7/5.
 */
public class TableTemplate {

    public static final String OPTION_UPDATE = "update";

    private String name;

    private String option;

    private String sql;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }
}
