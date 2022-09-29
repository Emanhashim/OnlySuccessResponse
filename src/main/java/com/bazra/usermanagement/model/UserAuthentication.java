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
@Table(name = "User_Authentication")
public class UserAuthentication {
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;
	
	private String authenticationValue;
	
	@ManyToOne
	@JoinColumn(name = "authenticationType_id")
	private AuthenticationType authenticationType;
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
	public String getAuthenticationValue() {
		return authenticationValue;
	}
	public void setAuthenticationValue(String authenticationValue) {
		this.authenticationValue = authenticationValue;
	}
	public AuthenticationType getAuthenticationType() {
		return authenticationType;
	}
	public void setAuthenticationType(AuthenticationType authenticationType) {
		this.authenticationType = authenticationType;
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
