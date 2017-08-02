package com.chh.dc.icp.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 * DateUtil
 *
 * @version 1.0.0
 */
public class DateUtil{

	/** 转换时间为字符串格式 yyyy-MM-dd HH:mm:ss */
	public static String getDateTimeString(Date date){
		String pattern = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat f = new SimpleDateFormat(pattern);
		return f.format(date);
	}

	/** 把 字符串 转换成 时间 */
	public static Date getDate(String str, String pattern) throws ParseException{
		SimpleDateFormat f = new SimpleDateFormat(pattern);
		Date d = f.parse(str);

		return d;
	}

	/** 把 yyyy-MM-dd HH:mm:ss形式的字符串 转换成 时间 */
	public static Date getDate1(String str) throws ParseException{
		String pattern = "yyyy-MM-dd HH:mm:ss";

		return getDate(str, pattern);
	}

	/**
	 * 把时间转化成formatPattern格式字符串
	 * 
	 * @param date
	 * @param formatPattern
	 * @return
	 */
	public static String getDateTimeString(Date date, String formatPattern) throws ParseException{
		SimpleDateFormat f = new SimpleDateFormat(formatPattern);
		return f.format(date);
	}

	/**
	 * 把字符串时间，转换成toPattern格式字符串时间
	 * 
	 * @param eventTime
	 * @param fromPattern
	 * @param toPattern
	 * @return
	 */
	public static String transStringDate(String eventTime, String fromPattern, String toPattern) throws ParseException{
		SimpleDateFormat f = new SimpleDateFormat(toPattern);
		return f.format(getDate(eventTime, fromPattern));
	}
	
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

    public static long daysBetween(Date smdate,Date bdate) throws ParseException    
    {    
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
    
	public static Date getCurDayLastTime(Date date){
		   Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
	}
	
	public static Date getNextDayFirstTime(Date date){
		   Calendar cal = Calendar.getInstance();
     cal.setTime(date);
     cal.add(Calendar.DAY_OF_MONTH, 1);
     cal.set(Calendar.HOUR_OF_DAY, 0);
     cal.set(Calendar.MINUTE, 0);
     cal.set(Calendar.SECOND, 0);
     return cal.getTime();
	}

	public static Date getNextSecondTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.SECOND, 1);
		return cal.getTime();
	}
	
	/**
	 * 转换日期为字符串
	 * 
	 * @param date
	 * @return
	 */
	public static String toDateString(Date date) {
		if (date == null) {
			return null;
		}
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}

	public static String toTimeString(Date date) {
		if (date == null) {
			return null;
		}
		return new SimpleDateFormat("HH:mm:ss").format(date);
	}
}
