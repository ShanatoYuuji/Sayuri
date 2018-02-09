package com.sayuri.common;

import java.util.SortedMap;
import java.util.TreeMap;

import com.jeeplus.common.utils.WxInfoUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 模板消息工具类
 * 
 * @author liuchj
 * @2017年10月11日
 * @VicmobMINA
 *
 */
public class SetTemplateMessage {

	/**
	 * 
	 * @param fansId
	 *            粉丝Id
	 * @param minaId
	 *            小程序Id
	 * @param url
	 *            点击模板消息跳转的链接
	 * @param formId
	 *            表单提交场景下，为 submit 事件带上的 formId；支付场景下，为本次支付的 prepay_id
	 * @param keyword1Content
	 *            模板第一个key对应的值
	 * @param keyword2Content
	 *            模板第二个key对应的值
	 * @param keyword3Content
	 *            模板第三个key对应的值
	 * @param keyword4Content
	 *            模板第四个key对应的值
	 * @param keyword5Content
	 *            模板第五个key对应的值
	 * @param keyword6Content
	 *            模板第六个key对应的值
	 * @param keyword7Content
	 *            模板第七个key对应的值
	 * @param keyword8Content
	 *            模板第八个key对应的值
	 * @param emphasisKey
	 *            模板需要放大的关键词的标识（例如：1标识关键词为keyword1.DATA 注：不需要放大关键词的时候，填写0）
	 */
	public void sendSmallAppTemplateMessage(Integer fansId, String minaId, String url, String formId,
			String template_id, String keyword1Content, String keyword2Content, String keyword3Content,
			String keyword4Content, String keyword5Content, String keyword6Content, String keyword7Content,
			String keyword8Content, Integer emphasisKey) {

		System.out.println("========================小程序模板消息========sendSmallAppTemplateMessage=============================");
		System.out.println("==sendSmallAppTemplateMessage========formId=" + formId);

		String access_token = WxInfoUtil.attainAccessToken(Integer.parseInt(minaId));
		System.out.println("==sendSmallAppTemplateMessage========access_token=" + access_token);
		
		String openId = WxInfoUtil.attainOpenId(Integer.parseInt(minaId), fansId);
		System.out.println("==sendSmallAppTemplateMessage========openId=" + openId);

		// String template_id = "03bICAfTTzdx9tMqbrAKurQhKbYDigalsKgfvGHFT0s";

		String form_id = formId;
		SortedMap<Object, Object> map = new TreeMap<>();
		if (!"".equals(keyword1Content)) {
			map.put("keyword1", keyword1Content);
		}
		if (!"".equals(keyword2Content)) {
			map.put("keyword2", keyword2Content);
		}
		if (!"".equals(keyword3Content)) {
			map.put("keyword3", keyword3Content);
		}
		if (!"".equals(keyword4Content)) {
			map.put("keyword4", keyword4Content);
		}
		if (!"".equals(keyword5Content)) {
			map.put("keyword5", keyword5Content);
		}
		if (!"".equals(keyword6Content)) {
			map.put("keyword6", keyword6Content);
		}
		if (!"".equals(keyword7Content)) {
			map.put("keyword7", keyword7Content);
		}
		if (!"".equals(keyword8Content)) {
			map.put("keyword8", keyword8Content);
		}

		setSmallAppTemplate(access_token, openId, template_id, url, form_id, map, emphasisKey);
	}

	/**
	 * 小程序模板消息发送util
	 * 
	 * @param access_token
	 *            小程序access_token
	 * @param openId
	 *            发送的粉丝的openId
	 * @param template_id
	 *            模板Id
	 * @param url
	 *            点击模板消息跳转的链接
	 * @param form_id
	 *            表单提交场景下，为 submit 事件带上的 formId；支付场景下，为本次支付的 prepay_id
	 * @param hashMap
	 *            发送的内容
	 * @param emphasisKey
	 *            模板需要放大的关键词的标识（例如：1标识关键词为keyword1.DATA 注：不需要放大关键词的时候，填写0）
	 */
	public static void setSmallAppTemplate(String access_token, String openId, String template_id, String url,
			String form_id, SortedMap<Object, Object> hashMap, Integer emphasisKey) {

		// 小程序给用户发模板消息
		SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
		parameters.put("touser", openId);
		parameters.put("template_id", template_id);
		parameters.put("page", url);
		parameters.put("form_id", form_id);
		// 循环添加到data
		JSONObject jsonObject = new JSONObject();
		for (int i = 0; i < hashMap.size(); i++) {

			JSONObject jsonObject2 = new JSONObject();
			jsonObject2.put("value", hashMap.get("keyword" + (i + 1)));
			jsonObject2.put("color", "#173177");
			jsonObject.put("keyword" + (i + 1), jsonObject2);

		}
		parameters.put("data", jsonObject);
		switch (emphasisKey) {
		case 0:
			parameters.put("emphasis_keyword", "");
			break;
		case 1:
			parameters.put("emphasis_keyword", "keyword1.DATA");
			break;
		case 2:
			parameters.put("emphasis_keyword", "keyword2.DATA");
			break;
		case 3:
			parameters.put("emphasis_keyword", "keyword3.DATA");
			break;
		case 4:
			parameters.put("emphasis_keyword", "keyword4.DATA");
			break;
		case 5:
			parameters.put("emphasis_keyword", "keyword5.DATA");
			break;
		case 6:
			parameters.put("emphasis_keyword", "keyword6.DATA");
			break;
		case 7:
			parameters.put("emphasis_keyword", "keyword7.DATA");
			break;
		case 8:
			parameters.put("emphasis_keyword", "keyword8.DATA");
			break;
		default:
			break;
		}

		String jsonStr = JSONArray.fromObject(parameters).toString();
		// 去掉头尾“[]”
		jsonStr = jsonStr.substring(1, jsonStr.length() - 1);
		System.out.println("=====================================================");
		System.out.println("===jsonStr====" + jsonStr);
		// 获取发送后接口返回数据
		String sendMsgUrl = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token="
				+ access_token;
		CommUtil.doPost(sendMsgUrl, jsonStr);
	}
}
