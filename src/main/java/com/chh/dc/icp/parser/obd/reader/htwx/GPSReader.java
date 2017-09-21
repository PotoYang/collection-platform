package com.chh.dc.icp.parser.obd.reader.htwx;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chh.dc.icp.warehouse.ParsedRecord;

/**
 * Created by Niow on 2016/8/26.
 */
public class GPSReader extends HTWXReader {

    private String deviceType = "3";
    private Integer gpsLocateModel = 1;

    /**
     * flag->stat_data->gpsdata->rpmdata
     */
    @Override
    public List<ParsedRecord> readRecord(byte[] bs) {
        String deviceId = readDeviceId(bs);
        List<ParsedRecord> list = new ArrayList<ParsedRecord>();
        int index = INDEX_DATA;
        //flag 0x00 表示常规 GPS 数据上传 0x01 表示历史 GPS 数据上传
//        int flag = readU8(bs, index);
        index += 1;

        //stat_data
        Map<String, Object> statDataMap = new HashMap<String, Object>();
        index = readStatData(statDataMap, bs, index);

        Date last_accon_time = (Date) statDataMap.get("last_accon_time");
        Date utctime = (Date) statDataMap.get("utctime");

        //gpsdata
        List<ParsedRecord> gpsData = readGPSData(bs, index);
        if (gpsData != null && gpsData.size() > 0) {
            index += 1 + gpsData.size() * 19;
            for (int i = 0; i < gpsData.size(); i++) {
                ParsedRecord record = gpsData.get(i);
                Map<String, Object> gpsItem = record.getRecord();
                gpsItem.put("last_accon_time", last_accon_time);
                gpsItem.put("last_accon_time_sec", statDataMap.get("last_accon_time_sec"));
                gpsItem.put("device_id", deviceId);
                gpsItem.put("gps_locate_model", gpsLocateModel);
                gpsItem.put("utctime", utctime);
                //将stat_data数据写入，最后一条gps_item
                if (i == (gpsData.size() - 1)) {
                    gpsItem.put("total_trip_mileage", statDataMap.get("total_trip_mileage"));
                    gpsItem.put("current_trip_milea", statDataMap.get("current_trip_milea"));
                    gpsItem.put("total_fuel", statDataMap.get("total_fuel"));
                    gpsItem.put("current_fuel", statDataMap.get("current_fuel"));
                    gpsItem.put("vstate", statDataMap.get("vstate"));
                    gpsItem.put("reserve", statDataMap.get("reserve"));
                }
            }
            list.addAll(gpsData);
        }
        //rpmdata

        return list;
    }


}
