package com.chh.dc.icp.db.dao;

import com.chh.dc.icp.db.pojo.TaskInfo;
import com.chh.dc.icp.util.DBUtil;
import com.chh.dc.icp.util.StringUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Niow
 * @version 1.0
 * @ClassName: TaskInfoDAO
 * @date: 2016-6-27
 * @since 1.0
 */
public class TaskInfoDAO {

    private DataSource dataSource;

    private String taskLoadSQL = "select * from t_icp_task t where t.is_used = 1";

    private String updateSQL = "update t_icp_task set excution_time=? where id = ?";

    private int processId;

    /**
     * 加载任务列表
     *
     * @return
     * @throws Exception
     */
    public List<TaskInfo> getTaskList() throws Exception {
        String pcName = StringUtil.getHostName();
        //此处预留为标记采集服务，因为共用同一采集任务表，所以需要在表数据中进行区分配置
//		if(processId != 0) {
//			pcName += "@" + processId;
//		}
        List<TaskInfo> taskList = new ArrayList<TaskInfo>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = dataSource.getConnection();
            ps = connection.prepareStatement(taskLoadSQL);
            rs = ps.executeQuery();
            while (rs.next()) {
                TaskInfo taskInfo = new TaskInfo();
                taskInfo.setId(rs.getInt("ID"));
                taskInfo.setName(rs.getString("NAME"));
                taskInfo.setAccessorId(rs.getString("ACCESSOR_ID"));
                taskInfo.setParserId(rs.getString("PARSER_ID"));
                taskInfo.setExporterTemplate(rs.getString("EXPORTER_TEMPLATE"));
                taskInfo.setHostAddr(rs.getString("HOST_ADDR"));
                taskInfo.setPort(rs.getInt("HOST_PORT"));
                taskInfo.setUsername(rs.getString("USERNAME"));
                taskInfo.setPassword(rs.getString("PASSWORD"));
                taskInfo.setDbDriver(rs.getString("DB_DRIVER"));
                taskInfo.setDbURL(rs.getString("DB_URL"));
                taskInfo.setCollectTimeoutSec(rs.getInt("COLLECTION_TIMEOUT_SEC"));
                taskInfo.setCollectPeriodMin(rs.getInt("COLLECTION_PERIOD_SEC"));
                taskInfo.setCollectPath(rs.getString("COLLECTION_PATH"));
                taskInfo.setDataTime(rs.getTimestamp("EXCUTION_TIME"));
                taskInfo.setPathEncode(rs.getString("PATH_ENCODE"));
                taskInfo.setFileEncode(rs.getString("FILE_ENCODE"));
                taskInfo.setHostSign(rs.getString("HOST_SIGN"));
                taskInfo.setTaskType(rs.getInt("TASK_TYPE"));
                taskInfo.setExtrasArgs(rs.getString("extras_args"));
                taskList.add(taskInfo);
            }
        } finally {
            DBUtil.close(rs, ps, connection);
        }
        return taskList;
    }

    /**
     * 更新任务执行时间
     *
     * @param taskInfo
     * @return
     * @throws Exception
     */
    public boolean updateTask(TaskInfo taskInfo) throws Exception {
        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            ps = connection.prepareStatement(updateSQL);
            ps.setTimestamp(1, taskInfo.getDataTime());
            ps.setLong(2, taskInfo.getId());
            ps.execute();
            connection.commit();
            return true;
        } finally {
            DBUtil.close(null, ps, connection);
        }
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * @return the taskLoadSQL
     */
    public String getTaskLoadSQL() {
        return taskLoadSQL;
    }

    /**
     * @param taskLoadSQL the taskLoadSQL to set
     */
    public void setTaskLoadSQL(String taskLoadSQL) {
        this.taskLoadSQL = taskLoadSQL;
    }

    /**
     * @return the processId
     */
    public int getProcessId() {
        return processId;
    }

    /**
     * @param processId the processId to set
     */
    public void setProcessId(int processId) {
        this.processId = processId;
    }

}
