package com.chh.dc.icp.util.monitor;

import java.util.LinkedList;
import java.util.List;

/**
 * 监控器，可以监控实现MonitorAble的内容
 *
 * @author Niow
 * @ClassName: Monitor
 * @date: 2014-11-20
 */
public class Monitor {

    private LinkedList<MonitorAble> monitoredList = new LinkedList<MonitorAble>();

    /**
     * 注册添加一个被监控者
     *
     * @param monitored
     */
    public synchronized void addMonitored(MonitorAble monitored) {
        monitoredList.add(monitored);
    }

    /**
     * 从监控目录中移除被监控者
     *
     * @param index 被监控者目录序号
     */
    public synchronized void removeMonitored(int index) {
        monitoredList.remove(index);
    }

    /**
     * 获取监控列表
     *
     * @return
     */
    public List<String> getContents() {
        LinkedList<String> contents = new LinkedList<String>();
        for (MonitorAble monitored : monitoredList) {
            contents.add(monitored.getTitle());
        }
        return contents;
    }

    /**
     * 通过目录序列号获取详细描述信息
     *
     * @param index 目录序号
     * @return
     */
    public String getDescription(int index) {
        if (index > monitoredList.size()) {
            return null;
        }
        MonitorAble monitored = monitoredList.get(index);
        return monitored.getDescription();
    }

    /**
     * 获取被监控的对象
     *
     * @param index 目录序号
     * @return
     */
    public MonitorAble getMonitored(int index) {
        if (index > monitoredList.size()) {
            return null;
        }
        return monitoredList.get(index);
    }

}
