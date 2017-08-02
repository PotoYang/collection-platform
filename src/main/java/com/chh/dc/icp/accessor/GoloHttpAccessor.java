package com.chh.dc.icp.accessor;

import com.chh.dc.icp.accessor.model.GoloResp;
import com.chh.dc.icp.accessor.model.GoloTripRecordByPage;
import com.chh.dc.icp.db.dao.DeviceDAO;
import com.chh.dc.icp.db.pojo.TDevice;
import com.chh.dc.icp.util.DateUtil;
import com.chh.dc.icp.util.GoloUtils;
import com.chh.dc.icp.util.MyHttpClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

public class GoloHttpAccessor extends AbstractAccessor {

    public static final Logger log = LoggerFactory.getLogger(GoloHttpAccessor.class);

    private DeviceDAO deviceDAO;

    private List<TDevice> deviceList = null;

    private int index = 0;

	@Override
    public boolean access() {
        //获取deviceList
        try {
//            JSONObject args = JSONObject.parseObject(taskInfo.getExtrasArgs());
//            begin = args.getIntValue("begin");
//            end = args.getIntValue("end");
            //1.从扩展表t_device_extends_yz查询元征1000设备信息，采集范围通过任务扩展字段begin、end限制表字段seq_number筛选；   
            //任务类型为4的话 ，查询 元征扩展表 （需保证适应之前 航天无线的采集）
//            if(4==taskInfo.getTaskType()){
            	deviceList = deviceDAO.getCnlaunchDeviceList(taskInfo);
//            }else{
//            	deviceList = deviceDAO.getDeviceList(taskInfo);
//            }
            return true;
        } catch (Exception e) {
            log.error("采集元征http接口数据Accessor访问失败，任务ID[" + taskInfo.getId() + "]", e);
        }
        return false;
    }

    @Override
    public DataPackage getData() {
        DataPackage data = null;
        TDevice device = null;
        //因为有可能跨天，所以会有多条数据
//        List<GoloResp> res = null;
        //改成先进后出的栈存储，解析的时候先解析GPS、故障码、体检报告，再解析行程
        Stack<GoloResp> res = null;
        //迭代deviceList
        //生成URL
        //return 采集URL=》data
        while (true) {
            if (deviceList != null && deviceList.size() > index) {//index + 1
                try {
                    device = deviceList.get(index++);
                    res = getGoloData(device);
                    break;
                } catch (Exception e) {
                    log.error("采集元征http接口数据失败，任务ID[" + taskInfo.getId() + "]，设备ID[" + device.getDeviceUid() + "]", e);
                }
            } else {
                break;
            }
        }
        if (res != null) {
            data = new DataPackage();
            data.setData(res);
        }
        return data;
    }

