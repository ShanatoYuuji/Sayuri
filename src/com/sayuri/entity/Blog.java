package com.sayuri.entity;

import java.util.List;

public class Blog {
	int id;
	String name;
	Product product;
	List<Product> productlst;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public List<Product> getProductlst() {
		return productlst;
	}
	public void setProductlst(List<Product> productlst) {
		this.productlst = productlst;
	}
	
}
