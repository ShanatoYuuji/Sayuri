package com.sayuri.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sayuri.service.CategoryService2;

@Controller
public class HelloController {
	@Resource
	private CategoryService2 categoryService;
	
	@RequestMapping("/hello")
	@ResponseBody
	public String test() {
//		System.out.println(categoryService.selectbyId(1).getName());
		return "hello,world! This come from spring!";
	}
}
