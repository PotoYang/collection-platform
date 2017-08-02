package com.chh.dc.icp.task.executor;

import com.chh.dc.icp.db.pojo.TaskInfo;
import com.chh.dc.icp.task.TaskFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 普通执行器<br>
 * 针对不是服务性或周期性的任务，任务只执行一次
 * 
 * 接入开始时间，接入结束时间，采集解析开始时间，采集结束时间，入库时间，入库结束时间 ,汇总开始时间，汇总结束时间，任务ID,采集总条数
 * 
 * @ClassName: CommonExecutor
 * @version 1.0
 * @since 1.0
 * @author Niow
 * @date: 2016-6-27
 */
public class CommonExecutor extends AbstractExecutor{

	private static Logger LOG = LoggerFactory.getLogger(CommonExecutor.class);

	public CommonExecutor(TaskInfo taskInfo){
		super(taskInfo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.uway.alarmbox.task.executor.AbstractExecutor#afterExec(cn.uway.alarmbox
	 * .task.TaskFuture)
	 */
	@Override
	protected void afterExec(TaskFuture future) throws Exception{
		parser.getRecievedNum();
		super.afterExec(future);
	}

}
