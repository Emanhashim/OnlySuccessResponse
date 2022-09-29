package com.bazra.usermanagement.request;

import javax.validation.constraints.NotBlank;

public class CreateAccountTypeRequest {
	@NotBlank
	private String accounttype;

	public String getAccounttype() {
		return accounttype;
	}

	public void setAccounttype(String accounttype) {
		this.accounttype = accounttype;
	}
	
	
}
