package com.chh.dc.icp.parser;

import com.chh.dc.icp.accessor.DataPackage;
import com.chh.dc.icp.db.pojo.TaskInfo;
import com.chh.dc.icp.warehouse.ParsedRecord;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Niow on 2016/7/12.
 */
public class TestDbParser extends AbstractParser {

    private static final String DATA_TYPE_TEST_DATA = "test_data";

    private ResultSet rs;


    @Override
    public void beforeParse(TaskInfo taskInfo, DataPackage data) throws Exception {

    }

    @Override
    public void afterParse() throws Exception {

    }

    @Override
    public void parse(TaskInfo taskInfo, DataPackage data) throws Exception {
        rs = (ResultSet) data.getData();
    }

    @Override
    public ParsedRecord readRecord() throws Exception {
        boolean hasNext = rs.next();
        if (!hasNext) {
            return null;
        }
        recievedNum++;
        Map<String, Object> readData = readData(rs);
        ParsedRecord record = new ParsedRecord();
        record.setRecord(readData);
        record.setType(DATA_TYPE_TEST_DATA);
        return record;
    }

    private Map<String, Object> readData(ResultSet rs) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", rs.getObject("id"));
        map.put("description", rs.getObject("description"));
        map.put("create_time", rs.getTimestamp("create_time"));
        return map;
    }


}
