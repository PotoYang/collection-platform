package com.chh.dc.icp.parser.obd.reader.htwx;

import static com.chh.dc.icp.util.ByteReaderUtil.readHexString;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chh.dc.icp.util.ByteReaderUtil;
import com.chh.dc.icp.util.OBDAlarmCodeConverter;
import com.chh.dc.icp.util.StringUtil;
import com.chh.dc.icp.warehouse.ParsedRecord;

/**
 * Created by Niow on 2016/9/9.
 */
public class FaultReader extends HTWXReader {

	private String deviceType = "3";


    @Override
    public  List<ParsedRecord> readRecord(byte[] bs) {

        List<ParsedRecord> list = new ArrayList<ParsedRecord>();
        Map<String, Object> statDataMap = new HashMap<>();
        int index = INDEX_DATA;
        //stat_data
        index = readStatData(statDataMap, bs, index);
        //fault_code
        List<ParsedRecord> faultList = readFaultCode(bs, index);
        
        if(faultList != null){
        	String uid = deviceType + statDataMap.get("device_id");
            Object deviceId = statDataMap.get("device_id");
            Object utctime = statDataMap.get("utctime");
            Date lastAcconTime = (Date) statDataMap.get("last_accon_time");
            Object lastAcconTimeSec = statDataMap.get("last_accon_time_sec");
            Object collectionTime = statDataMap.get("collection_time");
            
            for (ParsedRecord record : faultList) {
            	Map<String, Object> faultMap = record.getRecord();
            	faultMap.put("device_id",deviceId);
                faultMap.put("utctime",utctime);
                faultMap.put("last_accon_time",lastAcconTime);
                faultMap.put("last_accon_time_sec",lastAcconTimeSec);
                faultMap.put("device_uid", uid);
                faultMap.put("collection_time", collectionTime);
                list.add(record);
                //待汇总告警数据
                ParsedRecord warningRecord = new ParsedRecord("htwx_warning_to_stat");
               	Map<String, Object> warningMap = warningRecord.getRecord();
               	warningMap.put("device_id",deviceId);
               	warningMap.put("device_uid", uid);
               	warningMap.put("utctime",utctime);
               	warningMap.put("last_accon_time",lastAcconTime);
               	warningMap.put("last_accon_time_sec",lastAcconTimeSec);
               	warningMap.put("warning_type", OBDAlarmCodeConverter.HTWX_ALARM_FAULT_CODE);
               	warningMap.put("collection_time", collectionTime);
                warningMap.put("warning_desc", faultMap.get("fault_desc"));
                list.add(warningRecord);
			}
        }
        return list ;
    }

    protected  List<ParsedRecord> readFaultCode(byte[] bs, int start) {
        int index = start;
        List<ParsedRecord> faultList = new ArrayList<>();
        
        int faultFlag = ByteReaderUtil.readU8(bs, index);
        index += 1;
        int faultCount = ByteReaderUtil.readU8(bs, index++);
        //故障码数量大于0，才返回
        if(faultCount > 0){
            for (int i = 0; i < faultCount; i++) {
                String b2 = ByteReaderUtil.readHexString(bs[index++]);
                byte b = bs[index++];
                String b1 = ByteReaderUtil.readHexString(b);
                int bit1 = ByteReaderUtil.readBit(b, 0);
                int bit2 = ByteReaderUtil.readBit(b, 1);
                int bit3 = ByteReaderUtil.readBit(b, 2);
                int bit4 = ByteReaderUtil.readBit(b, 3);
                int firstNum = Integer.parseInt(bit3 + "" + bit4, 2);
                String letter = mapChar(bit1 + "" + bit2);
                String  codeStr = letter + firstNum + b1.substring(1) + b2;
                ParsedRecord record = new ParsedRecord("htwx_fault");
                Map<String, Object> faultMap = record.getRecord();
                faultMap.put("fault_flag", faultFlag);
                faultMap.put("fault_code", codeStr);
                String desc = OBDAlarmCodeConverter.getDtcDescByCode(codeStr);
               	desc = StringUtil.isEmpty(desc) ? codeStr : desc;
                faultMap.put("fault_desc", desc);
                faultList.add(record);
            }
            return faultList;
        }
        return null;
    }

    private String mapChar(String number) {
        switch (number) {
            case "00":return "P";
            case "01":return "C";
            case "10":return "B";
            case "11":return "U";
        }
        return "ERROR";
    }
}
