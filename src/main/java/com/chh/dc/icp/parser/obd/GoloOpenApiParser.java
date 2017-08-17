package com.chh.dc.icp.parser.obd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.chh.dc.icp.db.pojo.TaskInfo;
import com.chh.dc.icp.parser.obd.reader.goloopen.GoloDfdataStreamReader;
import com.chh.dc.icp.parser.obd.reader.goloopen.GoloMedicalReportReader;
import com.chh.dc.icp.parser.obd.reader.goloopen.GoloTripDataReader;
import com.chh.dc.icp.util.GoloUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chh.dc.icp.accessor.DataPackage;
import com.chh.dc.icp.accessor.model.GoloResp;
import com.chh.dc.icp.parser.AbstractParser;
import com.chh.dc.icp.parser.obd.reader.GoloObjectReader;
import com.chh.dc.icp.parser.obd.reader.goloopen.GoloGpsDataReader;
import com.chh.dc.icp.parser.obd.reader.goloopen.GoloTroubleCodeReader;
import com.chh.dc.icp.warehouse.ParsedRecord;

/**
 * 元征开放平台解析器
 *
 * @author fulr
 */
public class GoloOpenApiParser extends AbstractParser {

    public static final Logger log = LoggerFactory.getLogger(GoloOpenApiParser.class);

    private Map<Integer, GoloObjectReader> readerMap = new HashMap<Integer, GoloObjectReader>();

    {
        readerMap.put(GoloUtils.DATA_TYPE_DFDATA_STREAM, new GoloDfdataStreamReader());
        readerMap.put(GoloUtils.DATA_TYPE_GPS, new GoloGpsDataReader());
        readerMap.put(GoloUtils.DATA_TYPE_TROUBLE_CODE, new GoloTroubleCodeReader());
        readerMap.put(GoloUtils.DATA_TYPE_MEDICAL_REPORT, new GoloMedicalReportReader());
        readerMap.put(GoloUtils.DATA_TYPE_TRIP, new GoloTripDataReader());
    }

    private List<ParsedRecord> records;

    @Override
    public void parse(TaskInfo taskInfo, DataPackage data) throws Exception {
        records = new ArrayList<ParsedRecord>();
        Stack<GoloResp> res = (Stack<GoloResp>) data.getData();
        ParsedRecord rec = null;
        log.debug("采集golo数据{}条", res.size());
        GoloResp item = null;
        while (!res.isEmpty()) {
            item = res.pop();
            GoloObjectReader reader = readerMap.get(item.getType());
            log.debug("结果类型:{},readType:{},遍历返回结果：{}", item.getType(), reader.getClass(), item.getMsg());
            if (GoloUtils.ERR_CODE_SUCCESS == item.getCode()) {
                List<ParsedRecord> recs = reader.readRecords(item);
                if (recs != null) {
                    this.recievedNum += recs.size();
                    records.addAll(recs);
                }
            }
        }

    }

    @Override
    public ParsedRecord readRecord() throws Exception {
        if (records != null && records.size() > 0) {
            return records.remove(0);
        }
        return null;
    }

    @Override
    public void beforeParse(TaskInfo taskInfo, DataPackage data)
            throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void afterParse() throws Exception {
        // TODO Auto-generated method stub

    }

}
