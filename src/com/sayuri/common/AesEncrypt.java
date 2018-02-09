package com.sayuri.common;

import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
/**
 * AES加密和解密工具,可以对字符串进行加密和解密操作 
 * 
 *
 */
public class AesEncrypt {

	/**
	 * AES 加密
	 * @param sSrc 需要解密的字符串
	 * @param sKey 秘钥（ 此处使用AES-128-ECB加密模式，key需要为16位。）
	 * @return
	 * @throws Exception
	 */
    public static String Encrypt(String sSrc, String sKey){
        if (sKey == null) {
            System.out.print("Key为空null");
            return null;
        }
        // 判断Key是否为16位
        if (sKey.length() != 16) {
            System.out.print("Key长度不是16位");
            return null;
        }
        byte[] raw;
		try {
			raw = sKey.getBytes("utf-8");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");//"算法/模式/补码方式"
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
			
			return new Base64().encodeToString(encrypted);//此处使用BASE64做转码功能，同时能起到2次加密的作用。
			
		} catch (Exception e) {
			System.out.println(e.toString());
            return "";
		}
 
    }
 
    /**
     * AES解密
     * @param sSrc 解密的字符串
     * @param sKey 秘钥
     * @return
     * @throws Exception
     */
    public static String Decrypt(String sSrc, String sKey) {
        try {
            // 判断Key是否正确
            if (sKey == null) {
                System.out.print("Key为空null");
                return "";
            }
            // 判断Key是否为16位
            if (sKey.length() != 16) {
                System.out.print("Key长度不是16位");
                return "";
            }
            byte[] raw = sKey.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted1 = new Base64().decode(sSrc);//先用base64解密
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original,"utf-8");
                return originalString;
            } catch (Exception e) {
                System.out.println(e.toString());
                return "";
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return "";
        }
    }
    
    /**
     * 通过appId加密数据minaId
     * @param appId 公众号appId
     * @param minaId 小程序Id
     * @return enString 加密后的小程序Id
     * @throws Exception
     */
    public static String aesEncryption(String appId,String minaId){
    	//截取appId的前16位
    	String aesKey=appId.substring(0, 16);
    	String enString="";
		try {
			enString = AesEncrypt.Encrypt(minaId, aesKey);
		} catch (Exception e) {
			System.out.println(e.toString());
			enString = "";
		}
        System.out.println("加密后的字串是：" + enString);
        return enString;
    }
    
    /**
     * 
     * @param appId 小程序Id
     * @param minaIdAes 加密后的小程序Id
     * @return deString 解密后的小程序Id
     * @throws Exception
     */
    public static String aesDecryption(String appId,String minaIdAes) {
    	//截取appId的前16位
    	String aesKey=appId.substring(0, 16);
    	String deString="";
		try {
			deString = AesEncrypt.Decrypt(minaIdAes, aesKey);
		} catch (Exception e) {
			System.out.println(e.toString());
			deString = "";
		}
    	System.out.println("解密后的字串是：" + deString);
    	return deString;
    }
    
    public static void main(String[] args) throws Exception {
        /*
         * 此处使用AES-128-ECB加密模式，key需要为16位。
         */
//        String cKey = aesKeyForAppId("wx9f8c311115767d63");
//        // 需要加密的字串
//        String cSrc = "3";
//        System.out.println(cSrc);
//        // 加密
//        String enString = AesEncrypt.Encrypt(cSrc, cKey);
//        System.out.println("加密后的字串是：" + enString);
        //qkrxxA9fIF636aITDRJhcg==
        		//8wSsnxdaZefSmdEvG4ahCSG0zIhu+HSsoBIdzA3KEiE=
    	
    	String aa=AesEncrypt.aesEncryption("wx9f8c311115767d63","2");
    	AesEncrypt.aesDecryption("wx9f8c311115767d63",aa);
//    	//xufjefN5GaW5WGKvk24fag==
//        // 解密
//        String DeString = AesEncrypt.Decrypt("4Ebe2/FxD15wgxUllwy0Sg==", cKey);
//        if(DeString.isEmpty()){
//        	System.out.println("11");
//        }
//        System.out.println("解密后的字串是：" + DeString);
    }


}
