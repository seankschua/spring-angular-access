package com.exp.models;

public class SubLogin {
	
	private String email;
	private String password;
	private String device;
	
	public SubLogin(String email, String password, String device) {
		this.email = email;
		this.password = password;
		this.device = device;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	
	
	
}
