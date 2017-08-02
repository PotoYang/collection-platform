package com.chh.dc.icp.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.chh.dc.icp.db.pojo.PidThreshold;
import com.chh.dc.icp.util.DBUtil;
import com.chh.dc.icp.db.pojo.TDtc;

public class DtcDAO {

    private DataSource dataSource;
    
    public List<TDtc> getDtcList() throws Exception {
        List<TDtc> dtcList = new ArrayList<TDtc>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = dataSource.getConnection();
            String sql = "SELECT id,`value`,type,description FROM t_dictionary_dtc";
        	ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
            	TDtc d = new TDtc();
            	d.setId(rs.getInt("id"));
            	d.setValue(rs.getString("value"));
            	d.setType(rs.getInt("type"));
            	d.setDescription(rs.getString("description"));
            	dtcList.add(d);
            }
        } finally {
            DBUtil.close(rs, ps, connection);
        }
        return dtcList;
    }
    public List<Map<String, Object>> getWarningList() throws Exception {
        List<Map<String, Object>> warningList = new ArrayList<Map<String, Object>>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = dataSource.getConnection();
            String sql = "SELECT id,`value`,description FROM t_dictionary_warning";
        	ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();
            Map<String, Object> map = null;
            while (rs.next()) {
            	map = new HashMap<>();
            	map.put("id", rs.getInt("id"));
            	map.put("value", rs.getInt("value"));
            	map.put("description", rs.getString("description"));
            	warningList.add(map);
            }
        } finally {
            DBUtil.close(rs, ps, connection);
        }
        return warningList;
    }
    /**
     * PID阀值列表
     * @return
     * @throws Exception
     */
    public List<PidThreshold> getPidThresholdList() throws Exception {
        List<PidThreshold> list = new ArrayList<>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = dataSource.getConnection();
            String sql = "SELECT id,pid,`name`,min_value,max_value,type FROM t_pid_threshold";
        	ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();
            PidThreshold p = null;
            while (rs.next()) {
            	p = new PidThreshold();
            	p.setId(rs.getInt("id"));
            	p.setPid(rs.getString("pid"));
            	p.setName(rs.getString("name"));
            	p.setMinValue(rs.getDouble("min_value"));
            	p.setMaxValue(rs.getDouble("max_value"));
            	p.setType(rs.getInt("type"));
            	list.add(p);
            }
        } finally {
            DBUtil.close(rs, ps, connection);
        }
        return list;
    }
    
	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}
