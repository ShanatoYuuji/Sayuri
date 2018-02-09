package com.sayuri.common;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jeewx.api.core.exception.WexinReqException;
import org.jeewx.api.third.JwThirdAPI;
import org.jeewx.api.third.model.ApiComponentToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jeeplus.common.web.BaseController;
import com.jeeplus.modules.sys.entity.MinaInfo;
import com.jeeplus.modules.sys.entity.VicmobFansInfo;
import com.jeeplus.modules.sys.entity.WeixinOpenAccount;
import com.jeeplus.modules.sys.service.MinaInfoService;
import com.jeeplus.modules.sys.service.VicmobFansInfoService;
import com.jeeplus.modules.sys.service.WeixinOpenAccountService;
import com.jeeplus.modules.sys.utils.ConfigurationFileHelper;
import com.sayuri.common.CommUtil;

import net.sf.json.JSONObject;

/**
 * 获取用户信息API
 * 
 * @author squirrel
 * @version 2017-7-5
 */
@Controller
@RequestMapping(value = "minaAPI/restaurant/user")
public class WcsUserMessageAPI extends BaseController {
	
	 /**
      * 微信第三方平台账号信息
      */
     private final static String COMPONENT_APPID = ConfigurationFileHelper.getComponentAppid();
     private final static String COMPONENT_APPSECRET = ConfigurationFileHelper.getComponentAppsecret();
     
     @Autowired
     private WeixinOpenAccountService weixinOpenAccountService;
     
	 @Autowired
	 private MinaInfoService minaInfoService;

	 @Autowired
	 private VicmobFansInfoService vicmobFansInfoService;
	 
	/**
	 * 获取用户信息(openid,session_key)
	 * 
	 * @return
	 * @throws WexinReqException 
	 */
	@RequestMapping(value = "/message", method = RequestMethod.POST)
	@ResponseBody
	public String getFindList(HttpServletRequest request) throws WexinReqException {
		
		
		String minaIdAes=request.getParameter("minaId").trim();//得到参数，小程序ID
		String code = request.getParameter("code").trim();// 得到code
		
		try {
			String appId = request.getParameter("appId").trim();
			String minaType = request.getParameter("minaType").trim();
			if(minaType.equals("4") || minaType.equals("1")) {
				minaIdAes=AesEncrypt.aesDecryption(appId, minaIdAes);
			}
		} catch (Exception e1) {
			System.out.println(e1.toString());
			return "错误信息："+e1.toString();
		}
		
		Integer minaId=Integer.valueOf(minaIdAes);
		if (code != null && !code.equals("")) {// 小程序ID不为0
			//获取小程序APPID
			String APPID = "";// 小程序APPID
			String APPSECRET = "";// 小程序APPSECRET
			MinaInfo minaInfo = getMINAAppId(minaId);
			APPID=minaInfo.getAppid();
			APPSECRET=minaInfo.getAppsecret();
			
			if(! APPSECRET.equals("") && APPSECRET !=null && ! APPSECRET.equals("null")){
				/*//获取第三方开发的accessToken
				String ACCESS_TOKEN = "";// 第三方accessToken
				ACCESS_TOKEN=getComponentMessage(minaId);
				// 通过微信接口得到用户的openid和session_key
				String url = "https://api.weixin.qq.com/sns/component/jscode2session?appid=" + APPID + "&js_code=" + code
						+ "&grant_type=authorization_code&component_appid=" + COMPONENT_APPID + "&component_access_token="
						+ ACCESS_TOKEN;*/	
				
				
				String url = "https://api.weixin.qq.com/sns/jscode2session?appid="+APPID
						+"&secret="+APPSECRET+"&js_code="+code+"&grant_type=authorization_code";
				
				JSONObject json_object = CommUtil.httpsRequest(url, "GET", null);
				System.out.println("json_object===="+json_object);
				//得到微信返回的两个值(openid,session_key)
				String openId = json_object.getString("openid");
				
				String session_key = json_object.getString("session_key");//暂时用不到（解密时要用）
			
				
				if(openId.equals("") || openId ==null){
					return "GET OPENID ERROR !";
				}
				//存入vicmob_fans表格
				//得到这个粉丝的fansId
				String fansId="";
				fansId=getMINAFansId(openId,minaId);
				
				if(fansId.equals("") || fansId ==null){
					return "GET FANSID ERROR !";
				}
				return fansId;
				
			}else{
				return "GET MINA_APPSECRET ERROR !";
			}
		}

		return "CODE IS NONE !";

	}
	
	
	/**
	 * 更新用户信息(nickName,gender,avatarUrl)
	 * 
	 * @return
	 * @throws WexinReqException 
	 */
	@RequestMapping(value = "/updateFans", method = RequestMethod.POST)
	@ResponseBody
	public Integer updateFansMessage(HttpServletRequest request, VicmobFansInfo vicmobFansInfo) throws WexinReqException {
		
		//小程序传值过来
		Integer fansId = Integer.valueOf(request.getParameter("fansId").trim());// 得到fansId
		String nickName = request.getParameter("nickName").trim();// 得到nickName
		Integer gender = Integer.valueOf(request.getParameter("gender").trim());// 得到gender
		String avatarUrl = request.getParameter("avatarUrl").trim();// 得到avatarUrl
		/*try {
			nickName = new String(nickName.getBytes("ISO-8859-1"),"UTF-8");
	    } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
	    }*/
		vicmobFansInfo.setFansId(fansId);
		vicmobFansInfo.setNickname(nickName);
		vicmobFansInfo.setSex(gender);
		vicmobFansInfo.setAvatar(avatarUrl);
		//更新fans信息(更新失败返回值为0)
		Integer updateStatus = vicmobFansInfoService.updateData(vicmobFansInfo);
		
		return updateStatus;
	}
	
	
	
	
	
