package com.chh.dc.icp.parser.obd.reader.goloopen;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.chh.dc.icp.Runner;
import com.chh.dc.icp.util.redis.RedisCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.chh.dc.icp.accessor.model.GoloResp;
import com.chh.dc.icp.accessor.model.GoloTripRecordByPage;
import com.chh.dc.icp.db.pojo.TDevice;
import com.chh.dc.icp.parser.obd.reader.GoloObjectReader;
import com.chh.dc.icp.warehouse.ParsedRecord;

/**
 * golo open 行程数据
 * @author yansi.li
 *
 */
public class GoloTripDataReader  implements GoloObjectReader  {
	
	private static final Logger log = LoggerFactory.getLogger(GoloTripDataReader.class);
	
	private RedisCache redisCache;
	
	public GoloTripDataReader(){
		redisCache = Runner.getBean("redisCache", RedisCache.class);
	}
	
	@Override
	public  List<ParsedRecord> readRecords(GoloResp resp) throws Exception {
		List<ParsedRecord> res = null;
		GoloTripRecordByPage tripResp = (GoloTripRecordByPage)resp;
		if (tripResp!=null&&tripResp.getTripData()!=null&&tripResp.getTripData().getList()!=null){
			res = new ArrayList<ParsedRecord>();
			String tripId = null;
			ParsedRecord tripRecord = null;
			ParsedRecord warningData = null;
			ParsedRecord alarmData = null;
			for (GoloTripRecordByPage.TripRecordItem trip : tripResp.getTripData().getList()) {//遍历每段行程

				tripId = UUID.randomUUID().toString();//行程id
				tripRecord = new ParsedRecord("yz_trip");
				tripRecord.putData("id",tripId);
				tripRecord.putData("device_uid",resp.getDevice().getDeviceUid());
				tripRecord.putData("trip_mileage",trip.getTripMileage());
				tripRecord.putData("fuel_consumption",trip.getFuelConsumption());
				tripRecord.putData("start_time",trip.getTripStartTime());
				tripRecord.putData("end_time",trip.getTripEndTime());
				tripRecord.putData("create_time",new Date());
				tripRecord.putData("gps_locate_model",2);//元征平台GPS使用的是百度地图坐标
				tripRecord.putData("start_longitude",trip.getLongitude_start());
				tripRecord.putData("start_latitude",trip.getLatitude_start());
				tripRecord.putData("end_longitude",trip.getLongitude_end());
				tripRecord.putData("end_latitude",trip.getLatitude_end());
				tripRecord.putData("update_time",new Date());
				//统计字段
				tripRecord.putData("ra_count",trip.getExtrasData("ra_count"));
				tripRecord.putData("ad_count",trip.getExtrasData("ad_count"));
				tripRecord.putData("st_count",trip.getExtrasData("st_count"));
				tripRecord.putData("door_unclosed_cnt",trip.getExtrasData("door_unclosed_cnt"));
				tripRecord.putData("idle_cnt",trip.getExtrasData("idle_cnt"));
				tripRecord.putData("temp_barrier_cnt",trip.getExtrasData("temp_barrier_cnt"));
				tripRecord.putData("fault_cnt",trip.getExtrasData("fault_cnt"));
				tripRecord.putData("overspeed_cnt",trip.getExtrasData("overspeed_cnt"));

				tripRecord.putData("over_revs_cnt",trip.getExtrasData("over_revs_cnt"));//转速过高次数
				tripRecord.putData("overdo_cnt",trip.getExtrasData("overdo_cnt"));//疲劳驾驶次数
				
				//计算行程得分
				getTripScore(tripRecord);
				
				res.add(tripRecord);

				// 每段行程 对应 推送一个行程结束告警
				//5.触发行程结束告警，生成ParsedRecord，输出类型为：yz_alarm	*/
				//添加警告类型
				alarmData = new ParsedRecord("yz_alarm");
//			Map<String, Object> alarmMap = alarmData.getRecord();
				alarmData.putData("id", UUID.randomUUID().toString());
				alarmData.putData("device_uid", resp.getDevice().getDeviceUid());
				alarmData.putData("warning_time",  new Date());
				alarmData.putData("warning_type", 200);
				alarmData.putData("warning_value", tripId);
				alarmData.putData("warning_desc","行程结束 " );
				alarmData.putData("create_time", new Date());
				res.add(alarmData);

			}

			// 盒子 失联 针对的是 设备，并非每个行程 如果是每个行程 对应这边 后期获取的方式，告警早已失效 因此在for 循环外推送一个即可
			//4.如果盒子状态为失联=》触发18取消失联告警，生成对应的输出类型为htwx_warning_to_stat的待汇总告警数据ParsedRecord
			if (tripResp.getDevice().getStatus()== TDevice.STATUS_MISSING) {
				warningData = new ParsedRecord("yz_warning_to_stat");
//				Map<String, Object> warningMap = warningData.getRecord();
				warningData.putData("id", UUID.randomUUID().toString());
				warningData.putData("device_uid", resp.getDevice().getDeviceUid());
				warningData.putData("warning_time",  new Date());
				warningData.putData("warning_type", 18);
				warningData.putData("warning_value", System.currentTimeMillis());
				warningData.putData("warning_desc","取消失联告警" );
				res.add(warningData);
				tripResp.getDevice().setStatus(TDevice.STATUS_ONLINE);//设置为在线后，后续的GoloTripRecordByPage不会触发取消失联告警
			}

		}
		return res;
	}

