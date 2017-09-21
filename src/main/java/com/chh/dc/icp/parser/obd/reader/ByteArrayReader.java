package com.chh.dc.icp.parser.obd.reader;

import com.chh.dc.icp.util.ByteReaderUtil;
import com.chh.dc.icp.warehouse.ParsedRecord;

import java.util.List;
import java.util.Map;

/**
 * Created by Niow on 2016/7/28.
 */
public abstract class ByteArrayReader {

    public static void readData(byte[] bs, int index, FieldInfo fieldInfo, Map<String, Object> map) {
        int type = fieldInfo.getType();
        long rs = 0;
        switch (type) {
            case 1: {
                rs = ByteReaderUtil.readU8(bs, index);
                break;
            }
            case 2: {
                if (fieldInfo.isReverse()) {
                    rs = ByteReaderUtil.readU16(bs, index, true);
                    break;
                }
                rs = ByteReaderUtil.readU16(bs, index);
                break;
            }
            case 4: {
                if (fieldInfo.isReverse()) {
                    rs = ByteReaderUtil.readU32(bs, index, true);
                    break;
                }
                rs = ByteReaderUtil.readU32(bs, index, true);
                break;
            }
        }
        if (fieldInfo.getCoefficient() != null) {
            map.put(fieldInfo.getName(), rs * fieldInfo.getCoefficient());
            return;
        }
        if (fieldInfo.getType() < 4) {
            map.put(fieldInfo.getName(), (int) rs);
        } else {
            map.put(fieldInfo.getName(), rs);
        }
    }

    abstract public List<ParsedRecord> readRecord(byte[] bs) throws Exception;

}
