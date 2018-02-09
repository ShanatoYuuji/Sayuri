package com.sayuri.common;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeeplus.modules.onlineShop.entity.WosOrder;
import com.jeeplus.modules.restaurant.entity.WcsOrder;
import com.jeeplus.modules.sys.utils.ConfigurationFileHelper;

import net.sf.json.JSONArray;

@Service
@Transactional(readOnly = true)
public class SendMessage {
	
	public void wosMessage(WosOrder order,String formId, String url){
		Map<Object, Object> param = new HashMap<>();
		param.put("orderId", order.getOrderId());
		param.put("fansId", order.getFansId());
		param.put("minaId", order.getMinaId());
		param.put("url", url);
		param.put("form_id", formId);
		param.put("informType", getFormType(order.getOrderStatus()));// 取消订单通知类型
		
		String jsonStr = JSONArray.fromObject(param).toString();
		jsonStr = jsonStr.substring(1, jsonStr.length() - 1);
		
		String serviceUrl = ConfigurationFileHelper.getServiceUrl();
		String sendMsgUrl = serviceUrl + "/VicmobMINA/Template/wos/message";
		CommUtil.doPost(sendMsgUrl, jsonStr);
	}
	
	public void wcsMessage(WcsOrder order,String formId, String url){
		Map<Object, Object> param = new HashMap<>();
		param.put("orderId", order.getOrderId());
		param.put("fansId", order.getFansId());
		param.put("minaId", order.getMinaId());
		param.put("url", url);
		param.put("form_id", formId);
		param.put("informType", getFormType2(order.getOrderStatus()));// 取消订单通知类型
		
		String jsonStr = JSONArray.fromObject(param).toString();
		jsonStr = jsonStr.substring(1, jsonStr.length() - 1);
		
		String serviceUrl = ConfigurationFileHelper.getServiceUrl();
		String sendMsgUrl = serviceUrl + "/VicmobMINA/Template/wcs/message";
		CommUtil.doPost(sendMsgUrl, jsonStr);
	}
	
	private int getFormType(int orderStatus){
		//模板消息ID
		int informType = 0;
		
		switch (orderStatus) {
		case 1://已支付，下单成功
			informType = 1;
			break;
		case 2://已完成
			informType = 4;	
			break;
		case 4://已取消
			informType = 2;
			break;
		case 5://申请退，退款中
			informType = 5;
			break;
		case 6://已退款，退款成功
			informType = 6;
			break;
		case 7://拒绝退款
			informType = 7;
			break;
		default:
			break;
		}
		
		return informType;
	}
	
	private int getFormType2(int orderStatus){
		//模板消息ID
		int informType = 0;
		
		switch (orderStatus) {
		case 1://订单取消
			informType = 2;
			break;
		case 3://待收货
			informType = 1;	
			break;
		case 4://已完成
			informType = 4;
			break;
		case 5://申请退，退款中
			informType = 5;
			break;
		case 6://退款失败
			informType = 7;
			break;
		case 7://退款成功
			informType = 6;
			break;
		default:
			break;
		}
		
		return informType;
	}
	
}
