package com.chh.dc.icp.util;

import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * golo参数工具类
 *
 * @author
 */
public class GoloUtils {

    private static final Logger LOG = Logger.getLogger(GoloUtils.class);

    private static final ResourceBundle bundle = java.util.ResourceBundle.getBundle("golo-config");

    public static final String appId = bundle.getString("app_id");
    /**
     *
     */
    public static final String developId = bundle.getString("develop_id");

    public static final String developKey = bundle.getString("develop_key");

    public static final String baseUrl = bundle.getString("base_url");

    public static String accessId;

    public static String accessToken;
    /**
     * 请求成功
     */
    public static final int ERR_CODE_SUCCESS = 0;
    /**
     * 序列号不存在分组中
     */
    public static final int ERR_CODE_ILLEGAL_SN = 140008;

    /**
     * 数据类型：1.实时数据
     */
    public static final int DATA_TYPE_DFDATA_STREAM = 1;
    /**
     * 数据类型：2.GPS数据
     */
    public static final int DATA_TYPE_GPS = 2;
    /**
     * 数据类型：3.故障码
     */
    public static final int DATA_TYPE_TROUBLE_CODE = 3;
    /**
     * 数据类型：4.体检报告
     */
    public static final int DATA_TYPE_MEDICAL_REPORT = 4;
    /**
     * 数据类型：行程
     */
    public static final int DATA_TYPE_TRIP = 5;

//    /**
//     * 序列号已经激活
//     * serialNo have been registered by others
//     */
//    public static final int ERR_CODE_SN_ACTIVATED = -2;

//    /**
//     * 激活action
//     */
//    public static final String registerAction = bundle.getString("register_action");


    public static String generateSign(String params) {
        return Encrypt.e(params + developKey);
    }

//    /**
//     * 注册元征账号
//     * @throws Exception
//     */
//	public static void regUser()  {
//		try {
//			StringBuffer params = new StringBuffer("action=develop.reg_user");
//			params.append("&app_id=2013120200000002");
//			params.append("&develop_id=").append(GoloUtils.developId);
//			params.append("&time=").append(Long.toString(System.currentTimeMillis()/1000));
//			String signF = GoloUtils.generateSign(params.toString());
//			params.append("&sign=").append(signF);
////			MyHttpClient client = new MyHttpClient();
//			JSONObject res = (JSONObject) MyHttpClient.getInstance().getJsonRequest(GoloUtils.baseUrl+params.toString());
//			if(GoloUtils.ERR_CODE_SUCCESS==res.getInteger("code")){
//				JSONObject data = res.getJSONObject("data");
//				accessId = data.getString("access_id");
//				accessToken = data.getString("access_token");
//				LOG.info("注册golo用户成功！");
//			} else {
//				LOG.error("注册golo用户失败！！！！！,错误消息："+res.toJSONString());
////				throw new Exception("注册golo用户失败！！！！！,错误消息："+res.toJSONString());
//			}
//		} catch(Exception e){
//			LOG.error("注册golo用户失败！！！！！",e);
////			throw e;
//		}
//	}

    public static void getDeviceInfo() {
        try {
            StringBuffer params = new StringBuffer("action=data_develop.get_devices_info");
            params.append("&app_id=").append(GoloUtils.appId);
            params.append("&develop_id=").append(GoloUtils.developId);
            params.append("&devicesn=").append("972290015404");
            params.append("&devicetype=golo3CU");
            params.append("&time=").append(Long.toString(System.currentTimeMillis() / 1000));
            String signF = GoloUtils.generateSign(params.toString());
            params.append("&sign=").append(signF);
//			MyHttpClient client = new MyHttpClient();
            JSONObject res = (JSONObject) MyHttpClient.getInstance().getJsonRequest(GoloUtils.baseUrl + params.toString());
            if (GoloUtils.ERR_CODE_SUCCESS == res.getInteger("code")) {
//				JSONObject data = res.getJSONObject("data");
                JSONArray data = res.getJSONArray("data");
            } else {
                LOG.error("获取设备信息失败！！！！！,错误消息：" + res.toJSONString());
            }
        } catch (Exception e) {
            LOG.error("获取设备信息失败！！！！！");
        }
    }


//	public static void main(String[] args){
//		getDeviceInfo();
//	}
}
