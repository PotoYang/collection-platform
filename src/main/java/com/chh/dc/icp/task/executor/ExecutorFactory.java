package com.chh.dc.icp.task.executor;


import com.chh.dc.icp.db.pojo.TaskInfo;


/**
 * @version 1.0
 * @since 1.0
 * @author Niow
 * @date: 2016-6-27
 */
public class ExecutorFactory{

	/**
	 * 获取创建执行器
	 * 
	 * @param taskInfo
	 * @return
	 */
	public static AbstractExecutor getExecutor(TaskInfo taskInfo){
		switch(taskInfo.getTaskType()){
			case TaskInfo.TASK_COMMON:
				return new CommonExecutor(taskInfo);
			case TaskInfo.TASK_PERIOD:
				return new PeriodExecutor(taskInfo);
			case TaskInfo.TASK_SERVICE:
				return new ServiceExecutor(taskInfo);
			case TaskInfo.TASK_GOLO_PERIOD:
				return new GoloPeriodExecutor(taskInfo);
			default:
				return new CommonExecutor(taskInfo);
		}
	}
}
