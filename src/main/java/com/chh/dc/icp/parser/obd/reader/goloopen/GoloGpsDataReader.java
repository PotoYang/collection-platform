package com.chh.dc.icp.parser.obd.reader.goloopen;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chh.dc.icp.accessor.model.GoloResp;
import com.chh.dc.icp.parser.obd.reader.GoloObjectReader;
import com.chh.dc.icp.util.StringUtil;
import com.chh.dc.icp.warehouse.ParsedRecord;
import com.chh.dc.icp.accessor.model.GoloTripRecordByPage.TripRecordItem;
import com.chh.dc.icp.util.DateUtil;

/**
 * golo open gps
 *
 * @author fulr
 */
public class GoloGpsDataReader implements GoloObjectReader {

//	private static Logger LOG = LoggerFactory.getLogger(GoloGpsDataReader.class);

    @Override
    public List<ParsedRecord> readRecords(GoloResp resp) throws Exception {
        List<ParsedRecord> res = new ArrayList<ParsedRecord>();
        ParsedRecord rec = null;
        Object da = resp.getData();
        if (da instanceof JSONArray) {
            JSONArray ja = (JSONArray) da;
            for (Iterator<Object> it = ja.iterator(); it.hasNext(); ) {
                rec = readRecord(resp, (JSONObject) it.next());
                res.add(rec);
            }
        } else if (da instanceof JSONObject) {
            rec = readRecord(resp, (JSONObject) da);
            res.add(rec);
        } else {
            //数据格式错误
            //TODO
            res = null;
        }

//		//2.如果行程中不包含起点和终点坐标，用抓取到的gps更新行程信息
//		setTripGpsInfo(res, resp);

        return res;
    }

    /**
     * 如果行程中不包含起点和终点坐标，用抓取到的gps更新行程信息
     *
     * @param res
     * @param resp
     */
    private void setTripGpsInfo(List<ParsedRecord> res, GoloResp resp) {
        TripRecordItem tri = resp.getTrip();
        Double longitudeStart = tri.getLongitude_start(); // 开始经度
        Double longitudeEnd = tri.getLongitude_end(); // 结束经度
        // Double latitudeStart = tri.getLatitudeStart(); //开始纬度
        // Double latitudeEnd = tri.getLatitudeEnd(); //结束纬度

        // 经纬度成对出现，判断单个经度就可以；如果 gps json只有经度无纬度也认为gps无效；替换成新的gps信息
        // 存在有行程没有gps的情况：本身行程也无gps （情形：在大型地下车库出发，停车缴费等）
        if (StringUtil.isEmpty(longitudeStart)) {// 补开始 gps信息 --list(0)的第一gps值 （依赖于 json 中gps 顺序排列）
            if (res.size() > 0) {
                Double latitudeStartTemp = (Double) res.get(0).getRecord().get("latitude");
                Double longitudeStartTemp = (Double) res.get(0).getRecord().get("longitude");
                tri.setLatitude_start(latitudeStartTemp);
                tri.setLongitude_start(longitudeStartTemp);
            }
        } else if (StringUtil.isEmpty(longitudeEnd)) {// 补结束gps信息 --- list(size)的最后gps值 （依赖于 json 中gps 顺序排列）
            if (res.size() > 0) {
                Double latitudeEndTemp = (Double) res.get(res.size() - 1).getRecord().get("latitude");
                Double longitudeEndTemp = (Double) res.get(res.size() - 1).getRecord().get("longitude");
                tri.setLatitude_end(latitudeEndTemp);
                tri.setLongitude_end(longitudeEndTemp);
            }
        } else if (StringUtil.isEmpty(longitudeStart)
                && StringUtil.isEmpty(longitudeEnd)) // 补开始和结束gps信息
        {
            if (res.size() > 0) {
                Double latitudeStartTemp = (Double) res.get(0).getRecord().get("latitude");
                Double longitudeStartTemp = (Double) res.get(0).getRecord().get("longitude");
                tri.setLatitude_start(latitudeStartTemp);
                tri.setLongitude_start(longitudeStartTemp);

                Double latitudeEndTemp = (Double) res.get(res.size() - 1).getRecord().get("latitude");
                Double longitudeEndTemp = (Double) res.get(res.size() - 1).getRecord().get("longitude");
                tri.setLatitude_end(latitudeEndTemp);
                tri.setLongitude_end(longitudeEndTemp);
            }

        }
    }

    private ParsedRecord readRecord(GoloResp resp, JSONObject js) throws Exception {
        ParsedRecord record = new ParsedRecord();
        record.setType("yz_gps");
        Map<String, Object> map = record.getRecord();
        map.put("gps_time", DateUtil.parseGoloDate(js.getString("GPSTimeInDefaultTimeZone")));
        map.put("latitude", js.getDoubleValue("Latitude"));
        map.put("longitude", js.getDoubleValue("Longitude"));
        map.put("height", js.getDoubleValue("Height"));
        map.put("speed", js.getDoubleValue("Speed"));
        map.put("dir", js.getDoubleValue("Direction"));
        map.put("create_time", new Timestamp(System.currentTimeMillis()));
        map.put("accuracy", js.getDoubleValue("accuracy"));
        map.put("gps_locate_model", 2);//  元征百度坐标
        map.put("device_uid", resp.getDevice().getDeviceUid());

        return record;
    }
}
