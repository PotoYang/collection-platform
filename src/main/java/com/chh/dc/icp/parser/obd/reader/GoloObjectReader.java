package com.chh.dc.icp.parser.obd.reader;

import java.util.List;

import com.chh.dc.icp.accessor.model.GoloResp;
import com.chh.dc.icp.warehouse.ParsedRecord;

public interface GoloObjectReader {

    public List<ParsedRecord> readRecords(GoloResp resp) throws Exception;

}
