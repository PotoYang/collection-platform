package com.chh.dc.icp.parser.obd.reader.goloopen;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chh.dc.icp.db.pojo.PidThreshold;
import com.chh.dc.icp.accessor.model.GoloResp;
import com.chh.dc.icp.parser.obd.reader.GoloObjectReader;
import com.chh.dc.icp.util.OBDAlarmCodeConverter;
import com.chh.dc.icp.util.StringUtil;
import com.chh.dc.icp.warehouse.ParsedRecord;

public class GoloMedicalReportReader implements GoloObjectReader {


    @Override
    public List<ParsedRecord> readRecords(GoloResp resp) throws Exception {
        List<ParsedRecord> res = new ArrayList<ParsedRecord>();
        ParsedRecord rec = null;
        Object da = resp.getData();
        if (da instanceof JSONArray) {
            JSONArray ja = (JSONArray) da;
            for (Object o : ja) {
                rec = readRecord(resp, (JSONObject) o);
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
        return res;
    }

    private ParsedRecord readRecord(GoloResp resp, JSONObject js) throws Exception {
        ParsedRecord record = new ParsedRecord("yz_medical_report");

        int score = 100;
        String id = UUID.randomUUID().toString();
        String deviceUid = resp.getDevice().getDeviceUid();
        Date now = new Date();
        Date examinationTime = js.getDate("examination_time");
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("deviceUid", deviceUid);
        data.put("examinationTime", examinationTime);

        int dataFlowCount = 0;
        List<Map<String, Object>> newDataFlow = new ArrayList<>();
        //数据流
        JSONArray dataFlow = JSON.parseArray(js.getString("data_flow"));
        if (dataFlow != null) {
            for (Object o : dataFlow) {
                JSONObject df = (JSONObject) o;
                String pid = "0x" + df.getString("id");
                String value = df.getString("value");
                Map<String, Object> m = new HashMap<>();
                m.put("name", df.getString("name"));
                m.put("pid", pid);
                m.put("value", value);
                //判断正常或异常
                PidThreshold pt = OBDAlarmCodeConverter.getThresholdByPid(pid);
                boolean flag = false;
                if (pt != null && StringUtil.isNotNull(value)) {
                    if (flag = checkValue(value, pt)) {
                        score--;
                        dataFlowCount++;
                    }
                }
                m.put("abnormalFlag", flag);
                newDataFlow.add(m);
            }
        }
        data.put("dataFlow", newDataFlow);
        //故障码
        List<Map<String, Object>> newFaultCodes = new ArrayList<>();

//        List<Map<String, Object>> syss1 = new ArrayList<>();
//        Map<String, Object> m1 = new HashMap<>();
//        m1.put("sys", "Sistema rilevamento carburante e aria");
//        List<Map<String, Object>> faults1 = new ArrayList<>();
//        Map<String, Object> mf1 = new HashMap<>();
//        mf1.put("id", "00002187");
//        mf1.put("fault_description", "asd");
//        mf1.put("code", "P2187");
//        mf1.put("status", "Sempre");
//        faults1.add(mf1);
//        Map<String, Object> mf2 = new HashMap<>();
//        mf2.put("id", "00002187");
//        mf2.put("fault_description", "asd");
//        mf2.put("code", "P2187");
//        mf2.put("status", "Sempre");
//        faults1.add(mf2);
//        m1.put("faults", faults1);
//        syss1.add(m1);
//        syss1.add(m1);
//        Map<String, Object> sysMap = new HashMap<>();
//        sysMap.put("syss", syss1);
//        js.put("fault_codes", JSON.toJSONString(sysMap));
        int faultCount = 0;
        JSONObject faultCodes = JSON.parseObject(js.getString("fault_codes"));
        if (faultCodes != null) {
            JSONArray syss = faultCodes.getJSONArray("syss");
            if (syss != null) {
                for (Object o : syss) {
                    Map<String, Object> m = new HashMap<>();
                    JSONObject faultItem = (JSONObject) o;
                    m.put("sys", faultItem.getString("sys"));
                    List<Map<String, Object>> faults = new ArrayList<>();
                    JSONArray faultArray = faultItem.getJSONArray("faults");
                    for (Object f : faultArray) {
                        JSONObject fo = (JSONObject) f;
                        Map<String, Object> fm = new HashMap<>();
                        fm.put("dtcId", fo.getString("id"));
                        fm.put("dtcValue", fo.getString("code"));
                        fm.put("dtcDescription", fo.getString("fault_description"));
                        faults.add(fm);
                        faultCount++;
                    }
                    m.put("faults", faults);
                    newFaultCodes.add(m);
                }
            }
        }
        data.put("faultCodes", newFaultCodes);
        //体检评分规则：1个故障码扣8份，故障码至多扣32分
        score = faultCount >= 4 ? (score - 32) : (score - 8 * faultCount);
        //检查结论
        StringBuilder sb = new StringBuilder();
        if (score == 100) {
            sb.append("您的爱车未检测到故障码，车况良好，请继续保持");
        } else {
            score = score < 0 ? 0 : score;
            sb.append("您的爱车检测到");
            if (faultCount > 0) {
                sb.append(faultCount).append("个故障码");
            }
            if (dataFlowCount > 0) {
                if (faultCount > 0)
                    sb.append("/");
                sb.append(dataFlowCount).append("项数据异常");
            }
            sb.append("，建议至4S店或其他专业检测机构进行详细排查和养护");
        }
        data.put("score", score);
        data.put("conclusion", sb.toString());

        String reportContent = JSONObject.toJSONStringWithDateFormat(data, "yyyy-MM-dd HH:mm:ss");
        Map<String, Object> map = record.getRecord();
        map.put("id", UUID.randomUUID().toString());
        map.put("examination_time", examinationTime);
        map.put("device_uid", deviceUid);
        map.put("create_time", now);
        map.put("report_content", reportContent);
        return record;
    }

    /**
     * 验证值（正常/异常)
     *
     * @param value
     * @param field
     * @return boolean
     * @throws Exception
     */
    private static boolean checkValue(String value, PidThreshold pt) throws Exception {
        String reg = "-?\\d+(\\.\\d+)?";
        boolean flag = false;//true:异常	false:正常   默认正常
        if (StringUtil.isNotNull(value)) {
            //数值类型，判断阀值
            if (value.matches(reg)) {
                Double dValue = Double.parseDouble(value);
                if (pt.getMinValue() > dValue || dValue > pt.getMaxValue()) {
                    flag = true;
                }
            } else {
                //字符串值，例如 YES NO OFF等，默认都是正常
            }
//			if("YES".equals(value) || "NO".equals(value)){
//				
//			}else{
//				Double dValue = Double.parseDouble(value);
//				if(pt.getMinValue() > dValue || dValue > pt.getMaxValue()){
//					flag = true;
//				}
//			}
        }
        return flag;
    }
}
