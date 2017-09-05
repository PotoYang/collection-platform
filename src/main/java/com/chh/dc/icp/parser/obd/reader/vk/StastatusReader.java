package com.chh.dc.icp.parser.obd.reader.vk;

import com.chh.dc.icp.db.dao.DtcDAO;
import com.chh.dc.icp.util.ByteReaderUtil;
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
public class StastatusReader extends VKReader {
    private static DtcDAO dtcDao = new DtcDAO();
    private static final Logger LOG = LoggerFactory.getLogger(StastatusReader.class);
    private static Map<Integer, String> alarmMap;
    private static Map<String, String> statMap;

    private int index = INDEX_DATA;

    public void init() throws Exception{
        alarmMap = dtcDao.getAlarmDtc();
        statMap = dtcDao.getStatDtc();
    }

    @Override
    public List<ParsedRecord> readRecord(byte[] bs) throws Exception {
//        init();
        String deviceId = readDeviceId(bs);
        List<ParsedRecord> list = new ArrayList<>();

        String str = new String(bs);
        parseGPS(list, str, bs);
        parseStateB(list, str, bs);
        /*指到功能项关键字*/
        index++;
        switch ((char) bs[index]) {
            //警情上报
            case 'A': {
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

    private void parseStateB(List<ParsedRecord> list, String str, byte[] bs) {
        ParsedRecord parsedRecord = new ParsedRecord("vk_stat");
        int index = str.indexOf("&B");
        if (index < 0)
            return;
        index += 2;
        Map<String,Object> map =  parsedRecord.getRecord();

        String BS01 = "&BS01" + (bs[index]&0x01-'0');
        map.put(BS01,statMap.get(BS01));
        String BS02 = "&BS02" + (bs[index]&0x02-'0');
        map.put(BS02,statMap.get(BS02));
        String BS03 = "&BS03" + (bs[index]&0x04-'0');
        map.put(BS03,statMap.get(BS03));
        String BS04 = "&BS04" + (bs[index]&0x08-'0');
        map.put(BS04,statMap.get(BS04));

        String BS11 = "&BS11" + (bs[index]&0x01-'0');
        map.put(BS11,statMap.get(BS11));
        String BS12 = "&BS12" + (bs[index]&0x02-'0');
        map.put(BS12,statMap.get(BS12));
        String BS13 = "&BS13" + (bs[index]&0x04-'0');
        map.put(BS13,statMap.get(BS13));
        String BS14 = "&BS14" + (bs[index]&0x08-'0');
        map.put(BS14,statMap.get(BS14));

        String BS21 = "&BS21" + (bs[index]&0x01-'0');
        map.put(BS21,statMap.get(BS21));
        int t = bs[index]&0x03-'0';
        String BS22 = "&BS22" + t;
        map.put(BS22,statMap.get(BS22));
        String BS23 = "&BS23" + (bs[index]&0x08-'0');
        map.put(BS14,statMap.get(BS14));

        String BS31 = "&BS31" + (bs[index]&0x01-'0');
        map.put(BS31,statMap.get(BS31));
        String BS32 = "&BS32" + (bs[index]&0x02-'0');
        map.put(BS32,statMap.get(BS32));
        String BS33 = "&BS33" + (bs[index]&0x04-'0');
        map.put(BS33,statMap.get(BS33));
        String BS34 = "&BS34" + (bs[index]&0x08-'0');
        map.put(BS34,statMap.get(BS34));

        String BS41 = "&BS31" + (bs[index]&0x01-'0');
        map.put(BS41,statMap.get(BS41));
        String BS42 = "&BS32" + (bs[index]&0x02-'0');
        map.put(BS42,statMap.get(BS42));
        String BS43 = "&BS33" + (bs[index]&0x04-'0');
        map.put(BS43,statMap.get(BS43));
        String BS44 = "&BS34" + (bs[index]&0x08-'0');
        map.put(BS44,statMap.get(BS44));

        list.add(parsedRecord);


    }

    private void parseGPS(List<ParsedRecord> list, String str, byte[] bs) {
        int index = str.indexOf("&A");
        if (index < 0)
            return;
        index += 2;
        ParsedRecord parsedRecord = new ParsedRecord("vk_gps");
        Date date = readDateAndtime(bs, index);
        index += 6;

        // 纬度信息
        double latitude = readLatitude(bs, index);
        index += 8;

        //经度信息
        double longitude = readLongitude(bs, index);
        index += 9;

        int b = bs[index] & 0x08;
        String latitudeFlag = (bs[index] & 0x04) == 0 ? "west" : "east";
        String longitudeFlag = (bs[index] & 0x02) == 0 ? "south" : "north";
        String located = (bs[index] & 0x04) == 0 ? "true" : "false";

        int speed = ByteReaderUtil.readIntU16(bs, index);
        index += 2;

        int direction = ByteReaderUtil.readIntU16(bs, index);
        index += 2;

        index += 7;
        if (Character.isDigit((char) bs[index])) {
            String HDOP_str = str.substring(index, index + 9);
            double HDOP = Double.parseDouble(HDOP_str);
            parsedRecord.getRecord().put("HOOP",HDOP);
            System.out.println("HDOP is " + HDOP);
        }

        parsedRecord.getRecord().put("time",date);
        parsedRecord.getRecord().put("latitude",latitude);
        parsedRecord.getRecord().put("longitude",longitude);
        parsedRecord.getRecord().put("latitudeFlag",latitudeFlag);
        parsedRecord.getRecord().put("longitudeFlag",longitudeFlag);
        parsedRecord.getRecord().put("located",located);
        parsedRecord.getRecord().put("speed",speed);
        parsedRecord.getRecord().put("direction",direction);



//        System.out.println("time is " + date);
//        System.out.println("latitude is " + latitude);
//        System.out.println("longitude is " + longitude);
//        System.out.println(latitudeFlag);
//        System.out.println(longitudeFlag);
//        System.out.println(located);
//        System.out.println("speed is " + speed);
//        System.out.println("direction is " + direction);

        list.add(parsedRecord);
    }
}