	/**
	 * 判断粉丝存不存在，存在的话返回fansId,不存在的话先插入在返回fansId
	 * @param openId
	 * @return
	 */
	public String getMINAFansId(String openId,Integer minaId) {
		//返回值初始化
		String returnFansId="";
		//判断粉丝存在不存在
		VicmobFansInfo vicmobFansInfo=new VicmobFansInfo();//实体类
		VicmobFansInfo vicmobFansInfoNew=new VicmobFansInfo();//返回值接收实体类
		vicmobFansInfo.setOpenId(openId);
		vicmobFansInfo.setMinaId(minaId);
		//查询openId在不在
		vicmobFansInfoNew=vicmobFansInfoService.getFindList(vicmobFansInfo);
		//粉丝存在
		if(vicmobFansInfoNew !=null){
			
			returnFansId=String.valueOf(vicmobFansInfoNew.getFansId());//返回fansId
		}else{//粉丝不存在
			//插入一条新的粉丝信息
			
			vicmobFansInfo.setAddtime(new Date());
		    Integer fansId	= vicmobFansInfoService.saveData(vicmobFansInfo);
		    returnFansId=String.valueOf(fansId);//返回fansId
		}
		return returnFansId;
	}

	/**
	 * 获取小程序APPID,appSecret
	 * @param minaId
	 * @return
	 */
	public MinaInfo getMINAAppId(Integer minaId){
		
		MinaInfo minaInfo=null;
		minaInfo=minaInfoService.getMessageByminaId(minaId);
		return minaInfo;
	}
	
	/**
	 * 获取最新的授权账号的component_accessToken
	 * @param minaId
	 * @return
	 * @throws WexinReqException 
	 */
	public String getComponentMessage(Integer minaId) throws WexinReqException{
		
		 //初始化
		 String component_access_token="";
		 
		 //获取accessToken的微信接口参数实体
		 ApiComponentToken apiComponentToken = new ApiComponentToken();  
	     apiComponentToken.setComponent_appid(COMPONENT_APPID);  
	     apiComponentToken.setComponent_appsecret(COMPONENT_APPSECRET); 
	     //获取授权账号信息
	     WeixinOpenAccount  entity = getWeixinOpenAccount(COMPONENT_APPID);  
	     apiComponentToken.setComponent_verify_ticket(entity.getTicket());
	     System.out.println();
	     System.out.println();
	     System.out.println();
	     component_access_token = entity.getComponentAccessToken();
	     Integer component_access_token_endtime = entity.getComponentAccessTokenEndtime();
	     //判断授权账号的component_access_token有没有过期
	   	 if(((int)((System.currentTimeMillis())/1000)>=component_access_token_endtime)){//过期了
	   		 	//重新获取授权账号的accessToken
     			component_access_token = JwThirdAPI.getAccessToken(apiComponentToken);
     			Integer currentTime = (int)((System.currentTimeMillis())/1000+7200);
     			entity.setComponentAccessTokenEndtime(currentTime);
     			entity.setComponentAccessToken(component_access_token);
     			//保存授权账号的accessToken到数据库
     			weixinOpenAccountService.saveWeixinOpenAccount(entity);
	   	 }
	   	 
	   	 return component_access_token;
	}
	
	 /**
	  * 获取授权账号信息
	  * @param componentAppid
	  * @return
	  */
	public WeixinOpenAccount getWeixinOpenAccount(String componentAppid) {
		
		WeixinOpenAccount weixinOpenAccount = new WeixinOpenAccount();
		weixinOpenAccount.setComponentAppid(componentAppid);
		List<WeixinOpenAccount> weixinOpenAccountList = weixinOpenAccountService.findList(weixinOpenAccount);
		if (weixinOpenAccountList != null && weixinOpenAccountList.size() != 0) {
			weixinOpenAccount = weixinOpenAccountList.get(0);
		}
		return weixinOpenAccount;
	}

}