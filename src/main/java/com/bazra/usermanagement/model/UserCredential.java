package com.bazra.usermanagement.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "User_Credential")
public class UserCredential {
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;
	
	private String loginID;
	private boolean isActive;
	@ManyToOne
	@JoinColumn(name = "loginIDType")
	private LoginIDType loginIDType;
	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserInfo userInfo;
	private LocalDate localDate;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLoginID() {
		return loginID;
	}
	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public LoginIDType getLoginIDType() {
		return loginIDType;
	}
	public void setLoginIDType(LoginIDType loginIDType) {
		this.loginIDType = loginIDType;
	}
	public UserInfo getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	public LocalDate getLocalDate() {
		return localDate;
	}
	public void setLocalDate(LocalDate localDate) {
		this.localDate = localDate;
	}
	
	
}
