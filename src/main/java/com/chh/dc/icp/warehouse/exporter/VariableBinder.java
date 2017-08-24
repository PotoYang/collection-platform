package com.chh.dc.icp.warehouse.exporter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 变量绑定接口<br>
 * 在DBPoolProxy中的部分方法需要通过实现该接口来完成对PreparedStatement的变量绑定<br>
 * <p>
 * VariableBinder
 *
 * @author Niow
 * @version 1.0
 * @date: 2016-6-27
 * @since 1.0
 */
public class VariableBinder {

    private List<ExportMapping> mappings;

    private SimpleDateFormat formatter = new SimpleDateFormat();

    public VariableBinder(List<String> exportMapping) {
        if (exportMapping == null || exportMapping.isEmpty()) {
            throw new IllegalArgumentException("exportMapping is null");
        }
        mappings = new ArrayList<ExportMapping>();
        for (String map : exportMapping) {
            ExportMapping mapping = new ExportMapping(map);
            mappings.add(mapping);
        }
    }

    /**
     * @return the mappings
     */
    public List<ExportMapping> getMappings() {
        return mappings;
    }

    /**
     * @param mappings the mappings to set
     */
    public void setMappings(List<ExportMapping> mappings) {
        this.mappings = mappings;
    }

    /**
     * 在方法中实现给PreparedStatement中的动态变量赋值，赋值来源obj中的各属性值
     *
     * @param pm
     * @param data
     * @throws SQLException
     */
    public void bind(PreparedStatement pm, Map<String, String> data) throws Exception {
        int parameterIndex = 1;
        for (int i = 0; i < mappings.size(); i++) {
            ExportMapping mapping = mappings.get(i);
            if (mapping.defaultValue != null) {
                continue;
            }
            fill(pm, parameterIndex, data, mapping);
            parameterIndex++;
        }
    }

    private void fill(PreparedStatement pm, int parameterIndex, Map<String, String> data, ExportMapping mapping)
            throws Exception {
        String value = data.get(mapping.fieldName);
        int type = getSqlTYpe(mapping.dataType);
        if (value == null || value.isEmpty()) {
            pm.setNull(parameterIndex, type);
            return;
        }
        if (mapping.dataType.equalsIgnoreCase("STRING")) {
            pm.setString(parameterIndex, value);
            return;
        } else if (mapping.dataType.equalsIgnoreCase("INT")) {
            pm.setInt(parameterIndex, Integer.parseInt(value));
            return;
        } else if (mapping.dataType.equalsIgnoreCase("LONG")) {
            pm.setLong(parameterIndex, Long.parseLong(value));
            return;
        } else if (mapping.dataType.equalsIgnoreCase("DATE")) {
            formatter.applyPattern(mapping.format);
            Date date = formatter.parse(value);
            pm.setDate(parameterIndex, new java.sql.Date(date.getTime()));
            return;
        } else if (mapping.dataType.equalsIgnoreCase("TIMESTAMP")) {
            formatter.applyPattern(mapping.format);
            Date date = formatter.parse(value);
            pm.setTimestamp(parameterIndex, new Timestamp(date.getTime()));
            return;
        } else if (mapping.dataType.equalsIgnoreCase("FLOAT")) {
            pm.setFloat(parameterIndex, Float.parseFloat(value));
            return;
        } else if (mapping.dataType.equalsIgnoreCase("DOUBLE")) {
            pm.setDouble(parameterIndex, Double.parseDouble(value));
            return;
        } else if (mapping.dataType.equalsIgnoreCase("BOOLEAN")) {
            pm.setBoolean(parameterIndex, Boolean.parseBoolean(value));
            return;
        }
    }

    static int getSqlTYpe(String dataType) {
        if (dataType.equalsIgnoreCase("STRING")) {
            return Types.VARCHAR;
        } else if (dataType.equalsIgnoreCase("INT")) {
            return Types.INTEGER;
        } else if (dataType.equalsIgnoreCase("LONG")) {
            return Types.NUMERIC;
        } else if (dataType.equalsIgnoreCase("DATE")) {
            return Types.DATE;
        } else if (dataType.equalsIgnoreCase("TIMESTAMP")) {
            return Types.TIMESTAMP;
        } else if (dataType.equalsIgnoreCase("FLOAT")) {
            return Types.FLOAT;
        } else if (dataType.equalsIgnoreCase("DOUBLE")) {
            return Types.DOUBLE;
        } else if (dataType.equalsIgnoreCase("BOOLEAN")) {
            return Types.BOOLEAN;
        } else {
            return Types.VARCHAR;
        }
    }

    /**
     * 输出模板映射对象
     *
     * @author Niow
     * @version 1.0
     * @ClassName: ExportMapping
     * @date: 2016-6-27
     * @since 1.0
     */
    static class ExportMapping {

        public ExportMapping(String mapping) {
            String[] split = mapping.split(";");
            if (split == null || split.length < 3) {
                throw new IllegalArgumentException("one of exportMapping is wrong:" + mapping);
            }
            for (int i = 0; i < split.length; i++) {
                String[] property = split[i].split("=");
                if (property.length < 2) {
                    throw new IllegalArgumentException("one of exportMapping is wrong:" + mapping);
                }
                String key = property[0];
                String value = property[1];
                if ("column".equalsIgnoreCase(key)) {
                    columnName = value;
                } else if ("field".equalsIgnoreCase(key)) {
                    fieldName = value;
                } else if ("type".equalsIgnoreCase(key)) {
                    dataType = value;
                } else if ("default".equalsIgnoreCase(key)) {
                    defaultValue = value;
                } else if ("option".equalsIgnoreCase(key)) {
                    option = value;
                } else if ("symbol".equalsIgnoreCase(key)) {
                    if ("eq".equalsIgnoreCase(value)) {
                        value = "=";
                    } else if ("gt".equalsIgnoreCase(value)) {
                        value = ">";
                    }
                    symbol = value;
                } else if ("format".equalsIgnoreCase(key)) {
                    format = value;
                } else if ("expression".equalsIgnoreCase(key)) {
                    expression = value;
                }
            }
            if (columnName == null || fieldName == null || dataType == null) {
                throw new IllegalArgumentException("exportMapping must contain columnName & fieldName & dataType:"
                        + mapping);
            }
            if ((dataType.equalsIgnoreCase("date") || dataType.equalsIgnoreCase("timeStamp")) && format == null) {
                throw new IllegalArgumentException(
                        "exportMapping must contain format if the dataType is date or timeStamp:" + mapping);
            }

        }

        public String columnName;

        public String fieldName;

        public String dataType;

        public String format;

        public String defaultValue;

        public String option;

        public String symbol;

        public String expression;
    }

}
