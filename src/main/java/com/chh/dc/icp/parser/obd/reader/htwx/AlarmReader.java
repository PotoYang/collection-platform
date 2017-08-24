package com.chh.dc.icp.parser.obd.reader.htwx;

import static com.chh.dc.icp.util.ByteReaderUtil.readHexString;
import static com.chh.dc.icp.util.ByteReaderUtil.readU16;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.chh.dc.icp.util.ByteReaderUtil;
import com.chh.dc.icp.util.DateUtil;
import com.chh.dc.icp.util.OBDAlarmCodeConverter;
import com.chh.dc.icp.warehouse.ParsedRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by Niow on 2016/8/26.
 */
public class AlarmReader extends HTWXReader {
    private static final Logger log = LoggerFactory.getLogger(AlarmReader.class);
    private String deviceType = "3";
    private Integer gpsLocateModel = 1;

    @Override
    public List<ParsedRecord> readRecord(byte[] bs) {
        String deviceId = readDeviceId(bs);
        String uid = deviceType + deviceId;
        List<ParsedRecord> list = new ArrayList<ParsedRecord>();
        Map<String, Object> statDataMap = new HashMap<String, Object>();
        int index = INDEX_DATA;
        //alarm_num
//        long alarmNum = readU32(bs, index);
        index += 4;
        //stat_data
        index = readStatData(statDataMap, bs, index);
        //gps_data
        List<ParsedRecord> gpsData = readGPSData(bs, index);
        Object lat = null;
        Object lon = null;
        if (gpsData != null && gpsData.size() > 0) {
            index += 1 + gpsData.size() * 19;
            //如果存在gps数据，取第一条数据作为当前gps（正常情况，这里只会返回一条）
            Map<String, Object> curGps = gpsData.get(0).getRecord();
            lat = curGps.get("lat");
            lon = curGps.get("lon");
        }
        Date lastAcconTime = (Date) statDataMap.get("last_accon_time");
        Object lastAcconTimeSec = statDataMap.get("last_accon_time_sec");
        Object utctime = statDataMap.get("utctime");
        Date collectionTime = (Date) statDataMap.get("collection_time");
        //alam_code
        int alarm_count = ByteReaderUtil.readU8(bs, index++);
        for (int i = 0; i < alarm_count; i++) {
            //原始数据
            ParsedRecord alarmData = new ParsedRecord("htwx_alarm");
            Map<String, Object> alarmMap = alarmData.getRecord();
            index = readAlarmItem(alarmMap, bs, index);

            String alarmType = (String) alarmMap.get("alarm_type");
            int alarmId = OBDAlarmCodeConverter.getCommonAlarmIdByHtwxId(alarmType);
            if (alarmId == 0) {
                log.warn("告警类型不匹配{}", alarmType);
                continue;
            }
            String warningDesc = OBDAlarmCodeConverter.getCommonAlarmDesc(alarmId);
            alarmMap.put("device_id", deviceId);
//            alarmMap.put("utctime", utctime);
            alarmMap.put("warning_time", utctime);

            alarmMap.put("collection_time", collectionTime);
            alarmMap.put("device_uid", uid);
            alarmMap.put("last_accon_time", lastAcconTime);
            alarmMap.put("last_accon_time_sec", lastAcconTimeSec);
//            alarmMap.put("lat", lat);
//            alarmMap.put("lon", lon);
            alarmMap.put("latitude", lat);
            alarmMap.put("longitude", lon);
            alarmMap.put("id", UUID.randomUUID().toString());
            alarmMap.put("gps_locate_model", gpsLocateModel);
            alarmMap.put("warning_type", alarmId);
            alarmMap.put("warning_value", alarmMap.get("alarm_desc"));
            alarmMap.put("warning_desc", warningDesc);
            list.add(alarmData);
            //警情标志 =0 结束的警情 =1 新警情
            int newAlarmFlag = (int) alarmMap.get("new_alarm_flag");
            if (newAlarmFlag == 0) {
                log.debug("结束的警情:alarmId={},warning_desc={}", alarmId, warningDesc);
                continue;
            }
            //汇总数据
            ParsedRecord deviceWarningData = new ParsedRecord("device_warning");
            deviceWarningData.setRecord(alarmMap);
            list.add(deviceWarningData);
            //推送业务系统
            ParsedRecord warningData = new ParsedRecord("htwx_warning_to_stat");
            Map<String, Object> warningMap = warningData.getRecord();
            warningMap.put("device_uid", uid);
            warningMap.put("device_id", deviceId);
            warningMap.put("last_accon_time", lastAcconTime);
            warningMap.put("last_accon_time_sec", lastAcconTimeSec);
            warningMap.put("utctime", utctime);

            //熄火告警,输出待汇总告警数据
            if ("0x17".equals(alarmType)) {
                warningMap.put("warning_type", alarmId);
                warningMap.put("warning_desc", OBDAlarmCodeConverter.getCommonAlarmDesc(alarmId));
                warningMap.put("collection_time", collectionTime);
                list.add(warningData);
            } else if ("0x0e".equals(alarmType)) {//接头拔出
                warningMap.put("warning_type", 65);
                warningMap.put("warning_value", DateUtil.getDateTimeString(collectionTime));
                warningMap.put("warning_desc", "失联报警");
                warningMap.put("collection_time", collectionTime);
                list.add(warningData);
            }
        }
        return list;
    }

    protected int readAlarmItem(Map<String, Object> map, byte[] bs, int start) {
        int index = start;
        //警情标志 =0 结束的警情 =1 新警情
        map.put("new_alarm_flag", ByteReaderUtil.readU8(bs, index++));
        String type = ByteReaderUtil.readHexString(bs[index++]);
        map.put("alarm_type", "0x" + type);
        //告警需要直接
        if ("04".equals(type)) {
            int currentValocity = ByteReaderUtil.readU8(bs, index++);
            int secondBeforeValocity = ByteReaderUtil.readU8(bs, index++);
            if (currentValocity == 255) {
                map.put("alarm_desc", secondBeforeValocity);
            } else {
                int a = currentValocity - secondBeforeValocity;
                map.put("alarm_desc", a);
            }
        } else if ("05".equals(type)) {
            int currentValocity = ByteReaderUtil.readU8(bs, index++);
            int secondBeforeValocity = ByteReaderUtil.readU8(bs, index++);
            if (currentValocity == 255) {
                map.put("alarm_desc", secondBeforeValocity);
            } else {
                int a = secondBeforeValocity - currentValocity;
                map.put("alarm_desc", a);
            }
        } else {
            map.put("alarm_desc", ByteReaderUtil.readU16(bs, index, true));
            index += 2;
        }
        map.put("alarm_threshold", ByteReaderUtil.readU16(bs, index, true));
        index += 2;
        return index;
    }


}
