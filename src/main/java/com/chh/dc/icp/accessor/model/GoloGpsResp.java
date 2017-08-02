package com.chh.dc.icp.accessor.model;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.chh.dc.icp.util.DateUtil;

public class GoloGpsResp extends GoloResp {

	private List<GpsItem> data;
	
	
	public JSONObject getData() {
		return (JSONObject) JSON.toJSON(data);
	}

	public void setData(String data) {
		try {
			this.data = JSONObject.parseArray(data,GpsItem.class);
		} catch (Exception e) {
			this.data = null;
		}
	}
	
	public List<GpsItem> getGpsList(){
		return this.data;
	}

	
	public static class GpsItem {
		
		private Double Latitude;
		
		private Double Longitude;
		
		private Date GPSTimeInDefaultTimeZone;
		
		private String address;

		public Double getLatitude() {
			return Latitude;
		}

		public void setLatitude(Double latitude) {
			Latitude = latitude;
		}

		public Double getLongitude() {
			return Longitude;
		}

		public void setLongitude(Double longitude) {
			Longitude = longitude;
		}

		public Date getGPSTimeInDefaultTimeZone() {
			return GPSTimeInDefaultTimeZone;
		}
		
		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public void setGPSTimeInDefaultTimeZone(String gPSTimeInDefaultTimeZone) throws ParseException {
			GPSTimeInDefaultTimeZone = DateUtil.parseGoloDate(gPSTimeInDefaultTimeZone);
		}
		
		public GpsItem4Trip toGpsItem4Trip(){
			GpsItem4Trip g4 = new GpsItem4Trip();
			g4.setLa(Latitude);
			g4.setLo(Longitude);
			g4.setD(GPSTimeInDefaultTimeZone);
			return g4;
		}
	}
	
	public static class GpsItem4Trip {
		
		private Double la;
		
		private Double lo;
		
		@JSONField (format="yyyy-MM-dd HH:mm:ss")  
		private Date d;

		public Double getLa() {
			return la;
		}

		public void setLa(Double la) {
			this.la = la;
		}

		public Double getLo() {
			return lo;
		}

		public void setLo(Double lo) {
			this.lo = lo;
		}

		public Date getD() {
			return d;
		}

		public void setD(Date d) {
			this.d = d;
		}
		
	}
}
