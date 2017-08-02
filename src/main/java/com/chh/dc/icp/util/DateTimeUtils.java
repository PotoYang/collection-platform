package com.chh.dc.icp.util;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateTimeUtils {
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");    

	/**
	 * 获取当前时间
	 * 
	 * @return
	 */
	public static Date now() {
		return new Date(System.currentTimeMillis());
	}

	/**
	 * 获取当前时间
	 * 
	 * @return
	 */
	public static Date date() {
		Calendar date = Calendar.getInstance();
		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);
		return new Date(date.getTimeInMillis());
	}

	/**
	 * 获取年
	 * 
	 * @param date
	 * @return
	 */
	public static int getYear(java.util.Date date) {
		return getValue(date, Calendar.YEAR);
	}

	/**
	 * 获取月
	 * 
	 * @param date
	 * @return
	 */
	public static int getMonth(java.util.Date date) {
		return getValue(date, Calendar.MONTH);
	}

	/**
	 * 获取日
	 * 
	 * @param date
	 * @return
	 */
	public static int getDay(java.util.Date date) {
		return getValue(date, Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获取星期
	 * 
	 * @param date
	 * @return
	 */
	public static int getWeek(java.util.Date date) {
		return getValue(date, Calendar.DAY_OF_WEEK);
	}

	/**
	 * 获取小时
	 * 
	 * @param date
	 * @return
	 */
	public static int getHour(java.util.Date date) {
		return getValue(date, Calendar.HOUR_OF_DAY);
	}

	/**
	 * 获取分钟
	 * @param date
	 * @return
	 */
	public static int getMinute(java.util.Date date) {
		return getValue(date, Calendar.MINUTE);
	}

	/**
	 * 获取秒
	 * @param date
	 * @return
	 */
	public static int getSecond(java.util.Date date) {
		return getValue(date, Calendar.SECOND);
	}

	/**
	 * 获取时间的某个值(年、月、日、时、分、秒)
	 * @param date
	 * @param field
	 * @return
	 */
	private static int getValue(java.util.Date date, int field) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date.getTime());
		return calendar.get(field);
	}

	/**
	 * 字符串转换时间
	 * 解析时间,格式必须是:yyyy-MM-dd HH:mm:ss(不处理异常) 
	 * @param timeStr
	 * @return
	 * @throws BaseParseException
	 */
	public static Date parseDate(String timeStr) throws ParseException {
		return sdf.parse(timeStr);
	}
	
	/**
	 * 字符串转换时间
	 * 解析时间,格式必须是:HH:mm:ss(不处理异常) 
	 * @param timeStr
	 * @return
	 * @throws ParseException
	 */
	public static Date parseTime(String timeStr) throws ParseException {
		return new Date(new SimpleDateFormat("HH:mm:ss").parse(timeStr).getTime());
	}

	/**
	 * 字符串转换Date
	 * 解析时间,格式必须是:yyyy-MM-dd HH:mm:ss(处理异常，如有异常返回null) 
	 * @param timeStr
	 * @return
	 */
	public static Date getDateByTimeStr(String timeStr) {
		Date ts_time = null;
		try {
			ts_time = sdf.parse(timeStr);
		} catch (ParseException e) {
		}
		return ts_time;
	}

	/**
	 * 转换时间为字符串(格式为年月日十分秒) 
	 * @param Date
	 * @return
	 */
	public static String toDateTimeString(Date date) {
		if (date == null) {
			return null;
		}
		return sdf.format(date);
	}

	/**
	 * 转换日期为字符串 
	 * @param date
	 * @return
	 */
	public static String toDateString(Date date) {
		if (date == null) {
			return null;
		}
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}

	//获取时间时分秒转换字符串
	public static String toTimeString(Date date) {
		if (date == null) {
			return null;
		}
		return new SimpleDateFormat("HH:mm:ss").format(date);
	}
	
	/**
	 * 转换时间为mysql日期 
	 * @param date
	 * @return
	 */
	public static String dateToMysqlString(java.util.Date date) {
		if (date == null) {
			return null;
		}
		return new SimpleDateFormat("yyyyMMdd").format(date);
	}
	
	/**
	 * 转换时间为ORACLE日期时分秒 
	 * @param Date
	 * @return
	 */
	public static String DateToOracleString(Date date) {
		if (date == null) {
			return null;
		}
		return "to_date('" + sdf.format(date) + "', 'YYYY-MM-DD HH24:MI:SS')";
	}

	/**
	 * 转换时间为ORACLE日期 
	 * @param Date
	 * @return
	 */
	public static String dateToOracleString(Date date) {
		if (date == null) {
			return null;
		}
		return "to_date('" + new SimpleDateFormat("yyyy-MM-dd").format(date) + "', 'YYYY-MM-DD')";
	}

	/**
	 * 给日期增加值 
	 * @param date
	 * @param field
	 * @param amount
	 * @return
	 */
	public static Date add(Date date, int field, int amount) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date.getTime());
		calendar.add(field, amount);
		return new Date(calendar.getTimeInMillis());
	}
	
	/**
	 * 字符串转calendar
	 * @param date 日期的字符串
	 * @param pattern 日期的格式
	 * @return
	 */
	public static Calendar stringToCalendar(String date, String pattern){
		Calendar calendar = null;
		try {
			calendar = Calendar.getInstance();
			SimpleDateFormat format = new SimpleDateFormat(pattern);
			calendar.setTime(format.parse(date));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return calendar;
	}
	
	/**取上个月的第一天*/
	public static Calendar getLastMonFirstDay(Calendar calendar){
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		if (month > 0) {
			month--;
		} else {
			year --;
			month = 11;
		}
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return calendar;
	}
	
	/**取上个月的最后一天*/
	public static Calendar getLastMonLastDay(Calendar calendar){
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		return calendar;
	}
	
	/**取得上一天*/
	public static Calendar getLastDay(Calendar calendar){
	    int year=calendar.get(Calendar.DAY_OF_YEAR);
		 calendar.set(Calendar.DAY_OF_YEAR, year--);
		return calendar;
	}
	
	/**取上个周的最后一天*/
	public static Calendar getLastweekLastDay(Calendar calendar){
		calendar.add(Calendar.SUNDAY, -7);
		return calendar;
	}
	
	//将当前时间转换字符串(格式为yyyy-MM-dd HH:mm:ss)
	public static String getCurrentDateTimeString(){
		return toDateTimeString(now());
	}	
	
	//将字符串转换Date时间(格式是年月日时分秒)
	public static Date parseGoloDate(String timeStr) throws ParseException {
		return new SimpleDateFormat("yyyy-MM-dd+HH:mm:ss").parse(timeStr);
	}
	
	//获取两个时间差(以月为单位)
	public static int getMonthInterval(Date startTime,Date endTime){
		Calendar starCal = Calendar.getInstance();
		starCal.setTime(startTime);
		int sYear = starCal.get(Calendar.YEAR);
		int sMonth = starCal.get(Calendar.MONTH);
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(endTime);
		int eYear = endCal.get(Calendar.YEAR);
		int eMonth = endCal.get(Calendar.MONTH);

		return ((eYear - sYear) * 12 + (eMonth - sMonth));
	}
	
	//获取当前时间月末最后一天最晚的时间(23点59分59秒)
	public static Date getCurMonLastTime(Date date){
		   Calendar cal = Calendar.getInstance();
           cal.setTime(date);
           cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
           cal.set(Calendar.HOUR_OF_DAY, 23);
           cal.set(Calendar.MINUTE, 59);
           cal.set(Calendar.SECOND, 59);
           return cal.getTime();
	}
	
	//获取当前时间下个月第一天的最早时间(零时、零分、零秒)
	public static Date getNextMonFirstTime(Date date){
		Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
	}
	
	//计算两个时间差(以天为单位)
    public static long daysBetween(Date smdate,Date bdate) throws ParseException {    
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
        smdate=sdf.parse(sdf.format(smdate));  
        bdate=sdf.parse(sdf.format(bdate));  
        Calendar cal = Calendar.getInstance();    
        cal.setTime(smdate);    
        long time1 = cal.getTimeInMillis();                 
        cal.setTime(bdate);    
        long time2 = cal.getTimeInMillis();         
        long between_days=(time2-time1)/(1000*3600*24);  
        return Math.abs(between_days);
//       return Integer.parseInt(String.valueOf(between_days));           
    } 
    
    //获取当天最晚的时间
	public static Date getCurDayLastTime(Date date){
		Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
	}
	
	//获取明天零时零分零秒(明天最早的时间)
	public static Date getNextDayFirstTime(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();
	}
	
	//当前时间倒退1个月
	public static String getBC30DayTime(){
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -1);   
		return sdf.format(c.getTime());       
	}

}
