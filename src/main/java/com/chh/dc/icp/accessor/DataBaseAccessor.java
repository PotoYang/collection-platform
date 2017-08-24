package com.chh.dc.icp.accessor;

import com.chh.dc.icp.util.DBUtil;
import com.chh.dc.icp.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.List;

public class DataBaseAccessor extends AbstractAccessor {

    private static final Logger log = LoggerFactory.getLogger(DataBaseAccessor.class);

    private Connection conn;

    private String sql;

    private List<String> sqlList;

    private DataPackage data;

    public DataBaseAccessor() {

    }

    private void prepare() {

    }

    @Override
    public boolean access() {
        this.sql = taskInfo.getCollectPath();
        if (sql == null) {
            log.error(getLogHead() + "没有可以执行的采集SQL");
            return false;
        }
        if (sql.contains("{lastExecTime}")) {
            Timestamp dataTime = taskInfo.getDataTime();
            int collectPeriodMin = taskInfo.getCollectPeriodMin();
            Timestamp lastExecTime = new Timestamp(dataTime.getTime() - collectPeriodMin * 6000);
            sql.replace("{lastExecTime}", DateUtil.getDateTimeString(lastExecTime));
        }
        if (log.isDebugEnabled()) {
            log.debug(getLogHead() + "执行SQL:{}", sql);
        }
        String dbDriver = taskInfo.getDbDriver();
        String dbUrl = taskInfo.getDbURL();
        String userName = taskInfo.getUsername();
        String password = taskInfo.getPassword();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection(dbDriver, dbUrl, userName, password);
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            data = new DataPackage(rs);
        } catch (Exception e) {
            log.error(getLogHead() + "访问数据库接入出错", e);
            return false;
        }
        return true;
    }

    @Override
    public DataPackage getData() {
        return data;
    }

    @Override
    public boolean stop() {
        return false;
    }

}
