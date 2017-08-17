package com.chh.dc.icp.task.executor;

import com.chh.dc.icp.db.pojo.TaskInfo;

import java.util.Date;


/**
 * 执行报告<br>
 * <p>
 * 接入开始时间，接入结束时间，采集解析开始时间，采集结束时间，入库时间，入库结束时间 ,汇总开始时间，汇总结束时间，任务ID,采集总条数
 *
 * @author Niow
 * @version 1.0
 * @ClassName: Report
 * @date: 2016-6-27
 * @since 1.0
 */
public class ExecutionReport {

    private String title;

    private String description;

    private Date accessStartTime;

    private Date accessEndTime;

    private Date parseStartTime;

    private Date parseEndTime;

    private TaskInfo taskInfo;

    /**
     * 执行结果：成功，失败，执行不完全
     */
    private String result;

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the accessStartTime
     */
    public Date getAccessStartTime() {
        return accessStartTime;
    }

    /**
     * @param accessStartTime the accessStartTime to set
     */
    public void setAccessStartTime(Date accessStartTime) {
        this.accessStartTime = accessStartTime;
    }

    /**
     * @return the accessEndTime
     */
    public Date getAccessEndTime() {
        return accessEndTime;
    }

    /**
     * @param accessEndTime the accessEndTime to set
     */
    public void setAccessEndTime(Date accessEndTime) {
        this.accessEndTime = accessEndTime;
    }

    /**
     * @return the parseStartTime
     */
    public Date getParseStartTime() {
        return parseStartTime;
    }

    /**
     * @param parseStartTime the parseStartTime to set
     */
    public void setParseStartTime(Date parseStartTime) {
        this.parseStartTime = parseStartTime;
    }

    /**
     * @return the parseEndTime
     */
    public Date getParseEndTime() {
        return parseEndTime;
    }

    /**
     * @param parseEndTime the parseEndTime to set
     */
    public void setParseEndTime(Date parseEndTime) {
        this.parseEndTime = parseEndTime;
    }

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
     * @return the result
     */
    public String getResult() {
        return result;
    }

    /**
     * @param result the result to set
     */
    public void setResult(String result) {
        this.result = result;
    }

}
