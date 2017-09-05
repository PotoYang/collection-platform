package com.chh.dc.icp.parser.obd.reader.vk;

import com.chh.dc.icp.parser.obd.reader.ByteArrayReader;
import com.chh.dc.icp.util.ByteReaderUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by 申卓 on 2017/9/4.
 */
public abstract class VKReader extends ByteArrayReader {

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
    public static String readFeatureId(byte[] bs) {
        int index = 22;
        return String.valueOf(bs[index]);
    }


//    public abstract List<ParsedRecord> readRecord(byte[] bs) throws Exception;
//
//    protected Date readDateTime(byte[] bs, int start) {
//        long seconds = ByteReaderUtil.readU32(bs, start, true);
//        Date dateTime = new Date(seconds * 1000L);
//        return dateTime;
//    }

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
        double degree = (bs[start] - '0') * 100 + (bs[start + 1] - '0') * 10 + (bs[start + 2] - '0');
        double decimal = (bs[start + 3] - '0') * 10 + (bs[start + 4] - '0') + (bs[start + 5] - '0') * 0.1 +
                (bs[start + 6] - '0') * 0.01 + (bs[start + 7] - '0') * 0.01 + (bs[start + 8] - '0') * 0.001;

        return degree + decimal / 60.0;
    }

    protected double readLatitude(byte[] bs, int start){
        double degree = (bs[start] - '0') * 10 + (bs[start + 1] - '0');
        double decimal = (bs[start + 2] - '0') * 10 + (bs[start + 3] - '0') + (bs[start + 4] - '0') * 0.1 +
                (bs[start + 5] - '0') * 0.01 + (bs[start + 6] - '0') * 0.01 + (bs[start + 7] - '0') * 0.001;

        return degree + decimal / 60.0;
    }


//    protected int readStatData(Map<String, Object> map, byte[] bs, int start) {
//        int index = start;
//        String deviceId = readDeviceId(bs);
//        map.put("device_id", deviceId);
//        long seconds = ByteReaderUtil.readU32(bs, start, true);
//        map.put("last_accon_time_sec", seconds);
//        map.put("last_accon_time", new Date(seconds * 1000L));
//        index += 4;
//        //utc_time是关键字
//        map.put("utctime", readDateTime(bs, index));
//        index += 4;
//        map.put("total_trip_mileage", ByteReaderUtil.readU32(bs, index, true));
//        index += 4;
//        map.put("current_trip_milea", ByteReaderUtil.readU32(bs, index, true));
//        index += 4;
//        map.put("total_fuel", ByteReaderUtil.readU32(bs, index, true) * 0.01);
//        index += 4;
//        map.put("current_fuel", ByteReaderUtil.readU16(bs, index, true) * 0.01);
//        index += 2;
////        index = readVStatus(map, bs, index);
//        map.put("vstate", ByteReaderUtil.readU32(bs, index));
//        index += 4;
//        map.put("reserve", ByteReaderUtil.readHexString(bs, index, 8));
//        index += 8;
//        map.put("collection_time", new Date());
//        return index;
//    }

//    protected int readVStatus(Map<String, Object> map, byte[] bs, int start) {
//        int index = start;
//        byte s0 = bs[index++];
//        map.put("s07", ByteReaderUtil.readBit(s0, 0));
//        map.put("s06", ByteReaderUtil.readBit(s0, 1));
//        map.put("s05", ByteReaderUtil.readBit(s0, 2));
//        map.put("s04", ByteReaderUtil.readBit(s0, 3));
//        map.put("s03", ByteReaderUtil.readBit(s0, 4));
//        map.put("s02", ByteReaderUtil.readBit(s0, 5));
//        map.put("s01", ByteReaderUtil.readBit(s0, 6));
//        map.put("s00", ByteReaderUtil.readBit(s0, 7));
//        byte s1 = bs[index++];
//        map.put("s17", ByteReaderUtil.readBit(s1, 0));
//        map.put("s16", ByteReaderUtil.readBit(s1, 1));
//        map.put("s15", ByteReaderUtil.readBit(s1, 2));
//        map.put("s14", ByteReaderUtil.readBit(s1, 3));
//        map.put("s13", ByteReaderUtil.readBit(s1, 4));
//        map.put("s12", ByteReaderUtil.readBit(s1, 5));
//        map.put("s11", ByteReaderUtil.readBit(s1, 6));
//        map.put("s10", ByteReaderUtil.readBit(s1, 7));
//        byte s2 = bs[index++];
//        map.put("s27", ByteReaderUtil.readBit(s2, 0));
//        map.put("s26", ByteReaderUtil.readBit(s2, 1));
//        map.put("s25", ByteReaderUtil.readBit(s2, 2));
//        map.put("s24", ByteReaderUtil.readBit(s2, 3));
//        map.put("s23", ByteReaderUtil.readBit(s2, 4));
//        map.put("s22", ByteReaderUtil.readBit(s2, 5));
//        map.put("s21", ByteReaderUtil.readBit(s2, 6));
//        map.put("s20", ByteReaderUtil.readBit(s2, 7));
//        byte s3 = bs[index++];
//        map.put("s37", ByteReaderUtil.readBit(s3, 0));
//        map.put("s36", ByteReaderUtil.readBit(s3, 1));
//        map.put("s35", ByteReaderUtil.readBit(s3, 2));
//        map.put("s34", ByteReaderUtil.readBit(s3, 3));
//        map.put("s33", ByteReaderUtil.readBit(s3, 4));
//        map.put("s32", ByteReaderUtil.readBit(s3, 5));
//        map.put("s31", ByteReaderUtil.readBit(s3, 6));
//        map.put("s30", ByteReaderUtil.readBit(s3, 7));
//        return index;
//    }

