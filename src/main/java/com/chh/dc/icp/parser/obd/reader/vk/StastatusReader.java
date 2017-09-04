package com.chh.dc.icp.parser.obd.reader.vk;

import com.chh.dc.icp.warehouse.ParsedRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 解析 状态类信息
 * 功能类型编码为 A
 * Created by 申卓 on 2017/9/4.
 */
public class StastatusReader extends VKReader {

    private static final Logger LOG = LoggerFactory.getLogger(StastatusReader.class);

    private int index = INDEX_DATA;

    @Override
    public List<ParsedRecord> readRecord(byte[] bs) {
        List<ParsedRecord> list = new ArrayList<>();
        /*指到功能项关键字*/
        index++;
        switch (bs[index]) {
            //警情上报
            case 'A': {
                parseAA(list, bs);
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

    private void parseAA(List<ParsedRecord> list, byte[] bs) {


    }
}
