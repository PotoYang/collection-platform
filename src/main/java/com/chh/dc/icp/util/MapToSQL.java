package com.chh.dc.icp.util;

import com.chh.dc.icp.parser.obd.reader.FieldInfo;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Created by Niow on 2016/9/5.
 */
public class MapToSQL {

    public static String toCreateSQL(Map<String, Object> map, String tableName) {
        StringBuilder sql = new StringBuilder("drop table IF EXISTS ").append(tableName).append(";\n");
        sql.append("create table ").append(tableName).append("(\n");
        sql.append("id BIGINT (20) UNSIGNED NOT NULL AUTO_INCREMENT,").append("\n");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sql.append(entry.getKey());
            Object value = entry.getValue();
            if (value instanceof Integer) {
                sql.append(" int");
            } else if (value instanceof Double) {
                sql.append(" double");
            } else if (value instanceof String) {
                sql.append(" varchar(255)");
            } else if (value instanceof Long) {
                sql.append(" bigint(20)");
            } else if (value instanceof Date) {
                sql.append(" Datetime");
            } else if (value instanceof Timestamp) {
                sql.append(" Datetime");
            }
            sql.append(",\n");
        }
        sql.deleteCharAt(sql.length() - 2);
        sql.append(");");
        return sql.toString();
    }

    public static String toCreateSQL(Collection<FieldInfo> fields, String tableName) {
        StringBuilder sql = new StringBuilder("drop table IF EXISTS ").append(tableName).append(";\n");
        sql.append("create table ").append(tableName).append("(\n");
        sql.append("id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,").append("\n");
        for (FieldInfo fieldInfo : fields) {
            sql.append(fieldInfo.getName());
            int type = fieldInfo.getType();
            String column = "";
            if (type < 4) {
                column = "int";
            } else{
                column = "bigint";
            }
            if (fieldInfo.getCoefficient() != null) {
                column = "double";
            }
            sql.append(" ").append(column);
            sql.append(",\n");
        }
        sql.deleteCharAt(sql.length() - 2);
        sql.append(");");
        return sql.toString();
    }

    public static String toExportTemplate(Map<String, Object> map, String tableName) {
        StringBuilder sql = new StringBuilder("<export id=\"\" type=\"jdbc_batch\" dataType=\"dna_can\" batchNumber=\"100\" target=\"exportTarger\" enable=\"true\">").append("\n");
        sql.append("<table name=\"").append(tableName).append("\"/>").append("\n");
        sql.append("<columns>").append("\n");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sql.append(" <column name=\"").append(entry.getKey()).append("\" />");
            sql.append("\n");
        }
        sql.append("</columns>").append("\n");
        sql.append("</export>").append("\n");
        return sql.toString();
    }
}
