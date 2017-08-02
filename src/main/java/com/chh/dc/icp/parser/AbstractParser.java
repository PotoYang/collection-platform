package com.chh.dc.icp.parser;

import com.chh.dc.icp.accessor.DataPackage;
import com.chh.dc.icp.db.pojo.TaskInfo;
import com.chh.dc.icp.warehouse.ParsedRecord;
import com.chh.dc.icp.warehouse.exporter.Exporter;

/**
 * 解析器
 *
 * @author Niow
 * @version 1.0
 * @ClassName: AbstractParser
 * @date: 2016-6-27
 * @since 1.0
 */
public abstract class AbstractParser {

    protected Validator validator;

    protected TaskInfo taskInfo;

    protected String logHead;

    protected DataPackage data;

    protected Exporter exporter;

    /**
     * 接收到告警条数
     */
    protected int recievedNum;

    /**
     * 输出告警条数
     */
    protected int exporedNum;

    /**
     * 清除告警条数
     */
    protected int cleanedNum;

    /**
     * 坏数据条数
     */
    protected int badDataNum;


    public abstract void parse(TaskInfo taskInfo, DataPackage data) throws Exception;

    public abstract ParsedRecord readRecord() throws Exception;

    public abstract void beforeParse(TaskInfo taskInfo, DataPackage data) throws Exception;

    public abstract void afterParse() throws Exception;

    /**
     * @return the validator
     */
    public Validator getValidator() {
        return validator;
    }

    /**
     * @param validator the validator to set
     */
    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    /**
     * @return the logHead
     */
    public String getLogHead() {
        this.logHead = "[id:" + taskInfo.getId() + "][name:" + taskInfo.getName() + "]";
        return logHead;
    }

    /**
     * @param logHead the logHead to set
     */
    public void setLogHead(String logHead) {
        this.logHead = logHead;
    }

    /**
     * @return the recievedNum
     */
    public int getRecievedNum() {
        return recievedNum;
    }

    /**
     * @param recievedNum the recievedNum to set
     */
    public void setRecievedNum(int recievedNum) {
        this.recievedNum = recievedNum;
    }

    /**
     * @return the exporedNum
     */
    public int getExporedNum() {
        return exporedNum;
    }

    /**
     * @param exporedNum the exporedNum to set
     */
    public void setExporedNum(int exporedNum) {
        this.exporedNum = exporedNum;
    }

    /**
     * @return the cleanedNum
     */
    public int getCleanedNum() {
        return cleanedNum;
    }

    /**
     * @param cleanedNum the cleanedNum to set
     */
    public void setCleanedNum(int cleanedNum) {
        this.cleanedNum = cleanedNum;
    }

    /**
     * @return the badDataNum
     */
    public int getBadDataNum() {
        return badDataNum;
    }

    /**
     * @param badDataNum the badDataNum to set
     */
    public void setBadDataNum(int badDataNum) {
        this.badDataNum = badDataNum;
    }
}

