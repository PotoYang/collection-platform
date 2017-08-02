package com.chh.dc.icp.parser.obd.reader.goloopen;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chh.dc.icp.accessor.model.GoloResp;
import com.chh.dc.icp.parser.obd.reader.GoloObjectReader;
import com.chh.dc.icp.util.DateUtil;
import com.chh.dc.icp.warehouse.ParsedRecord;

/**
 * golo open api 实时数据流
 * @author fulr
 *
 */
public class GoloDfdataStreamReader implements GoloObjectReader {

	/**
	 * id,字段名
	 */
	public static Map<String,String> dfDataMap = new HashMap<String,String>();
	
	static {
		dfDataMap.put("00000001","lamp_far");
		dfDataMap.put("00000002","lamp_near");
		dfDataMap.put("00000008","lamp_width");
		dfDataMap.put("000002A5","lamp_fog");
		dfDataMap.put("00000509","lamp_left_turn");
		dfDataMap.put("0000050A","lamp_right_turn");
		dfDataMap.put("0000050B","lamp_danger");
		dfDataMap.put("00000180","door_left_front");
		dfDataMap.put("00000188","door_right_front");
		dfDataMap.put("00000190","door_left_rear");
		dfDataMap.put("00000198","door_right_rear");
		dfDataMap.put("000001E0","door_trunk");
		dfDataMap.put("0000050C","lock_whole_car");
		dfDataMap.put("00000181","lock_left_front");
		dfDataMap.put("00000189","lock_right_front");
		dfDataMap.put("00000191","lock_left_rear");
		dfDataMap.put("00000199","lock_right_rear");
		dfDataMap.put("0000050D","lock_trunk");
		dfDataMap.put("000001B0","window_left_front");
		dfDataMap.put("000001B8","window_right_front");
		dfDataMap.put("000001C0","window_left_rear");
		dfDataMap.put("000001C8","window_right_rear");
		dfDataMap.put("000001D8","window_dormer");
		dfDataMap.put("000002A1","fault_signal_ecm");
		dfDataMap.put("00000295","fault_signal_abs");
		dfDataMap.put("0000029D","fault_signal_srs");
		dfDataMap.put("0000029A","fault_signal_oil");
		dfDataMap.put("00000508","fault_signal_pressure");
		dfDataMap.put("000002AA","fault_signal_maintenance");
		dfDataMap.put("00000360","brake_hand");
		dfDataMap.put("00000015","brake_foot");
		dfDataMap.put("000002C0","seat_belts_driver");
		dfDataMap.put("000002C4","seat_belts_copilot");
		dfDataMap.put("0000050E","acc_status");
		dfDataMap.put("00000342","key_status");
		dfDataMap.put("0000050F","remote_control_signal");
		dfDataMap.put("00000510","wiper_status");
		dfDataMap.put("00000370","air_conditioning_status");
		dfDataMap.put("00000281","gear");
		dfDataMap.put("00000290","all_mileage");
		dfDataMap.put("00000511","endurance_mileage");
		dfDataMap.put("00000512","fuel_consumption");
		dfDataMap.put("00000305","water_temperature");
		dfDataMap.put("00000303","engine_inlet_temperature");
		dfDataMap.put("00000373","air_conditioning_temperature");
		dfDataMap.put("000001F0","battery_voltage");
		dfDataMap.put("00000100","wheel_speed_left_front");
		dfDataMap.put("00000110","wheel_speed_right_front");
		dfDataMap.put("00000120","wheel_speed_left_rear");
		dfDataMap.put("00000130","wheel_speed_right_rear");
		dfDataMap.put("0000030B","speed");
		dfDataMap.put("00000300","rotating_speed");
		dfDataMap.put("0000040F","fuel_consumption_average");
		dfDataMap.put("00000514","fuel_consumption_instant_km");
		dfDataMap.put("00000513","fuel_consumption_instant_h");
		dfDataMap.put("00000404","oil_lifetime");
		dfDataMap.put("0000041E","air_flow");
		dfDataMap.put("0000041F","MAP");
		dfDataMap.put("00000515","accelerator_pedal_relative_position");
		dfDataMap.put("00000516","accelerator");
		dfDataMap.put("00000350","steering_wheel_angle");
		dfDataMap.put("00000351","steering_wheel_status");
		dfDataMap.put("0000040C","residual_oil_volume_after_filtering_l");
		dfDataMap.put("0000040D","residual_oil_volume_after_filtering_p");
		dfDataMap.put("0000051A","total_mileage");
		dfDataMap.put("0000051B","trip_uid");
		dfDataMap.put("00000AF0","apk_battery_voltage");
		dfDataMap.put("00000AF1","acceleration");
		dfDataMap.put("0000051C","brake_pedal_relative_position");

	}
	
	@Override
	public List<ParsedRecord> readRecords(GoloResp resp) throws Exception {
		Object da = resp.getData();
		if(da instanceof JSONObject) {
			Object data = ((JSONObject)da).get("Data");
			if(data instanceof JSONArray) {
				List<ParsedRecord> res = new ArrayList<ParsedRecord>();
				JSONArray ja = (JSONArray) data;
				for(Iterator<Object> it=ja.iterator();it.hasNext();){
					res.add(readRecord(resp,(JSONObject)it.next()));
				}
				return res;
			} 
			return null;
		} else {
			//数据格式错误
			return null;
		} 
	}

	private ParsedRecord readRecord(GoloResp resp, JSONObject js) throws Exception {
		ParsedRecord record = new ParsedRecord();
		record.setType("yz_realtime_data");
        Map<String, Object> map = record.getRecord();
        map.put("gps_time", DateUtil.getDate1(js.getString("GPSTimeInDefaultTimeZone")));
        map.put("device_id", resp.getDevice().getDeviceUid());
        map.put("create_time", new Timestamp(System.currentTimeMillis()));
        JSONArray attrList = JSON.parseArray(js.getString("DFJsonContent"));
        JSONObject item = null;
        String id=null,value = null,columnName = null;
        for(Iterator it = attrList.iterator();it.hasNext();){
			item = (JSONObject) it.next();
			id = item.getString("DFDataStreamID");
			value = item.getString("DFDataStreamValue");
			columnName = dfDataMap.get(id);
			if(columnName!=null){
				map.put(columnName, value);
			}
        }
        
		return record;
	}

}
