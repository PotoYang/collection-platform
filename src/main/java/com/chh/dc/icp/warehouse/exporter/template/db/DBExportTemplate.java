package com.chh.dc.icp.warehouse.exporter.template.db;

import com.chh.dc.icp.warehouse.exporter.template.ExportTemplate;

import java.util.List;

/**
 * Created by Niow on 2016/7/4.
 */
public class DBExportTemplate extends ExportTemplate {

    private int batchNumber = 500;

    private TableTemplate table;

    private List<ColumnTemplate> columns;

    public String getPreparedStatement() {
        return null;
    }

    public TableTemplate getTable() {
        return table;
    }

    public void setTable(TableTemplate table) {
        this.table = table;
    }

    public List<ColumnTemplate> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnTemplate> columns) {
        this.columns = columns;
    }

    public int getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(int batchNumber) {
        this.batchNumber = batchNumber;
    }
}
