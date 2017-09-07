package com.chh.dc.icp.parser.obd.reader.vk;

import com.chh.dc.icp.warehouse.ParsedRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 解析 状态类信息
 * 功能类型编码为 A
 * Created by 申卓 on 2017/9/4.
 */
public class CanReader extends VKReader {
    private static final Logger LOG = LoggerFactory.getLogger(CanReader.class);


    @Override
    public List<ParsedRecord> readRecord(byte[] bs) throws Exception {
        List<ParsedRecord> list = new ArrayList<>();

        String str = new String(bs);

        Date utc_time = parseA_GPS(list, str, bs);
        parseBHCFG_Can(list, str, bs, utc_time);

        parseAlarm(list, str, bs, utc_time);

        /*指到功能项关键字*/
        index++;
        switch ((char) bs[index]) {
            //警情上报
            case 'A': {
                parseA(list, bs, utc_time);
                break;
            }
            //登录信息
            case 'B': {
                break;
            }
            //脱网信息
            case 'C': {
                break;
            }
            //在线待命信息 心跳
            case 'H': {
                break;
            }
            //上传终端参数
            case 'J': {
                break;
            }
            //查询OBD相关参数
            case 'K': {
                break;
            }
            //上传进出范围报警信息
            case 'N': {
                break;
            }
            //上传电话或者短信查位置请求
            case 'U': {
                break;
            }
            //圆形范围设置的回复信息
            case 'V': {
                break;
            }
            //同步中端参数
            case 'W': {
                break;
            }
            //控制类指令执行结果回复
            case 'Z': {
                break;
            }
        }
        return list;
    }

    public void parseA(List<ParsedRecord> list, byte[] bs, Date date) {
        index++;
        ParsedRecord parsedRecord = new ParsedRecord("vk_alarm");
        Map<String, Object> map = parsedRecord.getRecord();

        map.put("device_id", readDeviceId(bs));
        if (date != null) {
            map.put("utc_time", date);
        }
        map.put("collection_time", new Date());

        int t = bs[index];
        map.put("alarm_type","1000"+ t);
        map.put("alarm_description", alarmMap.get("1000"+ t));

        list.add(parsedRecord);
    }
}
