package com.bazra.usermanagement.request;

import javax.validation.constraints.NotBlank;

public class CreateAddressElementRequest {
	@NotBlank
	private String title;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	

}
