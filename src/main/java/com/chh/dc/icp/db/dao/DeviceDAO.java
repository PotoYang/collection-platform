package com.chh.dc.icp.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.alibaba.fastjson.JSONObject;
import com.chh.dc.icp.db.pojo.TaskInfo;
import com.chh.dc.icp.util.DBUtil;
import com.chh.dc.icp.util.StringUtil;
import com.chh.dc.icp.db.pojo.TDevice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeviceDAO {

    public static final Logger log = LoggerFactory.getLogger(DeviceDAO.class);

    private DataSource dataSource;

//    public List getDeviceList(TaskInfo taskInfo) throws Exception {
//    	List paramsList = new ArrayList();
//        List<TDevice> deviceList = new ArrayList<TDevice>();
//        Connection connection = null;
//        PreparedStatement ps = null;
//        ResultSet rs = null;
//        try {
//            connection = dataSource.getConnection();
//            String hql = getSql(taskInfo, paramsList);
//			log.debug("查询元征设备:{}",hql);
//        	ps = connection.prepareStatement(hql);
//        	if(paramsList!=null) {
//        		for (int i=0;i<paramsList.size() ;i++) {
//        			ps.setObject(i+1, paramsList.get(i));
//        		}
//        	}
//            rs = ps.executeQuery();
//            while (rs.next()) {
//            	TDevice device = new TDevice();
//            	device.setDeviceUid(rs.getString("id"));
//            	device.setVender_device_uid(rs.getString("device_uid"));
////            	device.setSn(rs.getString("sn"));
//            	device.setStatus(rs.getInt("status"));
//            	//TODO  add other column
//            	deviceList.add(device);
//            }
//			log.debug("获取到设备数:{}",deviceList.size());
//        } finally {
//            DBUtil.close(rs, ps, connection);
//        }
//        return deviceList;
//    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

//    private String getSql(TaskInfo taskInfo,List paramsList) {
//		StringBuffer sb = new StringBuffer("select t_device.id as id,device_uid,sn,status from t_device,t_yz_device_uid where t_device.id = t_yz_device_uid.id and status!=-1 ");
//		if (StringUtil.isNotNull(taskInfo.getExtrasArgs())) {
//		}
//    	return sb.toString();
//    }


    /**
     * 关联元征扩展表 查询当前设备列表
     *
     * @param taskInfo
     * @return
     * @throws Exception
     */
    public List<TDevice> getCnlaunchDeviceList(TaskInfo taskInfo) throws Exception {
        List<TDevice> deviceList = new ArrayList<TDevice>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = dataSource.getConnection();
            String hql = getCnlaunchSql(taskInfo);
            log.debug("查询元征设备:{}", hql);
            ps = connection.prepareStatement(hql);
            rs = ps.executeQuery();
            while (rs.next()) {
                TDevice device = new TDevice();
                device.setDeviceUid(rs.getString("id"));
                device.setVender_device_uid(rs.getString("vender_device_uid"));
                device.setSn(rs.getString("sn"));
                device.setStatus(rs.getInt("status"));
                device.setLastTripEndTime(rs.getTimestamp("last_trip_end_time"));
                deviceList.add(device);
            }
            log.debug("获取到设备数:{}", deviceList.size());
        } finally {
            DBUtil.close(rs, ps, connection);
        }
        return deviceList;
    }

    private String getCnlaunchSql(TaskInfo taskInfo) {
        StringBuffer sb = new StringBuffer("select d.id ,y.vender_device_uid,y.sn,d.status,y.last_trip_end_time "
                + "from t_device_extends_yz y left join t_device d "
                + "on y.device_uid=d.id ");
        if (StringUtil.isNotNull(taskInfo.getExtrasArgs())) {
            JSONObject args = JSONObject.parseObject(taskInfo.getExtrasArgs());
            Long begin = args.getLongValue("begin");
            Long end = args.getLongValue("end");
            if (begin != null || end != null) {
                if (begin == null) {
                    begin = 0L;//第一条记录
                }
                if (end == null) {
                    end = -1L;//最后一条记录
                }
                sb.append(" limit ").append(begin).append(",").append(end);
            }
        }
        return sb.toString();
    }

    public Map<Integer, Integer> getTripWarningNum(String deviceUID, Date sTime, Date eTime)
            throws Exception {
        Map<Integer, Integer> res = new HashMap<Integer, Integer>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            String sql = "select warning_type, count(warning_type) as num from t_device_warning "
                    + "where device_id=? and warning_time>=? and warning_time<=? "
                    + "and warning_type in(2,21,22,60,66,67,100,27,8,11)  group by warning_type ";
            ps = con.prepareStatement(sql);
            ps.setString(1, deviceUID);
            ps.setTimestamp(2, new Timestamp(sTime.getTime()));
            ps.setTimestamp(3, new Timestamp(eTime.getTime()));
            rs = ps.executeQuery();
            while (rs.next()) {
                res.put(rs.getInt("warning_type"), rs.getInt("num"));
            }
        } catch (Exception e) {
            throw e;
        } finally {

            DBUtil.close(rs, ps, con);
        }
        return res;
    }


    /**
     * 更新元征扩展表最后行程时间
     *
     * @param
     * @return
     * @throws Exception
     */
    public boolean updateLastTripEndTime(String sn, Date lastTripEndTime) throws Exception {
        Connection connection = null;
        PreparedStatement ps = null;
        String updateSQL = "update t_device_extends_yz set last_trip_end_time=? where sn = ?";
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            ps = connection.prepareStatement(updateSQL);
            ps.setTimestamp(1, new Timestamp(lastTripEndTime.getTime()));
            ps.setString(2, sn);
            ps.execute();
            connection.commit();
            return true;
        } finally {
            DBUtil.close(null, ps, connection);
        }
    }

}