//    protected List<ParsedRecord> readGPSData(byte[] bs, int start) {
//        ////flag 0x00 表示常规 GPS 数据上传 0x01 表示历史 GPS 数据上传
//        int flag = ByteReaderUtil.readU8(bs, INDEX_DATA);
//        String type = flag == 0 ? "htwx_gps" : "htwx_gps_his";
//        int index = start;
//        int gpsCount = ByteReaderUtil.readInt(bs[index++]);
//        if (gpsCount <= 0) {
//            return null;
//        }
//        String deviceId = readDeviceId(bs);
//        List<ParsedRecord> list = new ArrayList<ParsedRecord>();
//        Date now = new Date();
//        for (int i = 0; i < gpsCount; i++) {
//            ParsedRecord record = new ParsedRecord(type);
//            index = readGPSItem(record.getRecord(), bs, index);
//            record.putData("device_id", deviceId);
//            record.putData("collection_time", now);
//            //判断是否定位，如果未定位，则当做历史数据类型（历史数据类型不输出lastGpsCache）
//            boolean isLocated = (boolean) record.getData("isLocated");
//            if (!isLocated) {
//                record.setType("htwx_gps_his");
//            }
//            list.add(record);
//        }
//        return list;
//    }

//    protected int readGPSItem(Map<String, Object> map, byte[] bs, int start) {
//        int index = start;
//        map.put("gps_time", readDateAndtime(bs, index));
//        index += 6;
//        double lat = ByteReaderUtil.readU32(bs, index, true) / 3600000.0;
//        index += 4;
//        double lon = ByteReaderUtil.readU32(bs, index, true) / 3600000.0;
//        index += 4;
//        //speed cm/sec转成m/sec
//        map.put("speed", ByteReaderUtil.readU16(bs, index, true) / 100.0);
//        index += 2;
//        //dir 1/10度转成度
//        map.put("dir", ByteReaderUtil.readU16(bs, index, true) / 10.0);
//        index += 2;
////       读取valflag 小头bit7-bit0
//        int ew = ByteReaderUtil.readBit(bs[index], 7 - 0);
////      ew：1为东经，0为西经
//        if (ew == 0) {
//            lat = lat * -1;
//        }
//        map.put("lat", lat);
////       sn:1为北纬,0为南纬 小头bit7-bit0
//        int sn = ByteReaderUtil.readBit(bs[index], 7 - 1);
//        if (sn == 0) {
//            lon = lon * -1;
//        }
//        map.put("lon", lon);
////        int satellites = readU8(bs, index) & 0x0f;
////        map.put("satellites", satellites);
////        index += 1;
//        byte valflagBytes = bs[index++];
//        //定位标志  00---未定位 01---2D 定位 11---3D 定位   小头bit7-bit0
//        String locatedNo = String.valueOf(ByteReaderUtil.readBit(valflagBytes, 7 - 2)) + ByteReaderUtil.readBit(valflagBytes, 7 - 3);
//        //true：定位，false：未定位
//        boolean isLocated = false;
//        if (StringUtil.isNotNull(locatedNo) && !"00".equals(locatedNo))
//            isLocated = true;
//        map.put("isLocated", isLocated);
//        return index;
//    }

    protected List<Integer> readRpmItem(byte[] bs, int start) {
        int index = start;
        int rpmCount = ByteReaderUtil.readInt(bs[index++]);
        if (rpmCount <= 0) {
            return null;
        }
        return null;
    }


}

