package com.chh.dc.icp.warehouse.exporter;

import com.chh.dc.icp.db.pojo.TaskInfo;
import com.chh.dc.icp.util.StringUtil;
import com.chh.dc.icp.warehouse.exporter.template.ExportTemplate;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;


/**
 * Exporter抽象类 提供Exporter通用功能<br>
 * 1、线程控制<br>
 * 2、ExportReport定义和输出
 */
public abstract class AbstractExporter implements Exporter {


    protected String dataType;


    // 输出模版ID
    protected int exportId;

    // 输出器类型
    protected int exportType;

    // 当前处理条数
    protected long current = 0L;

    // 总共条数
    protected long total = 0L;

    // 成功条数
    protected long succ = 0L;

    // 失败条数
    protected long fail = 0L;

    // 失败码
    protected long errorCode;

    // 失败原因
    protected String cause;

    // 输出目的地
    protected String dest;

    // 输出开始时间
    protected Date startTime;

    // 输出结束时间
    protected Date endTime;

    // 输出断点信息
    protected long breakPoint = 0L;

    protected String encode;

    // 异常标志 主要用于记录处理输出器初始化异常
    protected boolean exporterInitErrorFlag = false;

    protected TaskInfo taskInfo;

    protected List<String> entryNames = new LinkedList<String>();


    // 终止处理标识(当向cacher加入dataBlock发生异常时，此标识将被启用)
    protected volatile boolean breakProcessFlag = false;

    // 终止原因
    protected volatile String breakProcessCause;

    /**
     * 日志表写入开关
     */
    protected boolean dbLoggerFlag = true;//AppContext.getBean("dbLoggerFlag", Boolean.class);

    /**
     * log_clt_insert日志表写入开关
     */
    protected String logCltInsertFlag = "";//AppContext.getBean("logCltInsertFlag", String.class);


    public AbstractExporter(ExportTemplate template, TaskInfo taskInfo) {
        super();
        this.taskInfo = taskInfo;
//        this.entryNames = exporterArgs.getEntryNames();
    }


    public int getExportId() {
        return exportId;
    }

    public void setExportId(int exportId) {
        this.exportId = exportId;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public int getExportType() {
        return exportType;
    }

    public void setExportType(int exportType) {
        this.exportType = exportType;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getSucc() {
        return succ;
    }

    public void setSucc(long succ) {
        this.succ = succ;
    }

    public long getFail() {
        return fail;
    }

    public void setFail(long fail) {
        this.fail = fail;
    }

    public long getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(long errorCode) {
        this.errorCode = errorCode;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }


    /**
     * 判断是否输出日志记录
     *
     * @return
     */
    protected boolean isLogCltInsertFlag() {
        if (StringUtil.isNotNull(logCltInsertFlag)) {
            if ("1".equalsIgnoreCase(logCltInsertFlag.trim()) || "on".equalsIgnoreCase(logCltInsertFlag.trim())
                    || "true".equalsIgnoreCase(logCltInsertFlag.trim()))
                return true;
        }
        return false;
    }

    /**
     * 记录日志记录到 log_clt_insert
     */
    public void logCltInsert() {
//        DBLogger.getInstance().insertMinute(taskInfo.getId(), taskInfo.getExtraInfo().getOmcId(), dest, exporterArgs.getDataTime(), succ,
//                exporterArgs.getEntryNames().get(0));
    }

    /**
     * 设置输出断点
     */
    protected void setBreakPoint() {
        //TODO 当前版本先不考虑断电问题
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }


    protected ExportReport createExportReport() {
        ExportReport exportReport = new ExportReport();
        exportReport.setStartTime(this.startTime);
        exportReport.setEndTime(this.endTime);
        exportReport.setDest(this.dest);
        exportReport.setExportType(this.exportType);
        exportReport.setSucc(this.succ);
        exportReport.setExportId(this.exportId);
        exportReport.setBreakPoint(this.breakPoint);
        exportReport.setFail(this.fail);
        exportReport.setTotal(this.total);
        exportReport.setErrorCode(errorCode);
        exportReport.setCause(this.cause);
        return exportReport;
    }

    public String getEncode() {
        return encode;
    }

    public void setEncode(String encode) {
        this.encode = encode;
    }

    @Override
    public void breakProcess(String breakCause) {
        this.breakProcessFlag = true;
        this.breakProcessCause = breakCause;
    }

    /**
     * @return the logHead
     */
    public String getLogHead() {
        return "[id:" + exportId + "][dataType:" + dataType + "]";
    }
}
