package com.kodewala.kodekart.bean;

public class OrderItem {

	private int orderItemId;
	private int orderId;
	private int productId;
	private int orderItemQuentity;
	private double orderItemPrice;
	private Product product;

	public int getOrderItemId() {
		return orderItemId;
	}

	public void setOrderItemId(int orderItemId) {
		this.orderItemId = orderItemId;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public int getOrderItemQuentity() {
		return orderItemQuentity;
	}

	public void setOrderItemQuentity(int orderItemQuentity) {
		this.orderItemQuentity = orderItemQuentity;
	}

	public double getOrderItemPrice() {
		return orderItemPrice;
	}

	public void setOrderItemPrice(double d) {
		this.orderItemPrice = d;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

}
