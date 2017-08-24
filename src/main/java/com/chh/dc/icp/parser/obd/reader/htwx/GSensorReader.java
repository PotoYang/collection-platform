package com.chh.dc.icp.parser.obd.reader.htwx;

import com.chh.dc.icp.warehouse.ParsedRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.chh.dc.icp.util.ByteReaderUtil.readU16;
import static com.chh.dc.icp.util.ByteReaderUtil.readU8;

/**
 * Created by Niow on 2016/9/13.
 */
public class GSensorReader extends HTWXReader {


    @Override
    public List<ParsedRecord> readRecord(byte[] bs) {
        String deviceId = readDeviceId(bs);
        List<ParsedRecord> list = new ArrayList<ParsedRecord>();
        ParsedRecord statData = new ParsedRecord("htwx_stat");
        Map<String, Object> statDataMap = statData.getRecord();
        int index = INDEX_DATA;
        //stat_data
        index = readStatData(statDataMap, bs, index);
        statDataMap.put("device_id", deviceId);
        list.add(statData);
        //gsensor_data
        int collectInterval = readU16(bs, index, true);
        index += 2;
        int groupCount = readU8(bs, index);
        for (int i = 0; i < groupCount; i++) {

        }
        return null;
    }

    protected int readGSensorData(byte[] bs, Map<String, Object> map, int start) {
        int index = start;
//        map.put("")
        return index;
    }
}
