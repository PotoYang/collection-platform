package com.chh.dc.icp.task;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.chh.dc.icp.task.executor.AbstractExecutor;
import com.chh.dc.icp.task.executor.ExecutorFactory;
import com.chh.dc.icp.task.loader.TaskLoader;
import com.chh.dc.icp.warehouse.exporter.ExporterManager;
import com.chh.dc.icp.warehouse.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chh.dc.icp.db.pojo.TaskInfo;
import com.chh.dc.icp.util.OBDAlarmCodeConverter;
import com.chh.dc.icp.warehouse.GenericWareHouse;
import com.chh.dc.icp.warehouse.WareHouse;
import com.chh.dc.icp.warehouse.repository.RepositoryArgs;

public class TaskManager extends Thread{

	private static final Logger log = LoggerFactory.getLogger(TaskManager.class);

	/**
	 * 任务线程池
	 */
    private ExecutorService threadPool;

	/**
	 * 触发器开关 当triggerFlag=false时 不会有新的任务提交
	 */
	private volatile boolean triggerFlag = true;

	/**
	 * 任务线程池包装类
	 */
	private CompletionService<TaskFuture> service;

	/**
	 * 正在运行的任务的Map<br>
	 * 同时使用workingTasks来进行并发控制,而不是使用线程池的线程并发控制,
     * 目的在于将运行队列提供给控制台用于显示<br>
	 * 
	 */
	private Map<Long,TaskInfo> workingTasks = new HashMap<Long,TaskInfo>();

	/**
	 * 任务执行线程监听器<br>
	 */
	private Listener listener;

	/**
	 * 触发器对象锁.用于控制workingTasks的并发读写<br>
	 */
	private Object lock = new Object();

	/** 任务加载器，作为一个单独的线程，在任务管理器初始化的时候开始运行 */
	private TaskLoader taskLoader;

	/** 输出器管理 */
	private ExporterManager exportManager;

	public TaskManager(TaskLoader taskLoader){
		super("任务触发器");
		this.taskLoader = taskLoader;
		threadPool = Executors.newFixedThreadPool(10);
		service = new ExecutorCompletionService<TaskFuture>(threadPool);
	}

	public TaskManager(){
		super("任务触发器");
		threadPool = Executors.newFixedThreadPool(10);
		service = new ExecutorCompletionService<TaskFuture>(threadPool);
	}

	public void run(){
		initialize();
		while(triggerFlag){
			try {
				TaskLoader.TaskQueue taskQueue = taskLoader.getTaskQueue();
				TaskInfo taskInfo;
				try{
					taskInfo = taskQueue.take();
				}catch(InterruptedException e){
					log.warn("从任务队列获取任务出错", e);
					continue;
				}
				if(running(taskInfo)){
					log.debug("任务已经在运行中. [task-name={}]", taskInfo.getName());
					continue;
				}
				// 根据类型获取对应执行器
				AbstractExecutor executor = ExecutorFactory.getExecutor(taskInfo);

				//从warehouse申请仓库，如果仓库已经存在，则返回已经存在的实例
				WareHouse wareHouse = GenericWareHouse.getInstance();

				RepositoryArgs repositoryArgs = new RepositoryArgs();
				repositoryArgs.setTaskInfo(taskInfo);

				Repository repository = wareHouse.apply(repositoryArgs);
				executor.setRepository(repository);

				// 设置任务本次启动时间
				service.submit(executor);
				workingTasks.put(taskInfo.getId(), taskInfo);
				log.debug("taskName={}已提交至运行队列中,当前任务运行队列大小={}", new Object[]{taskInfo.getName(),workingTasks.size()});
			} catch (Exception e) {
				log.error("任务触发器出错了", e);
			}
		}
		log.warn("由于触发器已停止，任务不再启动。");
	}

	/**
	 * 判断任务是否在运行 周期性任务 判断task_id和数据时间 非周期性任务 直接通过task_id进行判断
	 * 
	 * @param task
	 * @return 任务是否已经在运行 true表示已经在运行 false 表示未运行
	 */
	protected boolean running(TaskInfo task){
		return workingTasks.containsKey(task.getId());
	}

	private void initialize(){
		taskLoader.start();

		threadPool = Executors.newFixedThreadPool(10);
		service = new ExecutorCompletionService<TaskFuture>(threadPool);

		// 启动任务执行完毕后处理线程
		listener = new Listener();
		// 设置为守护线程
		listener.setDaemon(true);
		listener.start();
		//初始化所需数据
		OBDAlarmCodeConverter.loadData();
	}

	/**
	 * 停止任务触发
	 */
	public synchronized void stopTrigger(){
		triggerFlag = false;
		threadPool.shutdownNow();
		this.interrupt();
		workingTasks.clear();
		log.warn("将被外部停止");
	}

	/**
	 * 将任务从运行队列中移除 同时唤醒trigger线程
	 * 
	 * @param task
	 */
	public void removeTask(TaskInfo task){
		synchronized(lock){
			if(!workingTasks.containsKey(task.getId())){
				log.error("任务已从运行队列移除失败,taskName={}在运行任务队列中不存在", task.getName());
				return;
			}
			workingTasks.remove(task.getId());
			lock.notifyAll();
			log.debug("任务已从运行队列移除：{}", task.getName());
		}
	}

	/**
	 * @return the taskLoader
	 */
	public TaskLoader getTaskLoader(){
		return taskLoader;
	}

	/**
	 * @param taskLoader the taskLoader to set
	 */
	public void setTaskLoader(TaskLoader taskLoader){
		this.taskLoader = taskLoader;
	}

	/**
	 * @return the exportManager
	 */
	public ExporterManager getExportManager(){
		return exportManager;
	}

	/**
	 * @param exportManager the exportManager to set
	 */
	public void setExportManager(ExporterManager exportManager){
		this.exportManager = exportManager;
	}

	/**
	 * 任务执行结果处理监听器<br>
	 * 监听任务的执行结果
	 */
	class Listener extends Thread{

		Listener(){
			super("任务结果处理器");
		}

		@Override
		public void run(){
			TaskFuture taskFuture = null;
			log.debug("任务运行结果提取线程启动。");
			while(true){
				try{
					// 取出任务运行结果 如果没有返回 则线程会挂起
					Future<TaskFuture> future = service.take();
					if(future == null){
						log.error("提取线程返回结果异常.Future==null");
						continue;
					}
					taskFuture = future.get();
					if(taskFuture == null){
						log.error("提取线程返回结果异常.TaskFuture==null");
						continue;
					}
					TaskInfo task = taskFuture.getTask();

					log.debug("[taskName={},{}]", new Object[]{task.getName(),"cause=" + taskFuture.getCause()});
				}catch(InterruptedException e){
					log.error("提取任务线程运行中断", e);
					continue;
				}catch(ExecutionException e){
					log.error("提取任务线程运行结果失败", e);
					continue;
				}finally{
					// 无论返回成功与否都必须从 当前运行任务表 中清除掉
					if(taskFuture != null && taskFuture.getTask() != null)
						removeTask(taskFuture.getTask());
				}
			}
		}
	}

}
