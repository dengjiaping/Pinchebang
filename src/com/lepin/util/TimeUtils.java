package com.lepin.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.annotation.SuppressLint;

/**
 * 常用时间处理工具类
 * 
 * 
 */
@SuppressLint("SimpleDateFormat")
public class TimeUtils {

	public final static String TIME_FORMART_HM = "yyyy-MM-dd HH:mm";
	public final static String TIME_FORMART_HMS = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 将HH:mm转换为秒
	 * 
	 * @param date
	 * @return
	 */
	public static long dateToSecond(String date) {
		String[] str = date.split(":");
		int hour = Integer.parseInt(str[0]);
		int min = Integer.parseInt(str[1]);
		return hour * 3600 + min * 60;
	}

	/**
	 * 将yyyyMMdd转换为秒
	 * 
	 * @param date
	 * @return
	 */
	public static long dateStrToSecond(String date) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMART_HM);
			Date d = sdf.parse(date);
			return d.getTime() / 1000;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 将秒数转换为时间HH:mm格式
	 * 
	 * @param secondTime
	 * @return
	 */
	public static String secondToDate(long secondTime) {
		int hour = (int) (secondTime / (60 * 60));
		int minute = (int) (secondTime / 60);
		if (minute >= 60) {
			minute = minute % 60;
			hour += minute / 60;
		}
		String sh = "";
		String sm = "";
		if (hour < 10) {
			sh = "0" + String.valueOf(hour);
		} else {
			sh = String.valueOf(hour);
		}
		if (minute < 10) {
			sm = "0" + String.valueOf(minute);
		} else {
			sm = String.valueOf(minute);
		}
		return sh + ":" + sm;
	}

	/**
	 * 时间毫秒数转换为指定格式日期
	 * 
	 * @param date时间秒数
	 * @param format指定格式
	 * @return
	 */
	public static String formatDate(long date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	/**
	 * 时间毫秒数转换为指定格式日期
	 * 
	 * @param date时间秒数
	 * @param format指定格式
	 * @return
	 */
	public static String formatDate(String date, String format) {
		Long long1 = Long.parseLong(date);
		return formatDate(long1, format);
	}

	/**
	 * 时间自动补0转换
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static String parseStr2Date(String date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		try {
			return sdf.format(sdf.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 我的预约有用到
	 * 
	 * @param date
	 * @return
	 */
	public static Date parseStr2Date(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMART_HMS);
		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取系统当前时间
	 * 
	 * @return
	 */
	public static String getCurrentTime() {
		return new SimpleDateFormat(TIME_FORMART_HMS).format(new Date(System.currentTimeMillis()));
	}

	/**
	 * 获取指定格式的当前系统时间
	 * 
	 * @param format
	 * @return
	 */
	public static String getCurrentTime(String format) {
		return new SimpleDateFormat(format).format(new Date(System.currentTimeMillis()));
	}

	/**
	 * 字符串转换为yyyy-MM-dd
	 * 
	 * @param paramString
	 * @return
	 */
	public static String formartTaskDate(String paramString) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			return sdf.format(sdf.parse(paramString));
		} catch (ParseException e) {
			e.printStackTrace();
			return "";
		}
	}


}
