package com.chh.dc.icp.warehouse.repository;

import com.chh.dc.icp.warehouse.ParsedRecord;
import com.chh.dc.icp.warehouse.WarehouseReport;
import com.chh.dc.icp.warehouse.exporter.Exporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;


public class SyncRepository implements Repository {

    private static final Logger LOG = LoggerFactory.getLogger(SyncRepository.class);

    protected Map<Integer, List<Exporter>> exports;

    @Override
    public long getReposId() {
        return 0;
    }

    @Override
    public int transport(Collection<ParsedRecord> parsedRecords) {
        return 0;
    }

    @Override
    public int transport(ParsedRecord parsedRecord) {
        return 0;
    }

    @Override
    public void commit() {

    }

    @Override
    public void rollBack() {

    }

    @Override
    public WarehouseReport getReport() {
        return null;
    }
}