    private Stack<GoloResp> getGoloData(TDevice device) throws Exception {
        try {
            Stack<GoloResp> res = new  Stack<GoloResp>();
            Date lastTripEndTime = null;
            //1.抓取从device.getLastTripEndTime()到当前时间的该设备的行程数据
            Date startTime =device.getLastTripEndTime() ;  //开始时间   ： device中的    last_trip_end_time
            Date endTime = new Timestamp(System.currentTimeMillis()); //结束时间   ：当前时间
            //(注意：元征获取行程数据接口时间段不能跨月，如果跨月要拆分多次查询),注意区分vender_device_uid、device_uid
            int monthInterval = DateUtil.getMonthInterval(startTime, endTime);
            if(monthInterval>0){//跨月
                Date mStartTime = new Date(startTime.getTime());
                Date mEndTime = DateUtil.getCurMonLastTime(mStartTime);
                for(;monthInterval>=0;monthInterval--){
                    Date temp = getDataInMonthTime(device,res, mStartTime, mEndTime);
                    if (temp!=null) {
                        lastTripEndTime = temp;
                    }

                    //重置mStartTime和mEndTime
                    mStartTime = DateUtil.getNextMonFirstTime(mEndTime);
                    mEndTime = DateUtil.getCurMonLastTime(mStartTime);
                    if(mEndTime.after(endTime)){
                        mEndTime = endTime;
                    }
                }
            } else {//不跨月
                lastTripEndTime = getDataInMonthTime(device,res, startTime, endTime);
            }


            // .更新设备最后一次行程结束时间
            if (lastTripEndTime!=null) {
            	// 1(97*******) 和 4(97*******) 数据库表数据暂未统一 直接根据 sn 更新 行程最后时间

                deviceDAO.updateLastTripEndTime(device.getSn(),DateUtil.getNextSecondTime(lastTripEndTime));
            }

            return res;
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * 分月查询数据
     * 约定 行程 体检 故障码 gps 顺序入栈 （先进后出）
     * @param device
     * @param res
     * @param startTime
     * @param endTime
     * @return 最后一次行程的结束时间，如果没有行程返回null
     * @throws IOException
     * @throws Exception
     */
	private Date getDataInMonthTime(TDevice device, Stack<GoloResp> res,
                                Date startTime, Date endTime) throws IOException, Exception {
	    Date lastTripEndTime = null;
	    List<GoloTripRecordByPage> tripRespList = new ArrayList<>();
		//分页查询行程数据
        GoloTripRecordByPage goloResp = getTripRecordByPage(device, startTime, endTime,1);
        tripRespList.add(goloResp);
		// 项目结构上设计 [访问器]  GoloResp 入栈 ；之后 [解析器]解析数据  
		// 但是 元征开放平台接口中 行程和 体检报告 有分页 （需要提取解析 【判断是否需要分页再次请求】）
		GoloTripRecordByPage.TripRecordData rd = goloResp.getTripData();//进行分析
		if (rd != null && rd.getList() != null) {
			int page = rd.getCount() % GoloTripRecordByPage.PAGE_SIZE == 0 ? rd
					.getCount() / GoloTripRecordByPage.PAGE_SIZE
					: rd.getCount() / GoloTripRecordByPage.PAGE_SIZE + 1;
			if (page > 1) {
				for (int curPage = 2; curPage <= page; curPage++) {
					//如果 抽取的时间段中，还有分页 [（按照用户习惯 每天用车4次，4个行程，假设获取周期最长30天  120/10 最多 new 12个GoloResp对象）]
                    GoloTripRecordByPage goloRespPage = getTripRecordByPage(device, startTime, endTime,curPage);
                    tripRespList.add(goloRespPage);
				}
			}
		}


        Date tripStartTime = null;
        Date tripEndTime = null;
        for (GoloTripRecordByPage tripResp : tripRespList) {
            if (tripResp!=null&&tripResp.getTripData()!=null&&tripResp.getTripData().getList()!=null) {//有行程
                //1. 行程先入栈
                res.push(tripResp);
                //2.按行程查询行程相关数据
                //查询行程统计数据
                //抓取每段行程的GPS、故障码、体检报告并压入栈res
                for (GoloTripRecordByPage.TripRecordItem tripItem:tripResp.getTripData().getList()) {//遍历每段行程

                    tripStartTime = tripItem.getTripStartTime();
                    tripEndTime = tripItem.getTripEndTime();
                    if (lastTripEndTime==null||lastTripEndTime.getTime()<tripEndTime.getTime()) {
                        lastTripEndTime = tripEndTime;
                    }

                    processTrip(res,device,tripItem,tripStartTime,tripEndTime);

                }
            }
        }

		
		return lastTripEndTime;
	}


	private void processTrip(Stack<GoloResp> res, TDevice device, GoloTripRecordByPage.TripRecordItem tripItem, Date tripStartTime, Date tripEndTime) throws Exception {
        //建议在汇总里面 统一处理
        //获取行程汇总信息，绑定到行程TripRecordItem
        Map<Integer, Integer> tripWarningNum=deviceDAO.getTripWarningNum(device.getVender_device_uid(),tripStartTime,tripEndTime);
        //*5.从DB抓取t_device_warning中device_uid当前行程时间段的告警数据，分类汇总次数，写入trip.putExtrasData()，包括：ra_count, ad_count, st_count, door_unclosed_cnt, idle_cnt, temp_barrier_cnt, overspeed_cnt
        putValueForWarningNum(tripItem,tripWarningNum,66,"ra_count");//急加速次数
        putValueForWarningNum(tripItem,tripWarningNum,67,"ad_count");//急减速次数
        putValueForWarningNum(tripItem,tripWarningNum,21,"st_count");//急转弯次数

        putValueForWarningNum(tripItem,tripWarningNum,60,"door_unclosed_cnt");  //车门异常次数
        putValueForWarningNum(tripItem,tripWarningNum,22,"idle_cnt"); //怠速时间过长次数
        putValueForWarningNum(tripItem,tripWarningNum,2,"temp_barrier_cnt"); //水温异常次数
        putValueForWarningNum(tripItem,tripWarningNum,100,"fault_cnt"); 	//故障告警次数
        putValueForWarningNum(tripItem,tripWarningNum,27,"overspeed_cnt"); 	//超速告警次数
        putValueForWarningNum(tripItem,tripWarningNum,8,"over_revs_cnt"); 	//转速过高次数
        putValueForWarningNum(tripItem,tripWarningNum,11,"overdo_cnt"); 	//疲劳驾驶次数

        long dayInterval = DateUtil.daysBetween(tripStartTime, tripEndTime);//元征实时数据、GPS、故障码、体检报告不能跨天抓取数据
        if (dayInterval > 0) {
            Date mStartTime = tripStartTime;
            Date mEndTime = DateUtil.getCurDayLastTime(mStartTime);
            for (; dayInterval > 0; dayInterval--) {

                //6.调用采集GPS/故障码/体检报告接口
                res.push(getGps( device,tripItem, mStartTime, mEndTime));
                res.push(getTroubleCode(device,tripItem,mStartTime, mEndTime));

                //体检报告
                res.push(getMedicalReport(device, tripItem, mStartTime, mEndTime));

                //重置mStartTime和mEndTime
                mStartTime = DateUtil.getNextDayFirstTime(mEndTime);
                mEndTime = DateUtil.getCurDayLastTime(mStartTime);
                if(mEndTime.after(tripEndTime)){
                    mEndTime = tripEndTime;
                }
            }
        } else {
            //6.调用采集GPS/故障码/体检报告接口
            res.push(getGps( device,tripItem, tripStartTime, tripEndTime));
            res.push(getTroubleCode(device,tripItem,tripStartTime, tripEndTime));
            //体检报告
            res.push(getMedicalReport(device, tripItem, tripStartTime, tripEndTime));
        }
    }

    private GoloResp getTroubleCode(TDevice device, GoloTripRecordByPage.TripRecordItem tripItem, Date startTime, Date endTime) throws IOException, Exception {
        GoloResp resp_gps = MyHttpClient.getInstance().getJsonRequest(genUrl(device, GoloUtils.DATA_TYPE_TROUBLE_CODE, startTime, endTime,null), GoloResp.class);
        if(resp_gps != null){
            resp_gps.setType(GoloUtils.DATA_TYPE_TROUBLE_CODE);
            resp_gps.setDevice(device);
            resp_gps.setTrip(tripItem);
        }
        return resp_gps;
    }


    private String genUrl(TDevice device, int type, Date startTime, Date endTime,Integer curPage) throws Exception {
        StringBuffer params = null;
        switch (type) {
            case GoloUtils.DATA_TYPE_DFDATA_STREAM: { // 实时数据
                params = new StringBuffer("action=data_develop.get_dfdata_stream_info");
                params.append("&date=").append(DateUtil.toDateString(startTime));
                params.append("&develop_id=").append(GoloUtils.developId);
                params.append("&deviceuid=").append(device.getVender_device_uid());
                params.append("&endtime=").append(DateUtil.toTimeString(endTime));
                params.append("&starttime=").append(DateUtil.toTimeString(startTime));
                params.append("&time=").append(Long.toString(System.currentTimeMillis() / 1000));
                String signF = GoloUtils.generateSign(params.toString());
                params.append("&sign=").append(signF);
                break;
            }
            case GoloUtils.DATA_TYPE_GPS: { //gps 数据
                params = new StringBuffer("action=data_develop.get_gps_info");
                params.append("&date=").append(DateUtil.toDateString(startTime));
                params.append("&develop_id=").append(GoloUtils.developId);
                params.append("&deviceuid=").append(device.getVender_device_uid());
                params.append("&endtime=").append(DateUtil.toTimeString(endTime));
                params.append("&starttime=").append(DateUtil.toTimeString(startTime));
                params.append("&time=").append(Long.toString(System.currentTimeMillis() / 1000));
                String signF = GoloUtils.generateSign(params.toString());
                params.append("&sign=").append(signF);
                break;
            }
            case GoloUtils.DATA_TYPE_TROUBLE_CODE: { //故障码 数据
                params = new StringBuffer("action=data_develop.get_trouble_code_info");
                params.append("&date=").append(DateUtil.toDateString(startTime));
                params.append("&develop_id=").append(GoloUtils.developId);
                params.append("&deviceuid=").append(device.getVender_device_uid());
                params.append("&endtime=").append(DateUtil.toTimeString(endTime));
                params.append("&starttime=").append(DateUtil.toTimeString(startTime));
                params.append("&time=").append(Long.toString(System.currentTimeMillis() / 1000));
                String signF = GoloUtils.generateSign(params.toString());
                params.append("&sign=").append(signF);
                break;
            }
			case GoloUtils.DATA_TYPE_MEDICAL_REPORT:{
				params = new StringBuffer("action=vehicle_medical_report_service.get_medical_reports_by_page");
				params.append("&app_id=").append(GoloUtils.appId);
				params.append("&develop_id=").append(GoloUtils.developId);
				params.append("&devicesn=").append(device.getSn());
				params.append("&end_date=").append(DateUtil.getDateTimeString(endTime));
				params.append("&pagesize=10");
				params.append("&start_date=").append(DateUtil.getDateTimeString(startTime));
				params.append("&targetpage=1");
				params.append("&time=").append(Long.toString(System.currentTimeMillis() / 1000));
				String signF = GoloUtils.generateSign(params.toString());
				params.append("&sign=").append(signF);
				break;
			}
    		case GoloUtils.DATA_TYPE_TRIP:{ //行程数据
        		params = new StringBuffer("action=data_develop.get_trip_record_by_page");
        		params.append("&develop_id=").append(GoloUtils.developId);
        		params.append("&deviceuid=").append(device.getVender_device_uid());
        		params.append("&endtime=").append(DateUtil.getDateTimeString(endTime));
        		params.append("&pagesize=").append(GoloTripRecordByPage.PAGE_SIZE);
        		params.append("&starttime=").append(DateUtil.getDateTimeString(startTime));
        		params.append("&targetpage=").append(curPage);
        		params.append("&time=").append(Long.toString(System.currentTimeMillis() / 1000));
        		 String signF = GoloUtils.generateSign(params.toString());
        		params.append("&sign=").append(signF);
    			break;
    		}
            default: {
                throw new Exception("未支持的的请求类型");
            }
        }

        return params.insert(0, GoloUtils.baseUrl).toString().replaceAll("\\s", "%20");
    }
    /**
     * 获取体检报告
     * @param device	
     * @param trip
     * @param startTime
     * @param endTime
     * @return
     */
    public GoloResp getMedicalReport(TDevice device,GoloTripRecordByPage.TripRecordItem trip,
    							Date startTime,Date endTime){
    	try {
    		//测试数据
//    		if(startTime == null){
//    			startTime = DateUtil.getDate1("2016-10-06 10:37:56");
//    		}
//    		if(endTime == null){
//    			endTime = DateUtil.getDate1("2016-10-06 10:37:56");
//    		}
    		GoloResp resp = MyHttpClient.getInstance().getJsonRequest(genUrl(device,GoloUtils.DATA_TYPE_MEDICAL_REPORT, startTime, endTime,null), GoloResp.class);
    		if(resp != null){
                resp.setType(GoloUtils.DATA_TYPE_MEDICAL_REPORT);
                resp.setTrip(trip);
                resp.setDevice(device);
            }
    		return resp;
		} catch (Exception e) {
			log.error("获取体检报告接口错误", e);
		}
		return null;
    }
    
	/**
	 * 获取gps 数据
	 * @param device
	 * @param startTime
	 * @param endTime
	 * @throws IOException
	 * @throws Exception
	 */
	private GoloResp getGps(TDevice device, GoloTripRecordByPage.TripRecordItem trip, Date startTime,
                            Date endTime) throws IOException, Exception {
        		GoloResp resp_gps = MyHttpClient.getInstance().getJsonRequest(genUrl(device,GoloUtils.DATA_TYPE_GPS, startTime, endTime,null), GoloResp.class);
        		if(resp_gps != null){
        		resp_gps.setType(GoloUtils.DATA_TYPE_GPS);
        		resp_gps.setDevice(device);
        		resp_gps.setTrip(trip);
        		}
            return resp_gps;
	}

	/**
	 * 获取行程数据，并统计报警数量
	 * @param device
	 * @param startTime
	 * @param endTime
	 * @param curPage
	 * @return
	 * @throws Exception
	 */
	public  GoloTripRecordByPage getTripRecordByPage(TDevice device, Date startTime,
			Date endTime, int curPage) throws Exception {
		try {
			
			GoloTripRecordByPage goloResp = MyHttpClient.getInstance()
					.getJsonRequest(
							genUrl(device,GoloUtils.DATA_TYPE_TRIP, startTime, endTime,curPage),
							GoloTripRecordByPage.class);

			switch (goloResp.getCode()) {
			case GoloUtils.ERR_CODE_SUCCESS: {
				//*3.每次请求元征获取行程数据接口，返回GoloResp，对象关联TDevice、type（数据类型：GoloUtils.DATA_TYPE_TRIP）
				goloResp.setType(GoloUtils.DATA_TYPE_TRIP);//关联 类型
				goloResp.setDevice(device);//关联 device
				return goloResp;
			}
			default: {
				throw new Exception("获取设备里程信息失败!!!设备sn：" + device.getSn()
						+ ",接口返回错误code：" + goloResp.getCode() + ",msg:"
						+ goloResp.getMsg());
			}
			}
		} catch (Exception e) {
			log.error("获取设备里程信息失败！！！！", e);
			throw new Exception("获取设备里程信息失败！！！！设备sn："+device.getSn(),e);
		}
	}
	
	/**
	 * 封装 行程中报警次数
	 * @param tripItem
	 * @param tripWarningNum
	 * @param warningType
	 * @param warningName
	 */
	private  void putValueForWarningNum(GoloTripRecordByPage.TripRecordItem tripItem,
			Map<Integer, Integer> tripWarningNum,int warningType,String warningName) {
		int value = tripWarningNum.get(warningType) == null ? 0 : tripWarningNum.get(warningType);
        tripItem.putExtrasData(warningName, value);
	}
	

    @Override
    public boolean stop() {
        return false;
    }

    public DeviceDAO getDeviceDAO() {
        return deviceDAO;
    }

    public void setDeviceDAO(DeviceDAO deviceDAO) {
        this.deviceDAO = deviceDAO;
    }

/*	public static void main(String[] args) throws Exception{
		TDevice device = new  TDevice();
		Date startTime = DateTimeUtils.parseDate("2016-07-10 22:20:00");
		Date endTime = DateTimeUtils.parseDate("2016-07-10 22:50:00");
		int curPage = 1;
		device.setSn("972290025176");
		device.setVender_device_uid("F4AF0D53-AAA6-13C9-D035-1D35E4289709");
		//getTripRecordByPage 改成静态进行验证
		//getTripRecordByPage(device,startTime,endTime,curPage,new GoloResp());
	}*/
}
