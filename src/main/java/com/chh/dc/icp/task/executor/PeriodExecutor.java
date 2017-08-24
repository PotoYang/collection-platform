package com.chh.dc.icp.task.executor;

import java.sql.Timestamp;

import com.chh.dc.icp.Runner;
import com.chh.dc.icp.db.dao.TaskInfoDAO;
import com.chh.dc.icp.db.pojo.TaskInfo;
import com.chh.dc.icp.task.TaskFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 周期性任务执行器<br>
 * 在执行完毕采集任务后，完成对任务信息以及下次采集时间的更新
 *
 * @author Niow
 * @version 1.0
 * @date: 2016-6-27
 * @since 1.0
 */
public class PeriodExecutor extends AbstractExecutor {

    private static Logger LOG = LoggerFactory.getLogger(PeriodExecutor.class);

    private TaskInfoDAO taskDAO;

    public PeriodExecutor(TaskInfo taskInfo) {
        super(taskInfo);
        taskDAO = Runner.getBean("taskInfoDAO", TaskInfoDAO.class);
    }


    @Override
    protected void afterExec(TaskFuture future) throws Exception {
        boolean result = turnToNextTime(this.taskInfo);
        if (!result) {
            future.setCode(TaskFuture.TASK_CODE_INCOMPLETE);
            future.setCause("任务采集时间更新失败");
        }
        super.afterExec(future);
    }

    /**
     * 跳转任务采集时间到下一个时间点
     *
     * @param taskInfo
     * @return
     */
    private boolean turnToNextTime(TaskInfo taskInfo) {
        int collectPeriod = taskInfo.getCollectPeriodMin();
        Timestamp sucDataTime = taskInfo.getDataTime();
        long nextTime = sucDataTime.getTime() + collectPeriod * 60 * 1000;
        sucDataTime.setTime(nextTime);
        taskInfo.setDataTime(sucDataTime);
        try {
            boolean result = taskDAO.updateTask(taskInfo);
            return result;
        } catch (Exception e) {
            LOG.error(this.logHead + "更新任务采集时间出错", e);
        }
        return false;
    }

    /**
     * @return the taskDAO
     */
    public TaskInfoDAO getTaskDAO() {
        return taskDAO;
    }

    /**
     * @param taskDAO the taskDAO to set
     */
    public void setTaskDAO(TaskInfoDAO taskDAO) {
        this.taskDAO = taskDAO;
    }

}
