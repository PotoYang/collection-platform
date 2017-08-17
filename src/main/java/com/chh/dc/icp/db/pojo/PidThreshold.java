package com.chh.dc.icp.db.pojo;

import java.io.Serializable;

public class PidThreshold implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -5464301289859360291L;

    private Integer id;
    private String pid;
    private String name;
    private Double minValue;
    private Double maxValue;
    /**
     * 类型：0航天无线 1元征
     */
    private Integer type;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getMinValue() {
        return minValue;
    }

    public void setMinValue(Double minValue) {
        this.minValue = minValue;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
