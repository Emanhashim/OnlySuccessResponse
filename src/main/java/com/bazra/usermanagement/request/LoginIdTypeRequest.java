package com.bazra.usermanagement.request;

import javax.validation.constraints.NotBlank;

public class LoginIdTypeRequest {
	
	@NotBlank
	private String loginType;
	private String description;
	public String getLoginType() {
		return loginType;
	}
	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	

}
