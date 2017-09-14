package com.chh.dc.icp.parser.obd.reader.vk;

import com.chh.dc.icp.db.dao.DtcDAO;
import com.chh.dc.icp.parser.obd.reader.ByteArrayReader;
import com.chh.dc.icp.util.ByteReaderUtil;
import com.chh.dc.icp.warehouse.ParsedRecord;

import java.util.*;

/**
 * Created by 申卓 on 2017/9/4.
 */
public abstract class VKReader extends ByteArrayReader {


    protected static Map<Integer, String> alarmMap = new HashMap<>();
    static {
        alarmMap.put (10000, "防拆报警");
        alarmMap.put (10001, "见光报警");
        alarmMap.put (10002, "磁控报警");
        alarmMap.put (10003, "蓝牙断开链接报警");
        alarmMap.put (20000, "紧急报警/SOS/劫警");
        alarmMap.put (20001, "盗警/非法进入报警");
        alarmMap.put (20002, "震动报警");
        alarmMap.put (20003, "碰撞报警");
        alarmMap.put (20010, "进范围报警");
        alarmMap.put (20011, "出范围报警");
        alarmMap.put (20012, "超速报警");
        alarmMap.put (20013, "偏离路线报警");
        alarmMap.put (20020, "非法时段行驶报警");
        alarmMap.put (20021, "停车休息时间不足报警");
        alarmMap.put (20022, "位移报警/非法移动报警/越站报警");
        alarmMap.put (20023, "非法开车门");
        alarmMap.put (20030, "暗锁报警");
        alarmMap.put (20031, "断电报警/剪线报警");
        alarmMap.put (20032, "外部电瓶电压低报警");
        alarmMap.put (20033, "推车报警");
        alarmMap.put (20040, "停车未熄火报警/禁行报警");
        alarmMap.put (20041, "急加速报警（OBD）");
        alarmMap.put (20042, "急减速报警（OBD）");
        alarmMap.put (20043, "冷却液温度过高报警（OBD）");
        alarmMap.put (30000, "光感报警");
        alarmMap.put (30001, "磁感报警");
        alarmMap.put (30002, "防拆报警");
        alarmMap.put (30003, "充电过压报警");
        alarmMap.put (30010, "锁头报警/非法启动报警");
        alarmMap.put (30011, "侧翻报警");
        alarmMap.put (30012, "电源接触不良报警");
        alarmMap.put (30013, "内部电池低电压报警");
        alarmMap.put (30020, "防屏蔽报警");
        alarmMap.put (30021, "蓝牙防丢报警");
        alarmMap.put (30022, "电池电源不足关机报警");
        alarmMap.put (30023, "屏蔽断油报警");
        alarmMap.put (30030, "急转弯报警");
    }

    protected int index = INDEX_DATA;



    /**
     * MG20{}  从第6位开始是id
     **/
    public static final int INDEX_ID = 6;

    public static final int INDEX_PROTOCOL_TYPE = 25;

    /**
     * 从第23位开始 是功能类型编码
     */
    public static final int INDEX_DATA = 23;

    /**
     * 读取接收设备的id
     *
     * @param bs
     * @return
     */
    public static String readDeviceId(byte[] bs) {
        int index = INDEX_ID;
        StringBuilder sb = new StringBuilder();
        int i;
        while ((i = bs[index++]) != ',') {
            sb.append((char) i);
        }
        return sb.toString().trim();
    }

    /**
     * 假设数据为固定长度  功能类型编码在 第23位 based 1
     *
     * @param bs
     * @return
     */
    public static char readFeatureId(byte[] bs) {
        int index = 22;
        return (char)bs[index];
    }


