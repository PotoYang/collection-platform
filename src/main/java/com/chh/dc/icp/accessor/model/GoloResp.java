package com.chh.dc.icp.accessor.model;

import com.alibaba.fastjson.JSON;
import com.chh.dc.icp.db.pojo.TDevice;
import com.chh.dc.icp.accessor.model.GoloTripRecordByPage.TripRecordItem;

public class GoloResp {

	private int code;
	
	private String msg;
	
	private Object data;
	
	private Integer type;

//    /**
//     * 数据中心设备统一ID
//     */
//	private Long deviceUid;
    /**
     * 设备信息
     */
    private TDevice device;

	/**
	 * 当前数据对应的行程数据
	 */
	private TripRecordItem trip;


	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(String data) {
		try {
			this.data = JSON.parse(data);
		} catch (Exception e) {
			this.data = null;
		}
//		this.data = data;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

//	public Long getDeviceUid() {
//		return deviceUid;
//	}
//
//	public void setDeviceUid(Long deviceUid) {
//		this.deviceUid = deviceUid;
//	}


	public TripRecordItem getTrip() {
		return trip;
	}

	public void setTrip(TripRecordItem trip) {
		this.trip = trip;
	}

    public TDevice getDevice() {
        return device;
    }

    public void setDevice(TDevice device) {
        this.device = device;
    }
}
