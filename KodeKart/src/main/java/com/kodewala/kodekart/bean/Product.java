package com.kodewala.kodekart.bean;

public class Product {

	private int pId;
	private String pName;
	private String pCategory;
	private double pPrice;
	private int pQuantity;
	private String pDescription;

	public Product() {
		// super();
	}

	public Product( String pName, String pCategory, double pPrice, int pQuantity, String pDescription) {
		// super();
		
		this.pName = pName;
		this.pCategory = pCategory;
		this.pPrice = pPrice;
		this.pQuantity = pQuantity;
		this.pDescription = pDescription;
	}

	

	public int getpId() {
		return pId;
	}

	public void setpId(int pId) {
		this.pId = pId;
	}

	public String getpName() {
		return pName;
	}

	public void setpName(String pName) {
		this.pName = pName;
	}

	public String getpCategory() {
		return pCategory;
	}

	public void setpCategory(String pCategory) {
		this.pCategory = pCategory;
	}

	public double getpPrice() {
		return pPrice;
	}

	public void setpPrice(double pPrice) {
		this.pPrice = pPrice;
	}

	public int getpQuantity() {
		return pQuantity;
	}

	public void setpQuantity(int pQuality) {
		this.pQuantity = pQuality;
	}

	public String getpDescription() {
		return pDescription;
	}

	public void setpDescription(String pDescription) {
		this.pDescription = pDescription;
	}

}
