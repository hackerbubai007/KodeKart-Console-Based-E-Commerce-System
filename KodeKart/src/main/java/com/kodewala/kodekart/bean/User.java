package com.kodewala.kodekart.bean;

public class User {

	private int uId;
	private String uName;
	private String uEmail;
	private String uPhone;
	private String uPassword;
	private boolean isAdmin;

	public User() {
		// super();
	}

	public User( String uName, String uEmail, String uPhone, String uPassword) {
		// super();
		
		this.uName = uName;
		this.uEmail = uEmail;
		this.uPhone = uPhone;
		this.uPassword = uPassword;
		this.isAdmin = false;
	}

	public int getuId() {
		return uId;
	}

	public void setuId(int uId) {
		this.uId = uId;
	}

	public String getuName() {
		return uName;
	}

	public void setuName(String uName) {
		this.uName = uName;
	}

	public String getuEmail() {
		return uEmail;
	}

	public void setuEmail(String uEmail) {
		this.uEmail = uEmail;
	}

	public String getuPhone() {
		return uPhone;
	}

	public void setuPhone(String uPhone) {
		this.uPhone = uPhone;
	}

	public String getuPassword() {
		return uPassword;
	}

	public void setuPassword(String uPassword) {
		this.uPassword = uPassword;
	}

	public boolean isAdmin() {
		return isAdmin;
	}
	public void setAdmin(boolean admin) {
		isAdmin=admin;
	}

}
