package com.chh.dc.icp.parser.obd.reader.goloopen;

import java.sql.Timestamp;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chh.dc.icp.accessor.model.GoloResp;
import com.chh.dc.icp.util.StringUtil;
import com.chh.dc.icp.parser.obd.reader.GoloObjectReader;
import com.chh.dc.icp.util.DateUtil;
import com.chh.dc.icp.util.OBDAlarmCodeConverter;
import com.chh.dc.icp.warehouse.ParsedRecord;

/**
 * golo open 故障码
 *
 * @author fulr
 */
public class GoloTroubleCodeReader implements GoloObjectReader {

    @Override
    public List<ParsedRecord> readRecords(GoloResp resp) throws Exception {

        Object da = resp.getData();
        if (da instanceof JSONArray) {
            List<ParsedRecord> res = new ArrayList<ParsedRecord>();
            JSONArray ja = (JSONArray) da;
            for (Iterator<Object> it = ja.iterator(); it.hasNext(); ) {
                res.addAll(readRecords(resp, (JSONObject) it.next()));
            }
            return res;
        } else if (da instanceof JSONObject) {
            return readRecords(resp, (JSONObject) da);
        } else {
            //数据格式错误
            //TODO
            return null;
        }
    }

    private List<ParsedRecord> readRecords(GoloResp resp, JSONObject js) throws Exception {
        List<ParsedRecord> res = new ArrayList<ParsedRecord>();
        JSONArray contentList = JSON.parseArray(js.getString("JsonContent"));
        for (Iterator it = contentList.iterator(); it.hasNext(); ) {
            JSONObject conItem = (JSONObject) it.next();
            JSONArray ODTCValueList = JSON.parseArray(conItem.getString("jsonODTCValue"));
            for (Iterator tt = ODTCValueList.iterator(); tt.hasNext(); ) {
                JSONObject dtcObj = (JSONObject) tt.next();
                ParsedRecord record = new ParsedRecord("yz_fault");
//                record.setType("yz_fault");
//                Map<String, Object> map = record.getRecord();
                record.putData("create_time", new Timestamp(System.currentTimeMillis()));
                String dtcValue = dtcObj.getString("DTCValue");
                record.putData("dtc_value", dtcValue);
                record.putData("dtc_status", dtcObj.getString("DTCStatus"));
//				故障码描述
                String desc = OBDAlarmCodeConverter.getDtcDescByCode(dtcValue);
                desc = StringUtil.isEmpty(desc) ? dtcValue : desc;
                record.putData("dtc_description", desc);
                Date gpsTime = DateUtil.parseGoloDate(js.getString("GPSTime"));
                record.putData("gps_time", gpsTime);
                record.putData("device_uid", resp.getDevice().getDeviceUid());
                res.add(record);

                //添加故障码告警
                record = new ParsedRecord("yz_alarm");
                record.putData("device_uid", resp.getDevice().getDeviceUid());
                record.putData("id", UUID.randomUUID().toString());
                record.putData("warning_type", OBDAlarmCodeConverter.HTWX_ALARM_FAULT_CODE);
                record.putData("warning_desc", desc);
                record.putData("warning_time", gpsTime);
                record.putData("create_time", new Timestamp(System.currentTimeMillis()));
                res.add(record);
            }
        }
        return res;
    }

//	public static void main(String[] args){
//		String str = "[{\"SystemIDOnCar\":4294967295,\"SystemTextID\":\"\",\"SystemNameOnCar\":\"OBD\",\"SystemTypeID\":\"\",\"SystemTypeName\":\"\",\"jsonODTCValue\":\"[{\\\"DTCID\\\":\\\"00000236\\\",\\\"DTCValue\\\":\\\"P0236\\\",\\\"DTCDescription\\\":\\\"涡轮增压器/机械增压器增压传感器A电路范围/性能故障\\\",\\\"DTCHelp\\\":\\\"\\\",\\\"DTCStatus\\\":\\\"00000002\\\"}]\"}]";
//
//		JSONArray ss = (JSONArray) JSON.parse(str);
//		Object jsonODTCValue = ((JSONObject)ss.get(0)).get("jsonODTCValue");
//		JSONArray dd = (JSONArray) JSON.parse((String) jsonODTCValue);
//		System.out.println(((JSONObject)dd.get(0)).get("DTCValue"));
//
//
//	}
}
