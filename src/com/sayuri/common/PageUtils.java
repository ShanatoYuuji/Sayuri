package com.sayuri.common;

import java.util.ArrayList;
import java.util.List;
/**
 * 四个入参，要按顺序填好：每页显示列表记录数，当前页码，要展示的集合，每页要显示的页码数量
 * 返回类型List.Object
 * @author LiWenTao
 *
 */

public class PageUtils {
	private static Integer pageSize;//每页的记录数
	private static Integer pageShowed;//每页要显示的
	
	//四个入参，要按顺序填好：每页显示列表记录数，当前页码，要展示的集合，每页要显示的页码数量
	public static List<Object> page(Integer pageSizeGet,String currentPagefront,List list,Integer pageShowedGet ) {
		pageSize = pageSizeGet;//每页的列表记录数
		pageShowed = pageShowedGet;//每页要显示的页码数量，最大10
		Integer recordCount; //数据库查询到的总记录数
		Integer beginPageIndex;//首页
		Integer endPageIndex;//尾页
		Integer currentPage;//当前页
		Integer pageCount;//计算出一共要分多少页
		
		//list是查好传进来的数据集合，如果是门店，就是门店findList()查出的结果
		recordCount = list.size();//数据库查询到的总记录数
		currentPage = Integer.parseInt(currentPagefront);//当前页
		pageCount = (recordCount+pageSize-1)/pageSize;//计算出一共要分多少页
		//前台我做了处理，当点击尾页时，传过来的currentPagefront为-1，那么做判断，是不是-1，是-1，则强行将currentPage配置成pageCount
		if(currentPage==-1){
			currentPage=pageCount;
		}
		
		/**
		 * 当数据量特别大时，页码量比较大，则不能在页面下方显示所有的页码
		 * 就必须计算页面下方动态显示的页码数量
		 * 计算页面下方显示的页码是第几个到第几个
		 */
		Integer n = null;//计算当前页码左边第一个页码数字
		Integer m = null;//计算当前页面右边最后一个页码数字
		if(pageShowed%2==0){
			n=pageShowed/2-1;
			m=pageShowed/2;
		}else{
			n=pageShowed/2;
			m=pageShowed/2;
		}
		
		if(pageCount<=pageShowed){
			beginPageIndex = 1;
			endPageIndex = pageCount;
		}else{
			beginPageIndex = currentPage-n;
			endPageIndex = currentPage+m;
			if(beginPageIndex<1){
				beginPageIndex = 1;
				endPageIndex = pageShowed;
			}
			if(endPageIndex>pageCount){
				endPageIndex = pageCount;
				beginPageIndex = pageCount-pageShowed+1;
			}
		}
		
		/*
		 * 上方获取到当前页码以及beginPageIndex和endPageIndex，
		 * 就可以算出当前页要显示的页码数字是多少到多少了，然后把所有数
		 * 字填充到一个list中传到前端来动态展示点击的页面显示的页码
		 */
		Integer i = 0;
		List<Integer> pageNum = new ArrayList<Integer>();//出参顺序三
		pageNum.add(beginPageIndex);
		for(i=0;beginPageIndex<endPageIndex;i++){
			beginPageIndex++;
			pageNum.add(beginPageIndex);
		}
		
		/**
		 * 上方获取到每页需要展示的页码之后，动态填充出相应带有数字的页码button，在点击时，
		 * 触发方法传回页码，就可以算出对应页面需要展示的数据了，下面计算的是点击的页面所需
		 * 展示的第一条数据的序号，和最后一条的序号
		 */
		Integer start = (currentPage-1)*pageSize+1-1;//起始记录数，最后的减一是因为List是从0算起
		/*
		 * 结尾记录数，用来于总记录数座比较，若以每页显示10条的标准，
		 * 算出当前页应当展示的最后一条记录是总记录的第多少条，如果当
		 * 前页需要展示的最后一条记录的序号超出了总记录数，则只显示，
		 * 由当前页start开始到总记录最后一条的会员
		 * 
		 */
		Integer end = start+1*pageSize-1;
		//判断end与总记录数的关系
		List curList = new ArrayList<>();//出参顺序一
		if((end+1)<=list.size()){
			for(i=0;start<=end;i++){
				curList.add(list.get(start));
				start++;
			}
		}else{
			for(i=0;start<list.size();i++){
				curList.add(list.get(start));
				start++;
			}
		}
		
		//处理首页尾页，上一页，下一页
		Integer back = null ;
		Integer ahead = null ; 
		
		if(currentPage-1<=0){
			back=1;
		}else{
			back = currentPage-1;
		}
		
		if(currentPage+1>=pageCount){
			ahead = pageCount;
		}else{
			ahead = currentPage+1;
		}
		//上一页
		List<Integer> up = new ArrayList<Integer>();//出参顺序二
		up.add(back);
		//下一页
		List<Integer> down = new ArrayList<Integer>();//出参顺序四
		down.add(ahead);
		
		/**
		 * 动态添加一共4个部分，首页和尾页不需要，固定位置，
		 * 分别是当前页需要加载的所有记录以列表形式展示，上一页，动态加载的页码部分，和下一页
		 * 前台在接收枚举时都是按顺序，所以在上方也写好的添加顺序
		 */
		List<Object> all = new ArrayList<Object>();
		all.add(curList);
		all.add(up);
		all.add(pageNum);
		all.add(down);
		
		return all;
	}

}
