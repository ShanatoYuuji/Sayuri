
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

import com.sayuri.common.WxCommonUtil;



public class test {
	public static void main(String[] args) throws IOException {
		
//		获取access_token();
//		System.out.println(获取16位随机数());
		
		//用户标签管理接口 没有授权
//		String result=WxCommonUtil.httpsRequestString("https://api.weixin.qq.com/cgi-bin/tags/create?access_token=e3BEcKtNBcdJZEdFU_EG3WJzU-TXqurijv4yJ7ENmk2LCuOGOlyoRyHinkQy1pc19n3-4L7itDoS8LFDY02mcli4DEdEEWvSEPeIv_xV4oiCvhOtKEn_1WqW2EKQKTd9TEYeAGAYJD", "POST", "{'tag':{'name':'广东'}}");
//		System.out.println(result);
		
		//获得access_token
//		net.sf.json.JSONObject json= 获取access_token_util();
//		System.out.println(json.toString());
		
		JSONObject jsonobject= 上传多媒体接口("image","C:\\Users\\admin\\Desktop\\8913281_p0_master1200.jpg");
		System.out.println(jsonobject.toString());
	}
	
	private static void 获取access_token() throws IOException {
		String str="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={0}&secret={1}";
		String id="wx3390c7fbd7717570";
		String pass="5e835f530c88ba2ef76b0a9e80db71fd";
		String urlStr=MessageFormat.format(str, id,pass);
		URL realUrl=new URL(urlStr);
		URLConnection connection=realUrl.openConnection();
	     // 建立实际的连接
	     connection.connect();
		BufferedReader in=new BufferedReader(new InputStreamReader(connection.getInputStream())) ;
		String result=""; 
		String line;
		 while((line=in.readLine())!=null) {
			result=result+line; 
		 }
		 System.out.println(result);
	}
	
	private static net.sf.json.JSONObject 获取access_token_util() {
		String str="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={0}&secret={1}";
		String id="wx3390c7fbd7717570";
		String pass="5e835f530c88ba2ef76b0a9e80db71fd";
		String urlStr=MessageFormat.format(str, id,pass);
		net.sf.json.JSONObject json=WxCommonUtil.httpsRequest(urlStr, "GET", "");
		return json;
	}
	
	private static String 获取16位随机数() {
		char[] chars="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
		String result="";
		for(int i=0;i<16;i++) {
			Random rd=new Random();
			result=result+chars[rd.nextInt(chars.length-1)];
		}
		return result;
	}
	
	private static JSONObject 上传多媒体接口(String fileType,String filePath) throws IOException {
		String result=null;
		File file=new File(filePath);
		if(!file.exists()||!file.isFile()) {
			throw new IOException("文件不存在");
		}
		//上传图片
		URL urlObj=new URL("http://file.api.weixin.qq.com/cgi-bin/media/upload?access_token=JoX0P3ECHomt2lbJX9MPywfiTtGDQ0wPS1eEooHZzNa4kKllRiQJly_8rikP7bNH4-4Xah4B3bftORnGDVNeyIi-YcDnlhlwWiSay7-FtBgz9PFbqrUHV0P2xJ0TXpdZOCDdAFADRS&type=image");
		HttpURLConnection con=(HttpURLConnection)urlObj.openConnection();
		con.setRequestMethod("POST");
		con.setDoInput(true);
		con.setDoOutput(true);
		con.setUseCaches(false);
		//设置请求头信息
		con.setRequestProperty("Connection","Keep-Alive");
		con.setRequestProperty("Charset","UTF-8");
		//设置边界
		String BOUNDARY="--------"+System.currentTimeMillis();
		con.setRequestProperty("Content-Type", "multipart/form-data; boundary="+BOUNDARY);
		//请求正文信息
		//第一部分
		StringBuilder sb=new StringBuilder();
		sb.append("--");
		sb.append(BOUNDARY);
		sb.append("\r\n");
		sb.append("Content-Disposition: form-data;name=\"file\";filename=\""+file.getName()+"\"\r\n");
		sb.append("Content-Type:application/octet-stream\r\n\r\n");
		byte[] head=sb.toString().getBytes("utf-8");
		//获得输出流
		OutputStream out=new DataOutputStream(con.getOutputStream());
		//输出表头
		out.write(head);
		//文件正文部分
		//把文件以流文件的方式推入url中
		DataInputStream in=new DataInputStream(new FileInputStream(file));
		int bytes=0;
		byte[] bufferOut=new byte[1024];
		while ((bytes=in.read(bufferOut))!=-1) {
			out.write(bufferOut,0,bytes);
		}
		in.close();
		//结尾部分
		byte[] foot=("\r\n--"+BOUNDARY+"--\r\n").getBytes("utf-8");//最后定义数据分割线
		out.write(foot);
		out.flush();
		out.close();
		StringBuffer buffer=new StringBuffer();
		BufferedReader reader=null;
		try {
			//定义BufferedReader输入流来读取URL的响应
			reader=new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line=null;
			while((line=reader.readLine())!=null) {
				buffer.append(line);
			}
			if(result==null) {
				result=buffer.toString();
			}
		}catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			if(reader!=null) {
				reader.close();
			}
		}
		JSONObject jsonObj=new JSONObject(result);
		return jsonObj;
	}
	
}
