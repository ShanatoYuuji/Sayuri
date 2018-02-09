package com.sayuri.common;

import net.sf.json.JSONObject;
/**
 * 
 * @author Squirrel
 *
 */
public class MapDistanceUtils {

	/**
	 * 调用计算距离的接口
	 * @param locationX 用户经度
	 * @param locationY 用户纬度
	 * @param bd_lon	门店经度
	 * @param bd_lat	门店纬度
	 * @return
	 */
	public static double getDistance(double locationX,double locationY,double bd_lon,double bd_lat){
		
		//百度坐标转化成火星坐标
		JSONObject jsonObject=changeLocation(bd_lon,bd_lat);
		//计算距离
		double distance=getmatch(locationX, locationY,(double)jsonObject.get("gg_lon"),(double)jsonObject.get("gg_lat"));
		System.out.println("distance="+distance+" m");
		//赋值,米转换为km(保留两位小数)
		double mlocation=(double) (Math.round(distance/1000*100)/100.0);
		//如果小于100米，直接赋值0.09km
		if(distance<100){
			mlocation=0.09;
		}
		
		return mlocation;
	}
	
	/**
	 * 百度坐标转化成火星坐标
	 * @param bd_lon
	 * @param bd_lat
	 */
	public static JSONObject changeLocation(double bd_lon,double bd_lat){

		 double X_PI = Math.PI * 3000.0 / 180.0;
		 double x = bd_lon - 0.0065;
		 double y = bd_lat - 0.006;
		 double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * X_PI);
		 double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * X_PI);
		 double gg_lon = z * Math.cos(theta);
		 double gg_lat = z * Math.sin(theta);
		 
		 JSONObject jsonObject=new JSONObject();
		 jsonObject.put("gg_lon", gg_lon);
		 jsonObject.put("gg_lat", gg_lat);
		 
		return jsonObject;
	}
	
	//------------------------------------计算两经纬度之间的距离-------------------------------------------
	public static  double getmatch(double mlongitude,double mlatitude,double location_x,double location_y){
		
		//两个经纬度组成两个json对象
		JSONObject jsonObject1=new JSONObject();
		jsonObject1.put("lng", mlongitude);
		jsonObject1.put("lat", mlatitude);
		
		JSONObject jsonObject2=new JSONObject();
		jsonObject2.put("lng", location_x);
		jsonObject2.put("lat", location_y);
		
		double  distance = getDistance(jsonObject1, jsonObject2);
		
		return distance;
	};
	
	public static double getDistance(JSONObject a,JSONObject b) {
		
		double a_lng = fD((double)a.get("lng"), -180, 180);
		double a_lat = jD((double)a.get("lat"), -74, 74);
		double b_lng = fD((double)b.get("lng"), -180, 180);
		double b_lat = jD((double)b.get("lat"), -74, 74);
		
		return Ce(yk(a_lng), yk(b_lng), yk(a_lat), yk(b_lat));
	}
	
	public static double Ce(double a,double b,double c,double d) {
		double dO = 6370996.81;
		return dO * Math.acos(Math.sin(c) * Math.sin(d) + Math.cos(c) * Math.cos(d) * Math.cos(b - a));
	}
	
	public static double fD(double a,double b,double c) {
		for(; a > c;)
			a -= c - b;
		for(; a < b;)
			a += c - b;
		return a;
	}

	public static double jD(double a,double b,double c) {
		
//		b != null && (a = Math.max(a, b));
//		c != null && (a = Math.min(a, c));
		a = Math.max(a, b);
		a = Math.min(a, c);
		return a;
	}

	public static double yk(double a) {
		return Math.PI * a / 180 ;
	}

	//-------------------------------------------------------------------------------

}
