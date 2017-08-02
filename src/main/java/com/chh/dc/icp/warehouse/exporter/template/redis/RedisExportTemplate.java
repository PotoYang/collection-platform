package com.chh.dc.icp.warehouse.exporter.template.redis;

import com.chh.dc.icp.warehouse.exporter.template.ExportTemplate;

/**
 * Created by Niow on 2016/9/28.
 */
public class RedisExportTemplate extends ExportTemplate {

    /**
     * 健值
     */
    private String key;

    /**
     * redis操作命令
     */
    private OptionTemplate optionTemplate;



    public RedisExportTemplate() {
    }



    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public OptionTemplate getOptionTemplate() {
        return optionTemplate;
    }

    public void setOptionTemplate(OptionTemplate optionTemplate) {
        this.optionTemplate = optionTemplate;
    }
}
