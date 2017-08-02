package com.chh.dc.icp.warehouse.repository;

import com.chh.dc.icp.db.pojo.TaskInfo;

/**
 * 传入创建仓库的参数，做对象进行接口封装，方便后面加其他参数
 *
 * Created by Niow on 2016/7/6.
 */
public class RepositoryArgs {

    private TaskInfo taskInfo;

    public TaskInfo getTaskInfo() {
        return taskInfo;
    }

    public void setTaskInfo(TaskInfo taskInfo) {
        this.taskInfo = taskInfo;
    }
}
