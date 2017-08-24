package com.chh.dc.icp.accessor.model;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class GoloCurDfdataResp extends GoloResp {

    private CurDfdata data;


    public JSONObject getData() {
        return (JSONObject) JSON.toJSON(data);
    }

    public void setData(String data) {
        JSONObject dj = JSONObject.parseObject(data);
//		String DFJsonContent = (String) data.get("DFJsonContent");
        List<GoloDfItem> df = new ArrayList();
        String DFJsonContent = (String) dj.get("DFJsonContent");
        if (DFJsonContent != null) {
            df = JSONObject.parseArray(DFJsonContent, GoloDfItem.class);
        }
//		System.out.println(df==null);
//		this.data = data;
//		this.data = JSONObject.toJavaObject(data, CurDfdata.class);
        this.data = new CurDfdata();
        this.data.setDFJsonContent(df);
    }


    public static class CurDfdata {

        private String CarSeriesCode;

        private List<GoloDfItem> DFJsonContent;
        //TODO 其他数据

        public List<GoloDfItem> getDFJsonContent() {
            return DFJsonContent;
        }

        public void setDFJsonContent(List<GoloDfItem> dFJsonContent) {
            DFJsonContent = dFJsonContent;
//			DFJsonContent = JSON.parseArray(dFJsonContent, GoloDfItem.class);

        }

        public String getCarSeriesCode() {
            return CarSeriesCode;
        }

        public void setCarSeriesCode(String carSeriesCode) {
            CarSeriesCode = carSeriesCode;
        }

    }

    /**
     * 元征实时数据流数据项
     */
    public static class GoloDfItem {

        public static final String TYPE_Door_Front_left = "00000180";//前左门的状态
        public static final String TYPE_Door_Front_right = "00000188";//前右门的状态
        public static final String TYPE_Door_Back_left = "00000190";//后左门的状态
        public static final String TYPE_Door_Back_right = "00000198";//后右门的状态
        public static final String TYPE_Door_Trunk = "000001E0";//后备箱\尾箱门的状态
        public static final String TYPE_Lock_Full = "0000050C";//门锁（全车锁）状态
        public static final String TYPE_Lock_Front_Left = "00000181";//门锁（左前门）状态
        public static final String TYPE_Lock_Front_Right = "00000189";//门锁（右前门）状态
        public static final String TYPE_Lock_Back_Left = "00000191";//门锁（左后门）状态
        public static final String TYPE_Lock_Back_Right = "00000199";//门锁（右后门）状态
        //		public static final int TYPE_ = 0x0000b;//前左轮的胎压
//		public static final int TYPE_ = 0x0000c;//前右轮的胎压       
//		public static final int TYPE_ = 0x0000d;//后左轮的胎压       
//		public static final int TYPE_ = 0x0000e;//后右轮的胎压       
        public static final String TYPE_Win_Front_left = "000001B0";//左前窗状态
        public static final String TYPE_Win_Front_right = "000001B8";//右前窗状态
        public static final String TYPE_Win_Back_left = "000001C0";//左后窗状态
        public static final String TYPE_Win_Back_right = "000001C8";//右后窗状态
        public static final String TYPE_Win_Dormer = "000001D8";//天窗状态
        public static final String TYPE_LED_Beam = "00000002";//灯状态（近光灯）
        public static final String TYPE_LED_Width = "00000008";//灯状态（示宽灯）
        public static final String TYPE_LED_Fog = "000002A5";//灯状态（雾灯）
        public static final String TYPE_LED_Left_turn = "00000509";//灯状态（左转向）
        public static final String TYPE_LED_Right_turn = "0000050A";//灯状态（右转向）
        public static final String TYPE_LED_Hazard = "0000050B";//灯状态（危险灯）
        //		public static final int TYPE_ = 0x0001a;//发动机舱盖状态
        public static final String TYPE_Water_temp = "00000305";//水温
        public static final String TYPE_Mileage_total = "00000290";//里程（总）
        public static final String TYPE_Mileage_endurance = "00000511";//里程（续航）
        public static final String TYPE_Mileage_trip = "0000051A";//累计里程
        public static final String TYPE_ACC = "0000050E";//ACC信号
        public static final String TYPE_Consumption_average = "0000040F";//油耗（平均）
        public static final String TYPE_Consumption_transient = "00000514";//油耗（瞬时）
        public static final String TYPE_Oil_remain_absolute = "0000040C";    //滤波后剩余油量	L
        public static final String TYPE_Oil_remain_scale = "0000040D";    //滤波后剩余油量	%
        public static final String TYPE_battery_voltage = "000001F0"; //电池当前电压  单位V


        private String DFDataStreamID;

        private String DFDataStreamValue;

        public String getDFDataStreamID() {
            return DFDataStreamID;
        }

        public void setDFDataStreamID(String dFDataStreamID) {
            DFDataStreamID = dFDataStreamID;
        }

        public String getDFDataStreamValue() {
            return DFDataStreamValue;
        }

        public void setDFDataStreamValue(String dFDataStreamValue) {
            DFDataStreamValue = dFDataStreamValue;
        }

    }
}
