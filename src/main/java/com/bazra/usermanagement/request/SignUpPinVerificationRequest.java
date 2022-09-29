package com.bazra.usermanagement.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class SignUpPinVerificationRequest {
	@NotBlank
    @Size(min = 9, message = "Phone must be at least 9 characters")
    private String phone;
	private String pin;
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPin() {
		return pin;
	}
	public void setPin(String pin) {
		this.pin = pin;
	}
	
}