	/**
	 * 计算行程得分
	 * @param tripRecord
	 * @return
	 */
	private void getTripScore(ParsedRecord tripRecord) {
		//todo 设置redis缓存中device_uid对应的socre,获取device_uid在有序队列中排名和队列总长度
		
		String deviceUid = (String) tripRecord.getData("device_uid");
		double mileage = (double) tripRecord.getData("trip_mileage");//里程，单位km
		int raCount = (int) tripRecord.getData("ra_count");
		int adCount = (int) tripRecord.getData("ad_count");
		int stCount = (int) tripRecord.getData("st_count");
		
		//总分(疲劳驾驶、发动机高转速不计算，默认满分)
		Double totalScore = 28d + getScore(raCount, mileage) 
								+ getScore(adCount, mileage) 
								+ getScore(stCount, mileage);
		//将当前设备得分放入排行缓存
		redisCache.zadd("htwx_trip_ranking", totalScore, deviceUid);
		//获取当前设备在所有设备中的排名
        Long rank = redisCache.zrevrank("htwx_trip_ranking", deviceUid);
        int rank_cnt = rank == null ? 0 : rank.intValue() + 1;
        //获取所有排行总数
        Long rankTotal = redisCache.zcount("htwx_trip_ranking", 0d, 100d);
        //行程排名按百分比计算
        double rank_per = rankTotal == 0 ? 0 : new BigDecimal(rank_cnt).divide(new BigDecimal(rankTotal),10,BigDecimal.ROUND_HALF_DOWN).doubleValue();
        tripRecord.putData("score", totalScore);
        tripRecord.putData("rank_cnt", rank_cnt);
        tripRecord.putData("rank_per", rank_per);
	}
	/**
     * 根据异常次数和里程计算得分
     * @param count
     * @param tripMileage
     * @return
     */
    private static Double getScore(int count,Double tripMileage){
    	Double rstScore = 0d;
    	Double totalScore = 24d;
    	try {
    		//异常次数为零、总里程为零 返回总分数
			if(count == 0 || tripMileage == null || tripMileage == 0)
				return totalScore;
			
			double rate = count / tripMileage;
        	if(rate == 0){
        		rstScore = totalScore;
        	}else if(rate > 0 && rate < 0.1){
        		rstScore = (1 - (rate / 0.1)) * 24;
        	}
		} catch (Exception e) {
			log.error("计算得分错误", e);
		}
    	return rstScore;
    }

}
