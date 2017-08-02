package com.chh.dc.icp.parser.obd.reader.dna;

import com.chh.dc.icp.parser.obd.reader.ByteArrayReader;
import com.chh.dc.icp.util.ByteReaderUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static com.chh.dc.icp.util.ByteReaderUtil.readHexString;
import static com.chh.dc.icp.util.ByteReaderUtil.readInt;

/**
 * Created by Niow on 2016/7/28.
 */
public abstract class DNAReader extends ByteArrayReader {

    public static final int INDEX_DATA = 12;

    protected int readPacketHead(byte[] bs) {
        int i = (bs[0] << 8 & 0xff00) + (bs[1] & 0xff);
        return i;
    }

    protected static int readPacketIndex(byte[] bs) {
        return 0;
    }

    public static int readFeatureId(byte[] bs) {
        return bs[3] & 0xff;
    }

    protected String readDeviceId(byte[] bs) {
        StringBuilder sb = new StringBuilder();
        sb.append(ByteReaderUtil.readHexString(bs[4]));
        sb.append(ByteReaderUtil.readHexString(bs[5]));
        sb.append(ByteReaderUtil.readHexString(bs[6]));
        sb.append(ByteReaderUtil.readHexString(bs[7]));
        sb.append(ByteReaderUtil.readHexString(bs[8]));
        sb.append(ByteReaderUtil.readHexString(bs[9]));
        return sb.toString();
    }

    protected Date readDateTime(byte[] bs, int start) {
        int index = start;
        int hour = ByteReaderUtil.readInt(bs[index]);
        int minute = ByteReaderUtil.readInt(bs[++index]);
        int second = ByteReaderUtil.readInt(bs[++index]);
        int year = ByteReaderUtil.readInt(bs[++index]);
        int month = ByteReaderUtil.readInt(bs[++index]);
        int day = ByteReaderUtil.readInt(bs[++index]);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.YEAR, year + 2000);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }

    protected int readDataLength(byte[] bs) {
        int i = (bs[10] << 8 & 0xff00) + (bs[11] & 0xff);
        return i;
    }

//    public static long readU32(byte[] bs, int start) {
//        int index = start;
//        int b1 = readInt(bs[index]);
//        int b2 = readInt(bs[++index]);
//        int b3 = readInt(bs[++index]);
//        int b4 = readInt(bs[++index]);
//        long rs = (b1 << 12) + (b2 << 8) + (b3 << 4) + b4;
//        return rs;
//    }

    public static long readU8(byte[] bs, int start) {
        int index = start;
        int rs = readInt(bs[index]);
        return rs;
    }

    public static long readU16(byte[] bs, int start) {
        int index = start;
        int b1 = readInt(bs[index]);
        int b2 = readInt(bs[++index]);
        long rs = (b1 << 4) + b2;
        return rs;
    }

    public static long readU32(byte[] bs, int start) {
        int index = start;
        String b1 = readHexString(bs[index]);
        String b2 = readHexString(bs[++index]);
        String b3 = readHexString(bs[++index]);
        String b4 = readHexString(bs[++index]);

        long rs = Long.parseLong(b1 + b2 + b3 + b4, 16);
        return rs;
    }
}
