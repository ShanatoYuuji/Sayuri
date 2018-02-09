package com.sayuri.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jeeplus.common.web.BaseController;
import com.jeeplus.modules.sys.entity.MinaInfo;
import com.jeeplus.modules.sys.service.MinaInfoService;
import com.jeeplus.modules.sys.utils.ConfigurationFileHelper;
import com.jeeplus.modules.tools.utils.UtilsZip;

import net.sf.json.JSONObject;

@Controller
@RequestMapping(value = "vicmob/zip")
public class MINAZipDown extends BaseController{
	
	@Autowired
	private MinaInfoService minaInfoService;
	
	//下载的标志
	private static final String restaurant_type="1";
	private static final String vehicle_type="2";
	private static final String hotel_type="3";
	private static final String estate_type="4";
	private static final String shop_type="5";
	private static final String mall_type="6";
	
	/**
	 * 通过主键minaid获取小程序
	 */
	@RequestMapping(value = "/findMina",method = RequestMethod.POST)
	@ResponseBody
	public MinaInfo findMina(HttpServletRequest request) {
		MinaInfo minaInfo = minaInfoService.findEntityById(Integer.parseInt(request.getParameter("minaId")));
		return minaInfo;
	}

	@RequestMapping(value = "/message", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject zipDownFile(HttpServletRequest request) throws Exception{
		
		//页面获取
		HttpSession session =  request.getSession();
		String appId = (String) session.getAttribute("mina_appId");
		String minaId = (String) session.getAttribute("minaId");
		String type=request.getParameter("mina_type");
		// 第1步、小程序(appId,minaId)配置文件js的路径
		//判断type决定是哪个小程序，进入不同小程序文件
		
		String mina_path="";//小程序文件路径
		String minaConfig_path="";//小程序配置文件路径
		String minaZipFile_path="";//小程序压缩文件存放路径
		String minaName="";//小程序压缩后名字的其中组成部分(小程序类型名称，比如，微房产，微汽修等)
		if(type.equals(restaurant_type)){
			mina_path = ConfigurationFileHelper.getRestaurantMINA();
		    minaConfig_path = ConfigurationFileHelper.getRestaurantMINAConfig();
		    minaZipFile_path = ConfigurationFileHelper.getRestaurantZipFile();
		    minaName="restaurant";
		}else if(type.equals(vehicle_type)){
			mina_path = ConfigurationFileHelper.getVehicleMINA();
			minaConfig_path = ConfigurationFileHelper.getVehicleMINAConfig();
			minaZipFile_path = ConfigurationFileHelper.getVehicleZipFile();
			minaName="vehicle";
		}else if(type.equals(hotel_type)){
			mina_path = ConfigurationFileHelper.getHotelMINA();
			minaConfig_path = ConfigurationFileHelper.getHotelMINAConfig();
			minaZipFile_path = ConfigurationFileHelper.getHotelZipFile();
			minaName="hotel";
		}else if(type.equals(estate_type)){
			mina_path = ConfigurationFileHelper.getEstateMINA();
			minaConfig_path = ConfigurationFileHelper.getEstateMINAConfig();
			minaZipFile_path = ConfigurationFileHelper.getEstateZipFile();
			minaName="estate";
		} else if(type.equals(shop_type)) {
			mina_path = ConfigurationFileHelper.getShopMINA();
			minaConfig_path = ConfigurationFileHelper.getShopMINAConfig();
			minaZipFile_path = ConfigurationFileHelper.getShopZipFile();
			minaName="shop";
		}else if(type.equals(mall_type)){
			mina_path = ConfigurationFileHelper.getMallMINA();
			minaConfig_path = ConfigurationFileHelper.getMallMINAConfig();
			minaZipFile_path = ConfigurationFileHelper.getMallZipFile();
			minaName="mall";
		}
		
		if( ! minaId.equals("") && minaId !=null && ! appId.equals("") && appId !=null){
			
			File minaPath=new File(mina_path);//小程序文件路径
			File minaConfigPath=new File(minaConfig_path);//小程序配置文件路径
			// 加锁，在同一时间只能一个人执行该方法
			String zipUrlReturn=getZipUrl(minaConfigPath,appId,minaId,type,minaPath,minaZipFile_path,minaName);
			JSONObject jsonObject=new JSONObject();
			jsonObject.put("zipUrl", zipUrlReturn);
			
			return jsonObject;
		}
		return null;
		
	}
	/**
	 * 加锁，在同一时间只能一个人执行该方法
	 * @param minaConfigPath
	 * @param appId
	 * @param minaId
	 * @param type
	 * @param minaPath
	 * @param minaZipFile_path
	 * @param minaName
	 * @return
	 * @throws Exception
	 */
	public synchronized String getZipUrl(File minaConfigPath, String appId, String minaId, String type, File minaPath, String minaZipFile_path, String minaName) throws Exception{
		
		//通过minaId获取小程序昵称
		String minaNickName="";
		MinaInfo minaInfo = minaInfoService.findEntityById(Integer.parseInt(minaId));
		if(minaInfo.getNickName() !=null && ! "".equals(minaInfo.getNickName()) &&  ! minaInfo.getNickName().equals("null")) {
			minaNickName=minaInfo.getNickName();
		}
		
		pasteNewJs(minaConfigPath,appId,minaId,type,minaNickName);//修改配置文件
		String zipUrl=zipDown(minaPath,minaZipFile_path,minaId,minaName);//下载到本地并压缩成zip
		
		return zipUrl;
	}
	
	/**
	 * 修改配置文件
	 * @param type 
	 * @param minaId2 
	 * @param appId2 
	 * @param fileout
	 * @throws Exception 
	 */
	public void pasteNewJs(File configFile, String appId, String minaId, String type,String minaNickName) throws Exception{
		
		//对minaId进行加密
		String minaIdAes=AesEncrypt.aesEncryption(appId, minaId);
		  
		OutputStream outStr=null;
		try {
		    //输出流
		    outStr=new FileOutputStream(configFile);
		   
			JSONObject rejsonObject = new JSONObject();
			//往配置文件里面写入的值
			String moduleStr="module.exports";
			String dreamStr="";
			if(type.equals(estate_type) || type.equals(hotel_type) || type.equals(mall_type) || type.equals(restaurant_type) || type.equals(shop_type) || type.equals(vehicle_type)){
				rejsonObject.put("appId", appId);
				rejsonObject.put("minaId", minaIdAes);				
				rejsonObject.put("minaNickName", minaNickName);				
				//往配置文件里面写入的值
				dreamStr=moduleStr+" = "+rejsonObject.toString();
				dreamStr=dreamStr.replace("\"appId\"", "appId");
				dreamStr=dreamStr.replace("\"minaId\"", "minaId");
				dreamStr=dreamStr.replace("\"minaNickName\"", "minaNickName");
			}else{
				rejsonObject.put("appId", appId);
				rejsonObject.put("minaId", minaId);				
				rejsonObject.put("minaNickName", minaNickName);				
				//往配置文件里面写入的值
				dreamStr=moduleStr+" = "+rejsonObject.toString();
				dreamStr=dreamStr.replace("\"appId\"", "appId");
				dreamStr=dreamStr.replace("\"minaId\"", "minaId");
				dreamStr=dreamStr.replace("\"minaNickName\"", "minaNickName");
			}
			
			if (dreamStr != null) {
				outStr.write(dreamStr.getBytes("utf-8"));
			}
		}catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(outStr!=null){
				try {
					outStr.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * 下载到指定地方并压缩成zip
	 * @param fileMINA
	 * @param minaId 
	 * @param appId 
	 * @param minaZipFile_path 
	 * @param minaName 
	 * @throws Exception 
	 */
	public String zipDown(File fileMINA, String minaZipFile_path,String minaId, String minaName) throws Exception{
		
		UtilsZip utilsZip=new UtilsZip();
		//选择要复制到的地址（zip）
		File dirFile=new File(minaZipFile_path);
		if(!dirFile.exists()){//文件路径不存在时，自动创建目录
			dirFile.mkdir();
		}
		//压缩后的文件名
		String zipName="MINA_"+minaName+"_"+minaId+".zip";
		String zipFileName=dirFile+"//"+zipName;
		//工具类复制为Zip文件
		utilsZip.zip(zipFileName, fileMINA);
		//当前项目路径
		String path = ConfigurationFileHelper.getServiceUrl();
		
		String returnName=path+minaZipFile_path.split("webapps")[1]+"//"+zipName;//重组后的zip路径（复制后的路径）
		
		return returnName;
		
	}
	
}
