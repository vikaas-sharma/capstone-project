package com.example.transaction_service.dto;

//UserDTO.java
public class UserDTO {
	
	private Integer userId;
	 private String username;
	 private String address;
	 // getters and setters
	 
 public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
 
}
