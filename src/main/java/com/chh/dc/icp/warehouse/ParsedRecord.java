package com.chh.dc.icp.warehouse;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ParsedRecord implements Serializable {

    public transient static final String DEFAULT_DATA_TYPE = "undefined";

    private static final long serialVersionUID = 7306441924327195014L;

    private transient String type = DEFAULT_DATA_TYPE;

    /**
     * 解析器最终解析出的可以达到入库条件的数据
     */
    private transient Map<String, Object> record = new LinkedHashMap<String, Object>();


    public ParsedRecord() {
        super();
    }

    public ParsedRecord(String type) {
        super();
        this.type = type;
    }


    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }


    public Map<String, Object> getRecord() {
        return record;
    }

    public void setRecord(Map<String, Object> record) {
        this.record = record;
    }

    public void putData(String key, Object value) {
        this.record.put(key, value);
    }

    public Object getData(String key) {
        return this.record.get(key);
    }
}