package com.sayuri.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.sun.jmx.snmp.Timestamp;

/**
 * 
 * 微餐饮微商店用订单工具类
 *
 */
public class WcswosOrdermanage {
	
	
	/**
	 * 根据参数获得订单号
	 *@param type 1:微餐饮外卖/微商店订单号 2：微餐饮堂食
	 *
	 */
	public static String getOrderNumber(int type,String fansId ) {
		try {
		
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
		Date date=Calendar.getInstance().getTime();
		String timeStr=sdf.format(date);//年月日时分秒
		String ts =String.valueOf(System.currentTimeMillis());
		ts=ts.substring(ts.length()-8,ts.length());//时间戳后八位
		String fansidStr=fansId;
		if(fansId!=null&&fansId.length()<2) {
			fansidStr=String.format("%02d", Integer.valueOf(fansId));
		}
		String result="不符合类型";
		//微餐饮外卖/微商店订单号
		if(type==1) {
			result="SH"+timeStr+fansidStr+ts;
		}
		//微餐饮堂食
		if(type==2) {
			result="TS"+timeStr+fansidStr+ts;
		}
		
		return result;
		}catch(Exception e){
			return "获取不到";
		}
	}
	
}
