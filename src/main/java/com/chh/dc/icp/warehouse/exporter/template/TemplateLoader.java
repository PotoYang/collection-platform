package com.chh.dc.icp.warehouse.exporter.template;

import com.chh.dc.icp.util.StringUtil;
import com.chh.dc.icp.warehouse.exporter.Exporter;
import com.chh.dc.icp.warehouse.exporter.template.db.DBExportTemplate;
import com.chh.dc.icp.warehouse.exporter.template.db.TableTemplate;
import com.chh.dc.icp.warehouse.exporter.template.redis.OptionTemplate;
import com.chh.dc.icp.warehouse.exporter.template.redis.RedisExportTemplate;
import com.chh.dc.icp.warehouse.exporter.template.db.ColumnTemplate;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Niow on 2016/7/4.
 */
public class TemplateLoader {

    //    private static final String EXPORT_TEMPLATE_DIR ="/template" + File.separator + "export";
    private static final String EXPORT_TEMPLATE_DIR = "/template/export/";

    public static List<ExportTemplate> loadTemplate() {

        return null;
    }

    public static List<ExportTemplate> loadTemplate(List<String> filePaths) throws Exception {
        List<ExportTemplate> templateList = new ArrayList<ExportTemplate>();
        for (String filePath : filePaths) {
            List<ExportTemplate> templates = loadTemplate(filePath);
            templateList.addAll(templates);
        }
        return templateList;
    }

    public static List<ExportTemplate> loadTemplate(String filePath) throws Exception {
//        String path = EXPORT_TEMPLATE_DIR + File.separator + filePath;
        String path = EXPORT_TEMPLATE_DIR + filePath;
        InputStream inputStream = TemplateLoader.class.getResourceAsStream(path);
        if (inputStream == null) {
            throw new Exception("输出模板：" + path + "未找到");
        }
        //读取模板
        Element rootEle = new SAXReader().read(inputStream).getRootElement();
        String rootType = rootEle.attributeValue("type");
        String rootTarget = rootEle.attributeValue("target");

        List<Element> exports = rootEle.elements("export");
        //创建模板列表
        List<ExportTemplate> templateList = new ArrayList<ExportTemplate>();

        for (Element exportElement : exports) {
            String type = exportElement.attributeValue("type");
            if (type == null) {
                type = rootType;
            }
            ExportTemplate exportTemplate = null;
            if (isDBExport(type)) {
                exportTemplate = readDBExportTemplate(exportElement);
            } else if (Exporter.TYPE_REDIS.equals(type)) {
                exportTemplate = readRedisExportTemplate(exportElement);
            } else {
                exportTemplate = new ExportTemplate();
                readExportTemplate(exportTemplate, exportElement);
            }
            if (exportTemplate.getTarget() == null) {
                exportTemplate.setTarget(rootTarget);
            }
            if (exportTemplate.getType() == null) {
                exportTemplate.setType(rootType);
            }
            templateList.add(exportTemplate);
        }
        return templateList;
    }

    /**
     * 读取export标签的属性，填入exportTemplate中
     *
     * @param exportTemplate
     * @param exportElement
     */
    private static void readExportTemplate(ExportTemplate exportTemplate, Element exportElement) {
        int id = Integer.parseInt(exportElement.attributeValue("id"));
        String type = exportElement.attributeValue("type");
        String dataType = exportElement.attributeValue("dataType");
        String target = exportElement.attributeValue("target");
        boolean enable = Boolean.parseBoolean(exportElement.attributeValue("enable"));

        exportTemplate.setId(id);
        exportTemplate.setType(type);
        exportTemplate.setDataType(dataType);
        exportTemplate.setTarget(target);
        exportTemplate.setEnable(enable);

    }

    private static String readProperty(Element element, String fieldName) {
        String value = element.attributeValue(fieldName);
        if (value == null) {
            Element subElement = element.element(fieldName);
            if (subElement != null) {
                value = subElement.attributeValue("value");
            }
        }
        return value;
    }

    /* redis模板部分 */
    private static OptionTemplate readOption(Element optionElement) {
        String type = optionElement.attributeValue("type");
        String timeout = readProperty(optionElement, "timeout");
        String score = readProperty(optionElement, "score");
        String scoreType = readProperty(optionElement, "scoreType");
        String field = readProperty(optionElement, "field");
        String output = readProperty(optionElement, "output");
        OptionTemplate option = new OptionTemplate();
        option.setScore(score);
        option.setType(type);
        option.setField(field);
        option.setOutput(output);
        if (timeout != null) {
            option.setTimeout(Integer.parseInt(timeout));
        }
        option.setScoreType(scoreType);
        return option;
    }

    private static RedisExportTemplate readRedisExportTemplate(Element exportElement) {
        RedisExportTemplate redisExportTemplate = new RedisExportTemplate();

        readExportTemplate(redisExportTemplate, exportElement);

        Element keyElement = exportElement.element("key");
        redisExportTemplate.setKey(keyElement.attributeValue("value"));


        OptionTemplate optionTemplate = readOption(exportElement.element("option"));
        redisExportTemplate.setOptionTemplate(optionTemplate);
        return redisExportTemplate;
    }


    /* db模板部分 */
    private static TableTemplate readTable(Element tableElement) {
        TableTemplate table = new TableTemplate();

        String name = tableElement.attributeValue("name");
        String option = tableElement.attributeValue("option");

        table.setName(name);
        table.setOption(option);

        return table;
    }

    private static ColumnTemplate readColumn(Element columnElement) {
        String name = columnElement.attributeValue("name");
        String property = columnElement.attributeValue("property");
        String format = columnElement.attributeValue("format");

        ColumnTemplate column = new ColumnTemplate(name, property);
        column.setFormat(format);
        return column;
    }

    /**
     * 读取数据库类型输出模板
     *
     * @param exportElement
     * @return
     */
    private static DBExportTemplate readDBExportTemplate(Element exportElement) {
        DBExportTemplate exportTemplate = new DBExportTemplate();

        //read common attribute
        readExportTemplate(exportTemplate, exportElement);

        //read export param
        String batchNumber = exportElement.attributeValue("batchNumber");
        if (StringUtil.isNotNull(batchNumber)) {
            exportTemplate.setBatchNumber(Integer.parseInt(batchNumber));
        }

        //read table
        Element tableElement = exportElement.element("table");
        TableTemplate tableTemplate = readTable(tableElement);
        exportTemplate.setTable(tableTemplate);

        //read columns
        List<Element> columnElements = exportElement.element("columns").elements("column");
        List<ColumnTemplate> columns = new ArrayList<ColumnTemplate>();
        for (Element columnElement : columnElements) {
            ColumnTemplate columnTemplate = readColumn(columnElement);
            columns.add(columnTemplate);
        }

        exportTemplate.setColumns(columns);
        return exportTemplate;
    }


    public static boolean isDBExport(String type) {
        return Exporter.TYPE_JDBC_BATCH.equalsIgnoreCase(type) || Exporter.TYPE_JDBC_POOL.equalsIgnoreCase(type) || Exporter.TYPE_JDBC_POOL_FAULT_TOLERANT.equalsIgnoreCase(type);
    }

    protected ExportTemplate parseExportTemplate() {
        return null;
    }
}