    /**
     * &A 附加信息中读取时间数据
     *
     * @param bs
     * @param start Ahhmmss
     * @return
     */
    protected Date readDateAndtime(byte[] bs, int start) {
        int start2 = start + 28;
        int start1 = start;
        int day = ByteReaderUtil.readAscii2byte(bs, start2);
        start2 += 2;
        int month = ByteReaderUtil.readAscii2byte(bs, start2);
        start2 += 2;
        int year = ByteReaderUtil.readAscii2byte(bs, start2);


        int hour = ByteReaderUtil.readAscii2byte(bs, start1);
        start1 += 2;
        int minute = ByteReaderUtil.readAscii2byte(bs, start1);
        start1 += 2;
        int second = ByteReaderUtil.readAscii2byte(bs, start1);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.YEAR, year + 2000);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        return calendar.getTime();
    }

    /**
     * 读取 GPS 经度
     * 是 9 位的经度信息，后 4 位为小数部分，
     *
     * @param bs
     * @param start
     * @return
     */
    protected double readLongitude(byte[] bs, int start) {
        if (bs.length < start + 8) {
            return 0.0;
        }
        double degree = (bs[start] ) * 100 + (bs[start + 1] ) * 10 + (bs[start + 2] );
        double decimal = (bs[start + 3] ) * 10 + (bs[start + 4] ) + (bs[start + 5] ) * 0.1 +
                (bs[start + 6] ) * 0.01 + (bs[start + 7] ) * 0.01 + (bs[start + 8] ) * 0.001;

        return degree + decimal / 60.0;
    }

    /**
     * 读取 GPS 纬度
     * 是 8 位的纬度信息，后 4 位为小数部分，
     *
     * @param bs
     * @param start
     * @return
     */
    private double readLatitude(byte[] bs, int start) {
        double degree = (bs[start] ) * 10 + (bs[start + 1] );
        double decimal = (bs[start + 2] ) * 10 + (bs[start + 3] ) + (bs[start + 4] ) * 0.1 +
                (bs[start + 5] ) * 0.01 + (bs[start + 6] ) * 0.01 + (bs[start + 7] ) * 0.001;

        return degree + decimal / 60.0;
    }

    protected void parseBHCFG_Can(List<ParsedRecord> list, String str, byte[] bs, Date date) {
        ParsedRecord parsedRecord = new ParsedRecord("vk_can");
        Map<String, Object> map = parsedRecord.getRecord();

        int index = str.indexOf("&B");
        if (index > 0) {
            index += 2;

            map.put("device_id", readDeviceId(bs));
            if (date != null) {
                map.put("utctime", date);
            }
            map.put("collection_time", new Date());


            map.put("bs00", (bs[index] & 0x01 ));
            map.put("bs01", (bs[index] & 0x02 ));
            map.put("bs02", (bs[index] & 0x04 ));
            map.put("bs03", (bs[index] & 0x08 ));
            index++;


            map.put("bs10", (bs[index] & 0x01 ));
            map.put("bs11", (bs[index] & 0x02 ));
            map.put("bs12", (bs[index] & 0x04 ));
            map.put("bs13", (bs[index] & 0x08 ));
            index++;

            map.put("bs20", (bs[index] & 0x01 ));
            map.put("bs212", (bs[index] & 0x06 ));
            map.put("bs23", (bs[index] & 0x08 ));
            index++;

            map.put("bs30", (bs[index] & 0x01 ));
            map.put("bs31", (bs[index] & 0x02 ));
            map.put("bs32", (bs[index] & 0x04 ));
            map.put("bs33", (bs[index] & 0x08 ));
            index++;

            map.put("bs40", (bs[index] & 0x01 ));
            map.put("bs41", (bs[index] & 0x02 ));
            map.put("bs42", (bs[index] & 0x04 ));
            map.put("bs43", (bs[index] & 0x08 ));
            index++;
        }
        index = str.indexOf("&H");
        if (index > 0) {
            map.put("hs00", (bs[index] & 0x01 ));
            map.put("hs01", (bs[index] & 0x02 ));
            map.put("hs02", (bs[index] & 0x04 ));
            map.put("hs03", (bs[index] & 0x08 ));
            map.put("hs04", (bs[index] >> 4 & 0x01 ));
            map.put("hs05", (bs[index] >> 4 & 0x02 ));
            map.put("hs06", (bs[index] >> 4 & 0x04 ));
            map.put("hs07", (bs[index] >> 4 & 0x08 ));
            index++;

            map.put("hs10", (bs[index] & 0x01 ));
            map.put("hs11", (bs[index] & 0x02 ));
            map.put("hs12", (bs[index] & 0x04 ));
            map.put("hs13", (bs[index] & 0x08 ));
            map.put("hs14", (bs[index] >> 4 & 0x01 ));
            map.put("hs15", (bs[index] >> 4 & 0x02 ));
            index++;

            map.put("hs20", (bs[index] & 0x01 ));
            map.put("hs21", (bs[index] & 0x02 ));
            map.put("hs22", (bs[index] & 0x04 ));
            map.put("hs23", (bs[index] & 0x08 ));
            map.put("hs24", (bs[index] >> 4 & 0x01 ));
            map.put("hs25", (bs[index] >> 4 & 0x02 ));
            map.put("hs26", (bs[index] >> 4 & 0x04 ));
            map.put("hs27", (bs[index] >> 4 & 0x08 ));
            index++;

            map.put("hs30", (bs[index] & 0x01 ));
            map.put("hs31", (bs[index] & 0x02 ));
            map.put("hs32", (bs[index] & 0x04 ));
            map.put("hs33", (bs[index] & 0x08 ));
            map.put("hs34", (bs[index] >> 4 & 0x01 ));
            map.put("hs35", (bs[index] >> 4 & 0x02 ));
            map.put("hs36", (bs[index] >> 4 & 0x04 ));
            index++;
        }
        index = str.indexOf("&C");
        if (index > 0) {
            parseC_mileage(map, bs, index);
        }
        index = str.indexOf("&F");
        if (index > 0) {
            parseF_speed(map, bs, index);
        }
        index = str.indexOf("&G");
        if (index > 0) {
            parseG_height(map, bs, index);
        }

        /**
         * OBD 实时行程数据
         */
        index = str.indexOf("&R");
        if (index > 0) {
            int end = str.indexOf('&', index + 1);
            if (end < 0) {
                end = str.length() - 1;
            }
            parseR_OBD(map, bs, index, end);
        }

        list.add(parsedRecord);
    }

    /**
     * 解析GPS中的
     *
     * @param list
     * @param str
     * @param bs
     */
    protected Date parseA_GPS(List<ParsedRecord> list, String str, byte[] bs) {
        /******************&A****************/
        int index = str.indexOf("&A");
        if (index < 0)
            return null;
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
            parsedRecord.getRecord().put("hdop", HDOP);
        }

        parsedRecord.getRecord().put("device_id", readDeviceId(bs));
        parsedRecord.getRecord().put("collection_time", new Date());
        parsedRecord.getRecord().put("utctime", date);
        parsedRecord.getRecord().put("latitude", latitude);
        parsedRecord.getRecord().put("longitude", longitude);
        parsedRecord.getRecord().put("latitude_flag", latitudeFlag);
        parsedRecord.getRecord().put("longitude_flag", longitudeFlag);
        parsedRecord.getRecord().put("located", located);
        parsedRecord.getRecord().put("speed", speed);
        parsedRecord.getRecord().put("direction", direction);

        list.add(parsedRecord);

        return date;
    }

    private void parseC_mileage(Map<String, Object> map, byte[] bs, int index) {
        index += 2;

    }

    private void parseF_speed(Map<String, Object> map, byte[] bs, int index) {
        index += 2;
        int hundred = bs[index] ;
        int ten = bs[index + 1] ;
        int bit = bs[index + 2] ;
        int decimal = bs[index + 3] ;
        // 单位 节
        double speed = hundred * 100 + ten * 10 + bit + decimal * 0.1;
        // 单位  km
        speed *= 1.852;
        map.put("speed", speed);
    }

    private void parseG_height(Map<String, Object> map, byte[] bs, int index) {
        index += 2;
        String strHeight = new String(bs, index, 5);
        int intHeight = Integer.parseInt(strHeight);
        double height = intHeight + 0.1 * (bs[index + 5] );
        if (height > 20000) {
            height = -height;
        }
        map.put("height", height);
    }

    protected void parseK_weigh(List<ParsedRecord> list, String str, byte[] bs) {

    }

    protected void parseM_capacity(List<ParsedRecord> list, String str, byte[] bs) {

    }

    protected void parseN_GSM(List<ParsedRecord> list, String str, byte[] bs) {

    }

    protected void parseO_GPS(List<ParsedRecord> list, String str, byte[] bs) {

    }

    private void parseR_OBD(Map<String, Object> map, byte[] bs, int start, int end) {
        start += 2;
        String str = new String(bs, start, end - start);
        String[] strArr = str.split(",");
        int mileage = Integer.parseInt(strArr[0].trim());
        int fuelConsumption = Integer.parseInt(strArr[1].trim());
        int fuelPerKM = Integer.parseInt(strArr[2].trim());
        int startTime = Integer.parseInt(strArr[3].trim());

        map.put("mileage", mileage);
        map.put("fuel_consumption", fuelConsumption);
        map.put("fuel_per_km", fuelPerKM);
        map.put("start_time", startTime);
    }

    protected void parseAlarm(List<ParsedRecord> list, String str, byte[] bs, Date utc_time) {

        List<ParsedRecord> parsedRecords = new ArrayList<>();
        int index = str.indexOf("&B");
        if (index > 0) {
            index += 7;
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 4; j++) {
                    int k = bs[index + i] & ((byte) (0x1 << j));
                    if (k != 0) {
                        ParsedRecord parsedRecord = createParsedRecord(bs,utc_time);
                        parsedRecord.getRecord().put("alarm_type", alarmMap.get("200" + i + "" + j));
                        parsedRecord.getRecord().put("alarm_description", alarmMap.get("200" + i + "" + j));
                        parsedRecords.add(parsedRecord);
                    }
                }
            }
        }
        index = str.indexOf("&W");
        if (index > 0) {
            index += 2;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 4; j++) {
                    int k = bs[index + i] & ((byte) (0x1 << j));
                    if (k != 0) {
                        ParsedRecord parsedRecord = createParsedRecord(bs,utc_time);
                        parsedRecord.getRecord().put("alarm_type", "300" + i + "" + j);
                        parsedRecord.getRecord().put("alarm_description", alarmMap.get("300" + i + "" + j));
                        parsedRecords.add(parsedRecord);
                    }
                }
            }
            if (bs.length-1 > index+3 && Character.isDigit(bs[index+3])){
                int k = bs[index + 3] & ((byte) (0x1));
                if (k != 0){
                    ParsedRecord parsedRecord = createParsedRecord(bs,utc_time);
                    parsedRecord.getRecord().put("alarm_type", alarmMap.get("30030"));
                    parsedRecord.getRecord().put("alarm_description", alarmMap.get("30030"));
                    parsedRecords.add(parsedRecord);
                }
            }
        }
        list.addAll(parsedRecords);
    }

    private ParsedRecord createParsedRecord(byte[] bs, Date utc_time){
        ParsedRecord parsedRecord = new ParsedRecord("vk_alarm");
        Map<String, Object> map = parsedRecord.getRecord();

        map.put("device_id", readDeviceId(bs));
        if (utc_time != null) {
            map.put("utctime", utc_time);
        }
        map.put("collection_time", new Date());
        return parsedRecord;
    }

}

