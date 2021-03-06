package com.chh.dc.icp.parser.obd.reader.htwx;

import static com.chh.dc.icp.util.ByteReaderUtil.readHexString;
import static com.chh.dc.icp.util.ByteReaderUtil.readU8;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chh.dc.icp.parser.obd.reader.FieldInfo;
import com.chh.dc.icp.warehouse.ParsedRecord;

/**
 * @author wangbin
 * @date 2016年11月8日 下午3:43:40
 * @Description: TODO
 */
public class SnapReader extends HTWXReader {
    private Map<String, FieldInfo> fieldMap = new HashMap<String, FieldInfo>();

    {
        fieldMap.put("2100", new FieldInfo("f_2100", 4, true));
        fieldMap.put("2101", new FieldInfo("f_2101", 4, true));
        fieldMap.put("2102", new FieldInfo("f_2102", 2, true));
        fieldMap.put("2103", new FieldInfo("f_2103", 2));//1
        fieldMap.put("2104", new FieldInfo("f_2104", 1));
        fieldMap.put("2105", new FieldInfo("f_2105", 1));
        fieldMap.put("2106", new FieldInfo("f_2106", 1));
        fieldMap.put("2107", new FieldInfo("f_2107", 1));
        fieldMap.put("2108", new FieldInfo("f_2108", 1));
        fieldMap.put("2109", new FieldInfo("f_2109", 1));
        fieldMap.put("210a", new FieldInfo("f_210a", 2, true));
        fieldMap.put("210b", new FieldInfo("f_210b", 1));
        fieldMap.put("210c", new FieldInfo("f_210c", 2, true));
        fieldMap.put("210d", new FieldInfo("f_210d", 1));
        fieldMap.put("210e", new FieldInfo("f_210e", 1));
        fieldMap.put("210f", new FieldInfo("f_210f", 1));
        fieldMap.put("2110", new FieldInfo("f_2110", 2, 0.01, true));
        fieldMap.put("2111", new FieldInfo("f_2111", 1));
        fieldMap.put("2112", new FieldInfo("f_2112", 1));
        fieldMap.put("2113", new FieldInfo("f_2113", 1));
        fieldMap.put("2114", new FieldInfo("f_2114", 2));//1
        fieldMap.put("2115", new FieldInfo("f_2115", 2));//1
        fieldMap.put("2116", new FieldInfo("f_2116", 2));//1
        fieldMap.put("2117", new FieldInfo("f_2117", 2));//1
        fieldMap.put("2118", new FieldInfo("f_2118", 2));//1
        fieldMap.put("2119", new FieldInfo("f_2119", 2));//1
        fieldMap.put("211a", new FieldInfo("f_211a", 2));//1
        fieldMap.put("211b", new FieldInfo("f_211b", 2));//1
        fieldMap.put("211c", new FieldInfo("f_211c", 1));
        fieldMap.put("211d", new FieldInfo("f_211d", 1));
        fieldMap.put("211e", new FieldInfo("f_211e", 1));
        fieldMap.put("211f", new FieldInfo("f_211f", 2, true));
        fieldMap.put("2120", new FieldInfo("f_2120", 4, true));//
        fieldMap.put("2121", new FieldInfo("f_2121", 2, true));
        fieldMap.put("2122", new FieldInfo("f_2122", 2, 0.079, true));
        fieldMap.put("2123", new FieldInfo("f_2123", 2, true));
        fieldMap.put("2124", new FieldInfo("f_2124", 4, true));//2
        fieldMap.put("2125", new FieldInfo("f_2125", 4, true));//2
        fieldMap.put("2126", new FieldInfo("f_2126", 4, true));//2
        fieldMap.put("2127", new FieldInfo("f_2127", 4, true));//2
        fieldMap.put("2128", new FieldInfo("f_2128", 4, true));//2
        fieldMap.put("2129", new FieldInfo("f_2129", 4, true));//2
        fieldMap.put("212a", new FieldInfo("f_212a", 4, true));//2
        fieldMap.put("212b", new FieldInfo("f_212b", 4, true));//2
        fieldMap.put("212c", new FieldInfo("f_212c", 1));
        fieldMap.put("212d", new FieldInfo("f_212d", 1));
        fieldMap.put("212e", new FieldInfo("f_212e", 1));
        fieldMap.put("212f", new FieldInfo("f_212f", 1));
        fieldMap.put("2130", new FieldInfo("f_2130", 1));
        fieldMap.put("2131", new FieldInfo("f_2131", 2, true));
        fieldMap.put("2132", new FieldInfo("f_2132", 2, 0.25, true));
        fieldMap.put("2133", new FieldInfo("f_2133", 1));
        fieldMap.put("2134", new FieldInfo("f_2134", 4, true));//2
        fieldMap.put("2135", new FieldInfo("f_2135", 4, true));//2
        fieldMap.put("2136", new FieldInfo("f_2136", 4, true));//2
        fieldMap.put("2137", new FieldInfo("f_2137", 4, true));//2
        fieldMap.put("2138", new FieldInfo("f_2138", 4, true));//2
        fieldMap.put("2139", new FieldInfo("f_2139", 4, true));//2
        fieldMap.put("213a", new FieldInfo("f_213a", 4, true));//2
        fieldMap.put("213b", new FieldInfo("f_213b", 4, true));//2
        fieldMap.put("213c", new FieldInfo("f_213c", 2, true));
        fieldMap.put("213d", new FieldInfo("f_213d", 2, true));
        fieldMap.put("213e", new FieldInfo("f_213e", 2, true));
        fieldMap.put("213f", new FieldInfo("f_213f", 2, true));
        fieldMap.put("2140", new FieldInfo("f_2140", 4, true));//
        fieldMap.put("2141", new FieldInfo("f_2141", 4, true));//
        fieldMap.put("2142", new FieldInfo("f_2142", 2, 0.001, true));
        fieldMap.put("2143", new FieldInfo("f_2143", 1));
        fieldMap.put("2144", new FieldInfo("f_2144", 2, 0.0000305, true));
        fieldMap.put("2145", new FieldInfo("f_2145", 1));
        fieldMap.put("2146", new FieldInfo("f_2146", 1));
        fieldMap.put("2147", new FieldInfo("f_2147", 1));
        fieldMap.put("2148", new FieldInfo("f_2148", 1));
        fieldMap.put("2149", new FieldInfo("f_2149", 1));
        fieldMap.put("214a", new FieldInfo("f_214a", 1));
        fieldMap.put("214b", new FieldInfo("f_214b", 1));
        fieldMap.put("214c", new FieldInfo("f_214c", 1));
        fieldMap.put("214d", new FieldInfo("f_214d", 2, true));
        fieldMap.put("214e", new FieldInfo("f_214e", 2, true));
        fieldMap.put("214f", new FieldInfo("f_214f", 4, true));//
        fieldMap.put("2150", new FieldInfo("f_2150", 2, true));
        fieldMap.put("2151", new FieldInfo("f_2151", 1));
        fieldMap.put("2152", new FieldInfo("f_2152", 1));
        fieldMap.put("2153", new FieldInfo("f_2153", 2, 0.005, true));
        fieldMap.put("2154", new FieldInfo("f_2154", 2, true));
        fieldMap.put("2155", new FieldInfo("f_2155", 2));//1
        fieldMap.put("2156", new FieldInfo("f_2156", 2));//1
        fieldMap.put("2157", new FieldInfo("f_2157", 2));//1
        fieldMap.put("2158", new FieldInfo("f_2158", 2));//1
        fieldMap.put("2159", new FieldInfo("f_2159", 2, true));
        fieldMap.put("215a", new FieldInfo("f_215a", 1));
        fieldMap.put("215b", new FieldInfo("f_215b", 1));
        fieldMap.put("215c", new FieldInfo("f_215c", 1));
        fieldMap.put("215d", new FieldInfo("f_215d", 2, true));
        fieldMap.put("215e", new FieldInfo("f_215e", 2, true));
//        fieldMap.put("215f",new FieldInfo(f_"215f",预留));
        fieldMap.put("2160", new FieldInfo("f_2160", 4, true));
        fieldMap.put("2161", new FieldInfo("f_2161", 1));
        fieldMap.put("2162", new FieldInfo("f_2162", 1));
        fieldMap.put("2163", new FieldInfo("f_2163", 2, true));
        fieldMap.put("2164", new FieldInfo("f_2164", 4, true));
    }


