package com.chh.dc.icp.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chh.dc.icp.db.pojo.PidThreshold;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chh.dc.icp.db.dao.DtcDAO;
import com.chh.dc.icp.db.pojo.TDtc;

/**
 * Created by niow on 16/10/12.
 */
public class OBDAlarmCodeConverter {
    private static final Logger log = LoggerFactory.getLogger(OBDAlarmCodeConverter.class);
    private static Map<String, Integer> htwx2CommonMap = new HashMap<>();
    private static Map<Integer, String> dtcMap = new HashMap<>();
    private static Map<Integer, String> commonAlarmMap = new HashMap<>();
    public static final int HTWX_ALARM_FAULT_CODE = 100;
    private static DtcDAO dtcDao;

    /**
     * 加载数据
     */
    public static void loadData() {
        loadDtcToCache();
        loadWarningToCache();
        //loadPidThresholdToCache();
    }

    static {
        htwx2CommonMap.put("0x01", 27);
        htwx2CommonMap.put("0x02", 1);
        htwx2CommonMap.put("0x03", 2);
        htwx2CommonMap.put("0x04", 66);
        htwx2CommonMap.put("0x05", 67);
        htwx2CommonMap.put("0x06", 22);
        htwx2CommonMap.put("0x07", 7);
        htwx2CommonMap.put("0x08", 8);
        htwx2CommonMap.put("0x09", 69);
        htwx2CommonMap.put("0x0a", 9);
        htwx2CommonMap.put("0x0b", 10);
        htwx2CommonMap.put("0x0c", 21);
        htwx2CommonMap.put("0x0d", 11);
        htwx2CommonMap.put("0x0e", 70);
        htwx2CommonMap.put("0x0f", 12);
        htwx2CommonMap.put("0x10", 13);
        htwx2CommonMap.put("0x11", 14);
        htwx2CommonMap.put("0x12", 15);
        htwx2CommonMap.put("0x13", 16);
        htwx2CommonMap.put("0x14", 17);
        htwx2CommonMap.put("0x15", 18);
        htwx2CommonMap.put("0x16", 80);
        htwx2CommonMap.put("0x17", 81);
        htwx2CommonMap.put("0x18", 100);
        htwx2CommonMap.put("lost", 200);
        htwx2CommonMap.put("missing", 65);
        dtcMap.put(1, "燃油和空气侦查系统");
        dtcMap.put(2, "点火系统");
        dtcMap.put(3, "废气控制系统");
        dtcMap.put(4, "车速怠速控制系统");
        dtcMap.put(5, "电脑控制系统");
        dtcMap.put(6, "网络连接系统");
        dtcMap.put(7, "故障检测");
        dtcMap.put(8, "车身系统");
        dtcMap.put(9, "网络系统");
        dtcMap.put(10, "混合动力驱动系统");
        dtcMap.put(11, "底盘系统");
        dtcMap.put(12, "制造商自定义");
        dtcMap.put(13, "ISO/SAE预留");
    }


    public static int getCommonAlarmIdByHtwxId(String alarmType) {
        Integer i = htwx2CommonMap.get(alarmType);
        if (i == null) {
            return 0;
        }
        return i;
    }

    /**
     * 根据告警类型获取告警描述
     *
     * @param type
     * @return
     */
    public static String getCommonAlarmDesc(int type) {
        if (type <= 0)
            return "";
        return commonAlarmMap.get(type);
    }

    /**
     * 根据故障码获取故障码描述
     *
     * @param code
     * @return
     */
    public static String getDtcDescByCode(String code) {
        try {
            if (StringUtil.isNotNull(code)) {
                @SuppressWarnings("unchecked")
                Map<String, TDtc> dataMap = (Map<String, TDtc>) EhcacheUtils.get("dtcData");
                if (dataMap != null) {
                    TDtc d = dataMap.get(code);
                    if (d != null) {
                        String warningDesc = d.getValue() +
                                " " + dtcMap.get(d.getType()) +
                                " " + d.getDescription();
                        return warningDesc;
                    }
                }
            }
        } catch (Exception e) {
            log.error("根据故障码获取故障码信息异常", e);
        }
        return "";
    }

    /**
     * 根据故障码获取故障码信息
     *
     * @param code
     * @return
     */
    @SuppressWarnings("unchecked")
    public static TDtc getDtcByCode(String code) {
        try {
            if (StringUtil.isNotNull(code)) {
                Map<String, TDtc> dataMap = (Map<String, TDtc>) EhcacheUtils.get("dtcData");
                if (dataMap != null)
                    return dataMap.get(code);
            }
        } catch (Exception e) {
            log.error("根据故障码获取故障码信息异常", e);
        }
        return null;
    }

    /**
     * 根据PID获取阀值信息
     *
     * @param pid
     * @return
     */
    @SuppressWarnings("unchecked")
    public static PidThreshold getThresholdByPid(String pid) {
        try {
            if (StringUtil.isNotNull(pid)) {
                Map<String, PidThreshold> dataMap = (Map<String, PidThreshold>) EhcacheUtils.get("pidData");
                if (dataMap != null)
                    return dataMap.get(pid);
            }
        } catch (Exception e) {
            log.error("根据PID获取阀值信息异常", e);
        }
        return null;
    }

    /**
     * 获取故障码表数据放入缓存
     */
    private static void loadDtcToCache() {
        try {
            Map<String, TDtc> dtcMap = new HashMap<>();
            List<TDtc> dtcList = dtcDao.getDtcList();
            for (TDtc d : dtcList) {
                dtcMap.put(d.getValue(), d);
            }
            EhcacheUtils.put("dtcData", dtcMap);
            log.debug("初始化故障码表数据放入缓存成功，共【" + dtcList.size() + "】.");
        } catch (Exception e) {
            log.error("获取故障码表数据放入缓存异常", e);
        }
    }

    /**
     * 获取告警表数据放入缓存
     */
    private static void loadWarningToCache() {
        try {
            List<Map<String, Object>> warningList = dtcDao.getWarningList();
            for (Map<String, Object> m : warningList) {
                commonAlarmMap.put((int) m.get("value"), (String) m.get("description"));
            }
            log.debug("初始化告警表数据放入缓存成功，共【" + warningList.size() + "】.");
        } catch (Exception e) {
            log.error("初始化告警表数据放入缓存异常", e);
        }
    }

    /**
     * 获取PID阀值数据放入缓存
     */
    private static void loadPidThresholdToCache() {
        try {
            Map<String, PidThreshold> pidMap = new HashMap<>();
            List<PidThreshold> list = dtcDao.getPidThresholdList();
            for (PidThreshold p : list) {
                pidMap.put(p.getPid(), p);
            }
            EhcacheUtils.put("pidData", pidMap);
            log.debug("初始化PID阀值数据放入缓存成功，共【" + list.size() + "】.");
        } catch (Exception e) {
            log.error("初始化告警表数据放入缓存异常", e);
        }
    }

    public void setDtcDao(DtcDAO dtcDao) {
        this.dtcDao = dtcDao;
    }
}
