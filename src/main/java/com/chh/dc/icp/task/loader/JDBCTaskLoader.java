package com.chh.dc.icp.task.loader;

import com.chh.dc.icp.db.dao.TaskInfoDAO;
import com.chh.dc.icp.db.pojo.TaskInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.List;


/**
 * @author Niow
 * @version 1.0
 * @date: 2016-6-27
 * @since 1.0
 */
public class JDBCTaskLoader extends TaskLoader {

    private static final Logger LOG = LoggerFactory.getLogger(JDBCTaskLoader.class);

    private TaskInfoDAO taskInfoDAO;

    private boolean keepRunning = true;

    private int loadPeriodMin = 1;

    public JDBCTaskLoader() {
        setName("JDBCTaskLoader");
        this.taskQueue = new TaskQueue();
    }


    public void updateTask(TaskInfo taskInfo) {

    }

    /**
     * 每隔一段时间从数据库加载数据(任务)
     */
    @Override
    public void loadTask() {
        LOG.info("数据库任务加载器启动，任务加载周期为:" + loadPeriodMin + "分钟");
        while (keepRunning) {
            try {
                LOG.info("开始从数据库加载任务列表");
                List<TaskInfo> tempList = taskInfoDAO.getTaskList();
                for (int i = 0; tempList != null && i < tempList.size(); i++) {
                    TaskInfo taskInfo = tempList.get(i);
                    /// 采集周期
                    int collectPeriod = taskInfo.getCollectPeriodMin();
                    if (collectPeriod >= 0) {
                        ///开始执行的时间
                        Timestamp sucDataTime = taskInfo.getDataTime();
                        if (sucDataTime == null || sucDataTime.getTime() > System.currentTimeMillis()) {
                            continue;
                        }
                    }
                    taskQueue.put(tempList.get(i));
                }
                LOG.info("共加载到" + tempList.size() + "个满足执行条件的任务");
            } catch (Exception e) {
                LOG.error("加载任务报错！！！，请检查config.xml配置文件.", e);
//                return;
            }
            try {
                Thread.sleep(loadPeriodMin * 1000 * 60);
            } catch (InterruptedException e) {
            }
        }

    }

    /**
     * @return the taskInfoDAO
     */
    public TaskInfoDAO getTaskInfoDAO() {
        return taskInfoDAO;
    }

    /**
     * @param taskInfoDAO the taskInfoDAO to set
     */
    public void setTaskInfoDAO(TaskInfoDAO taskInfoDAO) {
        this.taskInfoDAO = taskInfoDAO;
    }

    /**
     * @return the loadPeriodMin
     */
    public int getLoadPeriodMin() {
        return loadPeriodMin;
    }

    /**
     * @param loadPeriodMin the loadPeriodMin to set
     */
    public void setLoadPeriodMin(int loadPeriodMin) {
        this.loadPeriodMin = loadPeriodMin;
    }

}
