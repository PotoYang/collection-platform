package com.chh.dc.icp.db.pojo;

import java.sql.Timestamp;
import java.util.Date;


public class TDevice implements java.io.Serializable {

    /**
     * 状态：在线
     */
    public static final int STATUS_ONLINE = 1;
    /**
     * 状态：失联
     */
    public static final int STATUS_MISSING = 2;
    // Fields

    /**
     * 数据中心设备统一ID
     */
    private String deviceUid;
    //	private Long companyId;
    private String sn;
//	private String deviceType;


    /**
     * 厂家设备ID
     */
    private String vender_device_uid;
    private Integer status;
    //	private Timestamp serviceStartTime;
//	private Timestamp createTime;
//	private Timestamp updateTime;
//	private Timestamp serviceEndTime;
//	private Date lastDfdataDate;
    private Timestamp lastTripEndTime;
//	private String password;

    // Constructors

    /**
     * default constructor
     */
    public TDevice() {
    }


    public String getDeviceUid() {
        return this.deviceUid;
    }

    public void setDeviceUid(String deviceUid) {
        this.deviceUid = deviceUid;
    }


    public String getSn() {
        return this.sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }
//
//	public String getDeviceType() {
//		return this.deviceType;
//	}
//
//	public void setDeviceType(String deviceType) {
//		this.deviceType = deviceType;
//	}

    public String getVender_device_uid() {
        return this.vender_device_uid;
    }

    public void setVender_device_uid(String vender_device_uid) {
        this.vender_device_uid = vender_device_uid;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

//	public Timestamp getServiceStartTime() {
//		return this.serviceStartTime;
//	}
//
//	public void setServiceStartTime(Timestamp serviceStartTime) {
//		this.serviceStartTime = serviceStartTime;
//	}
//
//	public Timestamp getCreateTime() {
//		return this.createTime;
//	}
//
//	public void setCreateTime(Timestamp createTime) {
//		this.createTime = createTime;
//	}
//
//	public Timestamp getUpdateTime() {
//		return updateTime;
//	}
//
//	public void setUpdateTime(Timestamp updateTime) {
//		this.updateTime = updateTime;
//	}
//
//	public Timestamp getServiceEndTime() {
//		return this.serviceEndTime;
//	}
//
//	public void setServiceEndTime(Timestamp serviceEndTime) {
//		this.serviceEndTime = serviceEndTime;
//	}
//
//	public Date getLastDfdataDate() {
//		return this.lastDfdataDate;
//	}
//
//	public void setLastDfdataDate(Date lastDfdataDate) {
//		this.lastDfdataDate = lastDfdataDate;
//	}


    public Timestamp getLastTripEndTime() {
        return this.lastTripEndTime;
    }

    public void setLastTripEndTime(Timestamp lastTripEndTime) {
        this.lastTripEndTime = lastTripEndTime;
    }


//	public String getPassword() {
//		return password;
//	}
//
//	public void setPassword(String password) {
//		this.password = password;
//	}


//	public Long getCompanyId() {
//		return companyId;
//	}
//
//
//
//	public void setCompanyId(Long companyId) {
//		this.companyId = companyId;
//	}


}