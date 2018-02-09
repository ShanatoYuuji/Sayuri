package com.sayuri.mapper;


import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Select;
import org.mybatis.spring.annotation.MapperScan;

import com.sayuri.entity.Blog;
import com.sayuri.entity.Category;

//@MapperScan
public interface BlogMapper {
//	 @Select("SELECT * FROM category WHERE id = #{id}")
	  Category selectBlog(int id);
	  @Select("call selectCategory()")
	  List<Category> selectAll();
	  @Select("call selectCategory2(#{id})")
	  int selectAll2(int id);
//	  Map<String,String> selectmap();
	  Blog selectBlog2(int id);
	  Blog selectBlog3(int id);
}
