package com.sayuri.service;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sayuri.entity.Category;
import com.sayuri.mapper.BlogMapper;

@Service
public class CategoryService2 {
	@Autowired
	private BlogMapper blogMapper;
	
	public Category selectbyId(int id) {
		return blogMapper.selectBlog(id);
	}
}