    @Override
    public List<ParsedRecord> readRecord(byte[] bs) {
        String deviceId = readDeviceId(bs);
        List<ParsedRecord> list = new ArrayList<ParsedRecord>();
        Map<String, Object> statDataMap = new HashMap<>();
        int index = INDEX_DATA;
        //stat_data
        index = readStatData(statDataMap, bs, index);
        //frozen_flag =0 快照数据  =1 冻结帧数据
//        int frozenFlag = readU8(bs, index);
        index += 1;
        //data_count 数据流个数
        int dataCount = readU8(bs, index);
        index += 1;
        if (dataCount > 0) {
            //data_type_array
            List<String> dataTypeList = new ArrayList<>();
            for (int i = 0; i < dataCount; i++) {
                String dataType = readHexString(bs[index + 1]) + readHexString(bs[index]);
                index += 2;
                dataTypeList.add(dataType);
            }
            //data_content
            ParsedRecord snapData = new ParsedRecord("htwx_snap");
            Map<String, Object> snapMap = snapData.getRecord();
            snapMap.put("device_id", deviceId);
            snapMap.put("last_accon_time", statDataMap.get("last_accon_time"));
            snapMap.put("last_accon_time_sec", statDataMap.get("last_accon_time_sec"));
            snapMap.put("utctime", statDataMap.get("utctime"));
            snapMap.put("collection_time", statDataMap.get("collection_time"));

            for (int j = 0; j < dataCount; j++) {
                String type = dataTypeList.get(j);
                FieldInfo fieldInfo = fieldMap.get(type);
                readData(bs, index, fieldInfo, snapMap);
                index += fieldInfo.getType();
            }
            list.add(snapData);
        }
        return list;
    }


}
