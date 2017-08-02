package com.chh.dc.icp.util.monitor;

import java.util.Date;
import java.util.List;

/**
 * 是否能监控运行状态接口<br>
 * 任务线程，输出线程，采集线程等实现监控接口后，可以提供监控器实时获取当前的运行状态
 * 
 * @ClassName: MonitorAble
 * @author Niow
 * @date: 2014-4-4
 */
public interface MonitorAble{

	/**
	 * 获取标题
	 * 
	 * @return
	 */
	public String getTitle();

	/**
	 * 获取详细描述
	 * 
	 * @return
	 */
	public String getDescription();

	/**
	 * 获取当前状态
	 * 
	 * @return
	 */
	public String getStatus();

	/**
	 * 获取当前状态的详细信息
	 * 
	 * @return
	 */
	public String getStatusInfo();

	/**
	 * 获取运行开始时间
	 * 
	 * @return
	 */
	public Date getStartTime();

	/**
	 * 获取运行结束时间
	 * 
	 * @return
	 */
	public Date getEndTime();

	/**
	 * 获取运行记录的所有步骤记录信息
	 * 
	 * @return
	 */
	public List<String> getStepHistory();

	/**
	 * 获取运行记录最新的lastestNum条步骤记录信息<br>
	 * 当步骤信息记录条数小于lastestNum，返回当前所有记录
	 * 
	 * @param lastestNum 所要获取的最新的步骤信息条数
	 * @return
	 */
	public List<String> getStepHistory(int lastestNum);

	/**
	 * 获取运行步骤记录堆栈中最新的步骤信息
	 * 
	 * @return
	 */
	public String getTopStep();

	/**
	 * 获取运行前的准备信息
	 * 
	 * @return
	 */
	public String getBeforeRunningInfo();

	/**
	 * 获取运行完成后的总结信息
	 * 
	 * @return
	 */
	public String getAfterRunningInfo();

	/**
	 * 获取当运行出现异常时的信息
	 * 
	 * @return
	 */
	public String getExceptionInfo();

}
