package com.chh.dc.icp.task.loader;

import com.chh.dc.icp.db.pojo.TaskInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public abstract class TaskLoader extends Thread{

	public abstract void updateTask(TaskInfo taskInfo);

	/**
	 * 任务队列<br>
	 */
	protected TaskQueue taskQueue;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run(){
		loadTask();
	}

	/**
	 * 加载任务,把任务加载到任务队列中。
	 */
	public abstract void loadTask();

	public TaskQueue getTaskQueue(){
		return taskQueue;
	}

	public void setTaskQueue(TaskQueue taskQueue){
		this.taskQueue = taskQueue;
	}

	public class TaskQueue{

		/** 任务队列默认大小,为200 */
		private static final int DEFAULT_TASK_QUEUE_SIZE = 200;

		private BlockingQueue<TaskInfo> currTaskQueue; // 当前任务队列

		private int taskQueueSize = DEFAULT_TASK_QUEUE_SIZE; // 任务队列大小,初始化为200

		/** 任务map **/
		public Map<Long,TaskInfo> taskMap;

		/**
		 * 默认构造方法
		 * <p>
		 * 以默认任务队列大小{@link #DEFAULT_TASK_QUEUE_SIZE}构造队列
		 * </p>
		 */
		public TaskQueue(){
			super();
			currTaskQueue = new ArrayBlockingQueue<TaskInfo>(taskQueueSize);
		}

		/**
		 * 指定队列大小方式构造队列
		 * 
		 * @param size 队列大小，如果为非正整数，则使用默认大小{@link #DEFAULT_TASK_QUEUE_SIZE}
		 */
		public TaskQueue(int size){
			super();
			if(size > 0)
				this.taskQueueSize = size;
			currTaskQueue = new ArrayBlockingQueue<TaskInfo>(taskQueueSize);
		}

		/**
		 * 添加任务到任务队列
		 * <p>
		 * 如果队列满了，则会被阻塞直到队列有空间时再添加.
		 * </p>
		 * 
		 * @param task 任务{@link Task}
		 * @throws InterruptedException if interrupted while waiting
		 */
		public void put(TaskInfo task) throws InterruptedException{
			// 添加任务到队列
			currTaskQueue.put(task);

			// 添加任务到map
			if(taskMap == null){
				taskMap = new HashMap<Long,TaskInfo>();
			}
			taskMap.put(task.getId(), task);
		}

		/**
		 * 从任务队列中取出一条任务
		 * <p>
		 * 如果任务队列中没有任务，则阻塞等待队列中有任务为止.
		 * </p>
		 * 
		 * @return 队列头的任务{@link Task}
		 * @throws InterruptedException if interrupted while waiting
		 */
		public TaskInfo take() throws InterruptedException{
			return currTaskQueue.take();
		}

		/**
		 * 获取当前所有的任务
		 * 
		 * @return 当前{@link Task}列表
		 */
		public synchronized List<TaskInfo> getCurrentTaskList(){
			List<TaskInfo> tasks = new ArrayList<TaskInfo>();
			Iterator<TaskInfo> itr = currTaskQueue.iterator();
			while(itr.hasNext()){
				tasks.add(itr.next());
			}
			return tasks;
		}

		/**
		 * 清空任务队列
		 */
		public synchronized void clear(){
			currTaskQueue.clear();
		}

		/**
		 * 获取任务队列的容量
		 */
		public int getCapacity(){
			return this.taskQueueSize;
		}
	}
}
