package com.chh.dc.icp.task.executor;

import com.chh.dc.icp.Runner;
import com.chh.dc.icp.warehouse.repository.Repository;
import com.chh.dc.icp.accessor.AbstractAccessor;
import com.chh.dc.icp.accessor.DataPackage;
import com.chh.dc.icp.db.pojo.TaskInfo;
import com.chh.dc.icp.parser.AbstractParser;
import com.chh.dc.icp.task.TaskFuture;
import com.chh.dc.icp.util.TimeUtil;
import com.chh.dc.icp.warehouse.ParsedRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;


/**
 * <p>
 * 抽象任务执行器
 * </P>
 * <p>
 * 任务执行器是一个任务执行的容器，容器中为任务准备所需要的所有资源。
 * </P>
 * <p>
 * 执行器在准备好任务执行期间所需要的资源后，会按照任务的类型来决定执行方式，<br>
 * 在按照任务类型执行运行期间，实现MonitorAble接口，提供外层管理器对当前执行<br>
 * 任务运行状态的查询和管理。
 * </p>
 *
 * @author Niow
 * @version 1.0
 * @ClassName: AbstractExecutor
 * @date: 2016-6-27
 * @since 1.0
 */
public abstract class AbstractExecutor implements Callable<TaskFuture>{

    private static Logger LOG = LoggerFactory.getLogger(AbstractExecutor.class);
    /**
     * 任务信息
     */
    protected TaskInfo taskInfo;
    /**
     * 数据访问器
     */
    protected AbstractAccessor accessor;
    /**
     * 数据解析组装器
     */
    protected AbstractParser parser;

    /**
     * 数据输出仓库
     */
    protected Repository repository;

    /**
     * 执行记录保留条数，默认20
     */
    protected int stackSize = 20;
    /**
     * 执行记录保存栈
     */
    protected LinkedList<String> traceStack;
    /**
     * 日志头
     */
    protected String logHead;

    /**
     * 公共构造，必须有任务信息
     *
     * @param taskInfo
     */
    public AbstractExecutor(TaskInfo taskInfo) {
        this.taskInfo = taskInfo;
        this.traceStack = new LinkedList<String>();
        this.logHead = "[id:" + taskInfo.getId() + "][name:" + taskInfo.getName() + "]";
    }

    /**
     * 执行前需要做的操作，主要实现对访问器、解析器、输出器等构建。
     */
    protected void beforeExec(TaskFuture future) throws Exception {
        LOG.debug("开始准备执行资源");
        Thread.currentThread().setName(logHead);
        accessor = Runner.getBean(taskInfo.getAccessorId(), AbstractAccessor.class);
        accessor.setTaskInfo(taskInfo);
        accessor.setLogHead(logHead);
        LOG.debug(logHead + "获取Accessor成功");
        parser = Runner.getBean(taskInfo.getParserId(), AbstractParser.class);
        parser.setLogHead(logHead);

        LOG.debug(logHead + "获取Parser成功");
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.concurrent.Callable#call()
     */
    @Override
    public TaskFuture call() throws Exception {
        TaskFuture future = new TaskFuture(TaskFuture.TASK_CODE_SUCCESS, taskInfo);
        try {
            beforeExec(future);
            exec(future);
            afterExec(future);
        } catch (Exception e) {
            LOG.error(logHead + "执行任务失败", e);
            future.setCode(TaskFuture.TASK_CODE_FAILED);
            future.setCause(e.getMessage());
        }
        return future;
    }

    /**
     * 执行务操作
     */
    protected void exec(TaskFuture future) throws Exception {
        LOG.info(logHead + "开始执行任务");
        boolean accessResult = accessor.access();
        if (!accessResult) {
            LOG.warn(logHead + "采集器访问失败");
            return;
        }
        DataPackage data = accessor.getData();
        LOG.info(logHead + "采集器访问成功，开始解析");
        parser.parse(taskInfo, data);
        ParsedRecord record = parser.readRecord();
        while (record != null) {
            try {
                repository.transport(record);
                record = parser.readRecord();
            } catch (Exception e) {
                LOG.error(logHead + "解析数据源出错:" + data.getDesc(), e);
            }
        }
        repository.commit();
        LOG.info(logHead + "任务采集解析完毕");
    }

    /**
     * 执行完成后需要做的操作，主要实现对占用资源的释放，任务执行报告完善等操作。
     */
    protected void afterExec(TaskFuture future) throws Exception {
        LOG.debug(logHead + "解析器采集到数据" + parser.getRecievedNum() + "条");
        accessor = null;
        parser = null;
        Thread.currentThread().setName("线程池空闲线程");
        LOG.debug("释放任务资源，回归线程池");
    }

    /**
     * 添加执行记录<br>
     * 在添加执行步骤时，自动在前面增加MM-dd hh:mm:ss:sss的时间戳
     *
     * @param execTrace 当前执行步骤
     */
    protected void addTrace(String execTrace) {
        String timeStamp = TimeUtil.getDateString_MMddhhmmssSSS(new Date());
        String trace = timeStamp + "|" + execTrace;
        if (traceStack.size() >= stackSize) {
            traceStack.removeFirst();
        }
        traceStack.addLast(trace);
    }

    /**
     * 停止执行器，主要包括停止访问器，停止解析器，停止输出器
     */
    public void stop(){

    };


    /**
     * @return the taskInfo
     */
    public TaskInfo getTaskInfo() {
        return taskInfo;
    }

    /**
     * @param taskInfo the taskInfo to set
     */
    public void setTaskInfo(TaskInfo taskInfo) {
        this.taskInfo = taskInfo;
    }

    /**
     * @return the accessor
     */
    public AbstractAccessor getAccessor() {
        return accessor;
    }

    /**
     * @param accessor the accessor to set
     */
    public void setAccessor(AbstractAccessor accessor) {
        this.accessor = accessor;
    }

    /**
     * @return the parser
     */
    public AbstractParser getParser() {
        return parser;
    }

    /**
     * @param parser the parser to set
     */
    public void setParser(AbstractParser parser) {
        this.parser = parser;
    }

    /**
     * @return the stackSize
     */
    public int getStackSize() {
        return stackSize;
    }

    /**
     * @param stackSize the stackSize to set
     */
    public void setStackSize(int stackSize) {
        this.stackSize = stackSize;
    }


    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }


}
