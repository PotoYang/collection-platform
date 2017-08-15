package com.chh.dc.icp.parser.obd;

import com.chh.dc.icp.accessor.DataPackage;
import com.chh.dc.icp.db.pojo.TaskInfo;
import com.chh.dc.icp.parser.AbstractParser;
import com.chh.dc.icp.parser.obd.reader.ByteArrayReader;
import com.chh.dc.icp.parser.obd.reader.dna.*;
//import com.tcl.paas.icp.parser.obd.reader.dna.*;
import com.chh.dc.icp.warehouse.ParsedRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Niow on 2016/7/12.
 */
public class DNAGID22Parser extends AbstractParser {

    private Map<Integer, ByteArrayReader> readerMap = new HashMap<Integer, ByteArrayReader>();

    private List<ParsedRecord> list = null;

    {
        readerMap.put(0, new GPSReader());
        readerMap.put(1, new CANReader());
        readerMap.put(2, new FaultReader());
        readerMap.put(4, new AlarmReader());
    }

    @Override
    public void parse(TaskInfo taskInfo, DataPackage data) throws Exception {
        byte[] bs = (byte[]) data.getData();
        int dataType = DNAReader.readFeatureId(bs);
        ByteArrayReader reader = readerMap.get(dataType);
        if (reader == null) {
            return;
        }
        this.list = reader.readRecord(bs);
    }

    @Override
    public ParsedRecord readRecord() {
        if (list == null || list.isEmpty()) {
            return null;
        }
        //只能读取一次
        ParsedRecord record = list.remove(0);
        return record;
    }

    @Override
    public void beforeParse(TaskInfo taskInfo, DataPackage data) throws Exception {

    }

    @Override
    public void afterParse() throws Exception {

    }

    protected ParsedRecord readGPS(byte[] bs) {
        return null;
    }

}
