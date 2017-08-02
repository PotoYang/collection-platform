package com.chh.dc.icp.accessor;

import com.chh.dc.icp.db.pojo.TaskInfo;

/**
 * 抽象访问器定义<br>
 * 使用访问器调用步骤：<br>
 * 1、创建访问器对象<br>
 * 2、调用access方法，如果返回false，则结束访问<br>
 * 3、返回true，则应该循环调用getData方法获取数据源DataPackage<br>
 * 4、应为访问器一次可能返回多个数据源，所以需要循环调用getData方法，知道返回null
 *
 * @ClassName: AbstractAccessor
 * @since 1.0
 * @version 1.0
 * @author Niow
 * @date: 2016-6-27
 */
public abstract class AbstractAccessor{

    protected String logHead;

    protected TaskInfo taskInfo;

    public abstract boolean access();

    /**
     * 获取数据源
     *
     *
     * @return
     */
    public abstract DataPackage getData();

    public abstract boolean stop();

    /**
     * @return the taskInfo
     */
    public TaskInfo getTaskInfo(){
        return taskInfo;
    }

    /**
     * @param taskInfo the taskInfo to set
     */
    public void setTaskInfo(TaskInfo taskInfo){
        this.taskInfo = taskInfo;
    }

    /**
     * @return the logHead
     */
    public String getLogHead(){
        return logHead;
    }

    /**
     * @param logHead the logHead to set
     */
    public void setLogHead(String logHead){
        this.logHead = logHead;
    }

}
