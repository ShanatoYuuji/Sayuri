package com.sayuri.service;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.sayuri.entity.Blog;
import com.sayuri.entity.Category;
import com.sayuri.entity.Product;
import com.sayuri.mapper.BlogMapper;

public class CategoryService {
	public static void main(String[] args) throws IOException {
		String resource = "Mybatis/mybatis-config.xml";
//		String path =CategoryService.class.getResource("/").getPath();
//		System.out.println(path);
//		CategoryService.class.getClassLoader().getResourceAsStream(resource);
		InputStream inputStream = Resources.getResourceAsStream(resource);
//		InputStream inputStream =CategoryService.class.getClassLoader().getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		SqlSession session = sqlSessionFactory.openSession();
		try {
			BlogMapper mapper = session.getMapper(BlogMapper.class);
			Category blog = mapper.selectBlog(1);
			System.out.println(blog.getName());
			List<Category> lists=mapper.selectAll();
			for (Category category : lists) {
				System.out.println(category.getName());
			}
			int sum=mapper.selectAll2(2);
			System.out.println(sum);
//			Map<String, String> mapselect=mapper.selectmap();
//			System.out.println(mapselect.size());
			Blog blog2=mapper.selectBlog2(2);
			blog2.getProduct().getPrice();
			System.out.println(blog2.getProduct().getVer());
			Blog productlst=mapper.selectBlog3(2);
			for (Product product : productlst.getProductlst()) {
				System.out.println(product.getName());
			}
			
		}finally {
			session.close();
		}
	}
}
