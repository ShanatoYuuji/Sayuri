package com.sayuri.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jeeplus.common.web.BaseController;
import com.jeeplus.modules.onlineShop.entity.WosOrder;
import com.jeeplus.modules.onlineShopAPI.serviceAPI.WosOrderServiceAPI;
import com.jeeplus.modules.restaurant.entity.WcsOrder;
import com.jeeplus.modules.restaurantAPI.serviceAPI.WcsOrderServiceAPI;
import com.jeeplus.modules.sys.entity.VicmobTemplate;
import com.jeeplus.modules.sys.service.VicmobTemplateService;

import net.sf.json.JSONObject;

/**
 * 适用于多行业模块的模板消息
 * 
 * @author liuchj
 * @2017年10月12日
 * @VicmobMINA
 *
 */
@Controller
@RequestMapping(value = "/Template")
public class TemplateUtils extends BaseController {

	@Autowired
	private WcsOrderServiceAPI wcsOrderService;
	@Autowired
	private WosOrderServiceAPI wosOrderService;
	@Autowired
	private VicmobTemplateService vicmobTemplateService;

	/**
	 * 微商店模板消息---订单 专用工具类 根据informType的值判断是哪种通知类型该适用于哪个模板
	 * 
	 * @param fansId
	 *            粉丝ID
	 * @param minaId
	 *            小程序ID
	 * @param url
	 *            点击通知跳转链接
	 * @param form_id
	 *            表单提交场景下，为 submit 事件带上的 formId；支付场景下，为本次支付的 prepay_id
	 * @param informType
	 *            通知类型
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping(value = "/wos/message", method = RequestMethod.POST)
	@ResponseBody
	public void sendSmallAppTemplateMessageSimpleForWos(HttpServletRequest request) throws UnsupportedEncodingException {
		System.out.println("TemplateUtils====sendSmallAppTemplateMessageSimpleForWos");
		InputStream iStream = null;
		try {
			iStream = request.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		JSONObject paramJson = CommUtil.loadString(iStream);
		// 获取页面传入值
		String orderId = paramJson.getString("orderId");
		String fansId = paramJson.getString("fansId");
		Integer fans_id = Integer.parseInt(fansId);
		String minaId = paramJson.getString("minaId");
		String url = paramJson.getString("url");
		String form_id = paramJson.getString("form_id");
		String informType = paramJson.getString("informType");
		Integer inform_type = Integer.parseInt(informType);

		WosOrder wosOrder = wosOrderService.get(orderId);

		System.out.println("22222222222222222222222222222===inform_type=" + inform_type + ",minaId=" + minaId);
		VicmobTemplate vicmobTemplate = vicmobTemplateService.findEntity(inform_type, Integer.parseInt(minaId));

		System.err.println("vicmobTemplate===" + vicmobTemplate);

		String informId = vicmobTemplate.getInformId();// 模板ID

		SetTemplateMessage setTemplateMessage = new SetTemplateMessage();
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 当前时间
		String currentTime = dateFormat.format(date);

		 String orderTimeString = wosOrder.getOrderStartTime();// 下单时间

		Double totalMoney = wosOrder.getTotalMoney();// 订单金额
		String orderMoney = totalMoney + "";

		Integer orderStatus = wosOrder.getOrderStatus();// 订单状态
		String status = null;
		switch (orderStatus) {
		case 1:
			status = "订单已支付";
			break;
		case 2:
			status = "订单已完成";
			break;
		case 3:
			status = "订单已超时";
			break;
		case 4:
			status = "订单已取消";
			break;
		case 5:
			status = "退款中";
			break;
		case 6:
			status = "退款成功";
			break;
		case 7:
			status = "退款失败";
			break;
		}

		switch (inform_type) {// 根据模板消息类型选择模板
		case 1:// 下单成功通知
			setTemplateMessage.sendSmallAppTemplateMessage(fans_id, minaId, url, form_id, informId,
					wosOrder.getOrderNumber(), orderMoney, status, orderTimeString, wosOrder.getAddress(),"下单成功了", "", "", 0);
			break;
		case 2:// 订单取消通知
			setTemplateMessage.sendSmallAppTemplateMessage(fans_id, minaId, url, form_id, informId,  
					wosOrder.getOrderNumber(), status, orderTimeString, currentTime, "订单取消了", "", "", "", 0);
			break;
		case 3:// 订单超时提醒
			setTemplateMessage.sendSmallAppTemplateMessage(fans_id, minaId, url, form_id, informId,
					wosOrder.getOrderNumber(), status, orderTimeString,  "", "", "", "", "", 0);
			break;
		case 4:// 订单完成通知
			setTemplateMessage.sendSmallAppTemplateMessage(fans_id, minaId, url, form_id, informId,
					wosOrder.getOrderNumber(), orderMoney, status, orderTimeString, currentTime, "订单完成了", "", "", 0);
			break;
		case 5:// 退款申请通知
			setTemplateMessage.sendSmallAppTemplateMessage(fans_id, minaId, url, form_id, informId,
					wosOrder.getOrderNumber(), orderMoney, currentTime, "请稍等，订单退款中", "", "", "", "", 0);
			break;
		case 6:// 退款成功通知
			setTemplateMessage.sendSmallAppTemplateMessage(fans_id, minaId, url, form_id, informId,
					wosOrder.getOrderNumber(), orderMoney, wosOrder.getOrderRefundTime(), currentTime, "退款已成功，请注意查收", "", "", "", 0);
			break;
		case 7:// 退款失败通知
			setTemplateMessage.sendSmallAppTemplateMessage(fans_id, minaId, url, form_id, informId,
					wosOrder.getOrderNumber(), orderMoney, URLDecoder.decode(wosOrder.getRefuseReason(), "UTF-8"), "", "", "", "", "", 0);
			break;
		}
	}

	/**
	 * 微餐饮模板消息---订单 专用工具类 根据informType的值判断是哪种通知类型该适用于哪个模板
	 *  
	 * @param fansId
	 *            粉丝ID
	 * @param minaId
	 *            小程序ID
	 * @param url
	 *            点击通知跳转链接
	 * @param form_id
	 *            表单提交场景下，为 submit 事件带上的 formId；支付场景下，为本次支付的 prepay_id
	 * @param informType
	 *            通知类型
	 */
	@RequestMapping(value = "/wcs/message", method = RequestMethod.POST)
	@ResponseBody
	public void sendSmallAppTemplateMessageSimpleForWcs(HttpServletRequest request) {
		System.out.println("TemplateUtils==========sendSmallAppTemplateMessageSimpleForWcs");
		InputStream iStream = null;
		try {
			iStream = request.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		JSONObject paramJson = CommUtil.loadString(iStream);
		// 获取页面传入值
		String orderId = paramJson.getString("orderId");
		String fansId = paramJson.getString("fansId");
		Integer fans_id = Integer.parseInt(fansId);
		String minaId = paramJson.getString("minaId");
		String url = paramJson.getString("url");
		String form_id = paramJson.getString("form_id");
		String informType = paramJson.getString("informType");
		Integer inform_type = Integer.parseInt(informType);

		WcsOrder wcsOrder = wcsOrderService.get(Integer.parseInt(orderId));

		System.out.println("22222222222222222222222222222===inform_type=" + inform_type + ",minaId=" + minaId);
		VicmobTemplate vicmobTemplate = vicmobTemplateService.findEntity(inform_type, Integer.parseInt(minaId));

		System.err.println("vicmobTemplate===" + vicmobTemplate);

		String informId = vicmobTemplate.getInformId();// 模板ID

		SetTemplateMessage setTemplateMessage = new SetTemplateMessage();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 当前时间
		Date updateTime=wcsOrder.getUpdateTime();
		String currentTime = dateFormat.format(updateTime);

		Date orderTime = wcsOrder.getOrderTime();// 下单时间
		String orderTimeString = dateFormat.format(orderTime);

		Double totalMoney = wcsOrder.getTotalMoney();// 订单金额
		String orderMoney = totalMoney + "";

		Integer orderStatus = wcsOrder.getOrderStatus();// 订单状态
		String status = null;
		switch (orderStatus) {
		case 1:
			status = "订单已取消";
			break;
		case 2:
			status = "订单已超时";
			break;
		case 3:
			status = "订单待收货";
			break;
		case 4:
			status = "订单已完成";
			break;
		case 5:
			status = "退款中";
			break;
		case 6:
			status = "退款失败";
			break;
		case 7:
			status = "退款成功";
			break;
		}
		
		System.err.println("============小程序模板订单状态====="+status+"=======");

		switch (inform_type) {// 根据模板消息类型选择模板
		case 1:// 下单成功通知
			setTemplateMessage.sendSmallAppTemplateMessage(fans_id, minaId, url, form_id, informId,
					wcsOrder.getOrderNumber(), orderMoney, status, orderTimeString, wcsOrder.getAddress(), "下单成功了",  "", "", 0);
			break;
		case 2:// 订单取消通知
			setTemplateMessage.sendSmallAppTemplateMessage(fans_id, minaId, url, form_id, informId, 
					wcsOrder.getOrderNumber(), status, orderTimeString, currentTime, "订单取消了", "", "", "", 0);
			break;
		case 3:// 订单超时提醒
			setTemplateMessage.sendSmallAppTemplateMessage(fans_id, minaId, url, form_id, informId,
					wcsOrder.getOrderNumber(), status, orderTimeString,  "", "", "", "", "", 0);
			break;
		case 4:// 订单完成通知
			setTemplateMessage.sendSmallAppTemplateMessage(fans_id, minaId, url, form_id, informId,
					wcsOrder.getOrderNumber(), orderMoney, status, orderTimeString, currentTime, "订单完成了", "", "", 0);
			break;
		case 5:// 退款申请通知
			setTemplateMessage.sendSmallAppTemplateMessage(fans_id, minaId, url, form_id, informId,
					wcsOrder.getOrderNumber(), orderMoney, currentTime, "请稍等，订单退款中", "", "", "", "", 0);
			break;
		case 6:// 退款成功通知
			setTemplateMessage.sendSmallAppTemplateMessage(fans_id, minaId, url, form_id, informId,
					wcsOrder.getOrderNumber(), orderMoney, dateFormat.format(wcsOrder.getOrderRefundTime()), currentTime, "退款已成功，请注意查收", "", "", "", 0);
			break;
		case 7:// 退款失败通知
			setTemplateMessage.sendSmallAppTemplateMessage(fans_id, minaId, url, form_id, informId,
					wcsOrder.getOrderNumber(), orderMoney, wcsOrder.getRefuseReason(), "", "", "", "", "", 0);
			break;
		}
	}

}
