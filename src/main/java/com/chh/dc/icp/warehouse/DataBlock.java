package com.chh.dc.icp.warehouse;

import java.util.List;
import java.util.Map;

/**
 * Created by Niow on 2016/7/4.
 */
public class DataBlock {

    public DataBlock() {

    }

    public DataBlock(List<ParsedRecord> data, String type) {
        this.dataType = type;
        this.data = data;
    }

    private String dataType;

    public List<ParsedRecord> data;

    public String getDataType() {
        return dataType;
    }

    public List<ParsedRecord> getData() {
        return data;
    }

    public void setData(List<ParsedRecord> data) {
        this.data = data;
    }

    public void setDataType(String dataType) {

        this.dataType = dataType;
    }
}
