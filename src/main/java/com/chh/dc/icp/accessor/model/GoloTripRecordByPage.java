package com.chh.dc.icp.accessor.model;

import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chh.dc.icp.util.DateTimeUtils;

public class GoloTripRecordByPage extends GoloResp  {

	private TripRecordData data;
	
	public static final int PAGE_SIZE = 10;
	
	public JSONObject getData() {
		return (JSONObject) JSON.toJSON(data);
	}

	public void setData(String data) {
		try {
			this.data = JSONObject.parseObject(data,TripRecordData.class);
		} catch (Exception e) {
			this.data = null;
		}
	}
	
	public TripRecordData getTripData(){
		return this.data;
	}
	
	
	
	public static class TripRecordData{
		
		private int count;
		
		private List<TripRecordItem> list;

		
		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

		public List<TripRecordItem> getList() {
			return list;
		}

		public void setList(List<TripRecordItem> list) {
			this.list = list;
		}
		
	}
	
	public static class TripRecordItem {
		
		private String OTripRecordUID;
		
		private String DeviceUID;
		
		private String DeviceSN;
		
		private String TripSN;
		
		private Long GPSLongValue;
		
		private Double TripMileage;
		
		private Double FuelConsumption;
		
		private Double AvgFuelConsumption;
		
		private Date TripStartTime;
		
		private Date TripEndTime;

		private Double longitude_start;

		private Double longitude_end;

		private Double latitude_start;

		private Double latitude_end;

		/**
		 * 存放临时统计数据
		 */
		private JSONObject extrasData = new JSONObject();

		public String getOTripRecordUID() {
			return OTripRecordUID;
		}

		public void setOTripRecordUID(String oTripRecordUID) {
			OTripRecordUID = oTripRecordUID;
		}

		public String getDeviceUID() {
			return DeviceUID;
		}

		public void setDeviceUID(String deviceUID) {
			DeviceUID = deviceUID;
		}

		public String getDeviceSN() {
			return DeviceSN;
		}

		public void setDeviceSN(String deviceSN) {
			DeviceSN = deviceSN;
		}

		public String getTripSN() {
			return TripSN;
		}

		public void setTripSN(String tripSN) {
			TripSN = tripSN;
		}

		public Long getGPSLongValue() {
			return GPSLongValue;
		}

		public void setGPSLongValue(Long gPSLongValue) {
			GPSLongValue = gPSLongValue;
		}

		public Double getTripMileage() {
			return TripMileage;
		}

		public void setTripMileage(Double tripMileage) {
			TripMileage = tripMileage;
		}

		public Double getFuelConsumption() {
			return FuelConsumption;
		}

		public void setFuelConsumption(Double fuelConsumption) {
			FuelConsumption = fuelConsumption;
		}

		public Double getAvgFuelConsumption() {
			return AvgFuelConsumption;
		}

		public void setAvgFuelConsumption(Double avgFuelConsumption) {
			AvgFuelConsumption = avgFuelConsumption;
		}

		public Date getTripStartTime() {
			return TripStartTime;
		}

		public void setTripStartTime(String tripStartTime) throws Exception {
			TripStartTime = DateTimeUtils.parseGoloDate(tripStartTime);
		}

		public Date getTripEndTime() {
			return TripEndTime;
		}

		public void setTripEndTime(String tripEndTime) throws Exception {
			TripEndTime = DateTimeUtils.parseGoloDate(tripEndTime);
		}


		public Double getLongitude_start() {
			return longitude_start;
		}

		public void setLongitude_start(Double longitude_start) {
			this.longitude_start = longitude_start;
		}

		public Double getLongitude_end() {
			return longitude_end;
		}

		public void setLongitude_end(Double longitude_end) {
			this.longitude_end = longitude_end;
		}

		public Double getLatitude_start() {
			return latitude_start;
		}

		public void setLatitude_start(Double latitude_start) {
			this.latitude_start = latitude_start;
		}

		public Double getLatitude_end() {
			return latitude_end;
		}

		public void setLatitude_end(Double latitude_end) {
			this.latitude_end = latitude_end;
		}



		public Object getExtrasData(String key) {
			return extrasData.get(key);
		}

		public void putExtrasData(String key, Object value) {
			this.extrasData.put(key,value);
		}
	}
}
