package com.chh.dc.icp.warehouse.exporter;

import com.chh.dc.icp.db.pojo.TaskInfo;
import com.chh.dc.icp.warehouse.exporter.template.ExportTemplate;

/**
 * Created by Niow on 2016/7/14.
 */
public class ExporterFactory {

    public static Exporter createExporter(TaskInfo taskInfo, ExportTemplate template) throws Exception {
        if (template.getType() == null || template.getType().trim().equals("")) {
            throw new IllegalArgumentException("模板中输出器类型不存在");
        }
        switch (template.getType()) {
            case Exporter.TYPE_JDBC_POOL_FAULT_TOLERANT: {
                //Exporter exporter = new JDBCPoolExExporter(template,taskInfo);
                return null;
            }
            case Exporter.TYPE_JDBC_POOL: {
                return null;
            }
            case Exporter.TYPE_JDBC_BATCH: {
                Exporter exporter = new JDBCBatchExporter(template, taskInfo);
                return exporter;
            }
            case Exporter.TYPE_REDIS: {
                Exporter exporter = new RedisExporter(template, taskInfo);
                return exporter;
            }
            default:
                return null;
        }
    }
}
