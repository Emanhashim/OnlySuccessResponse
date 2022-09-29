package com.bazra.usermanagement.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class SignUpPinRequest {
	@NotBlank
    @Size(min = 9, message = "Phone must be at least 9 characters")
    private String phone;

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	} 
}
