package com.chh.dc.icp.warehouse.exporter.template.redis;

/**
 * Created by Niow on 2016/10/9.
 */
public class OptionTemplate {

    private String type;

    private int timeout;

    //zadd属性
    private String score;
    //zadd属性
    private String scoreType;
    //hash属性
    private String field;
    //单独输出指定key的内容
    private String output;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getScoreType() {
        return scoreType;
    }

    public void setScoreType(String scoreType) {
        this.scoreType = scoreType;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
