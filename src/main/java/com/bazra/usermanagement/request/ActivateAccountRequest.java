package com.bazra.usermanagement.request;

import javax.validation.constraints.NotBlank;

public class ActivateAccountRequest {
	@NotBlank
	private String DefaultPin;
	@NotBlank
	private String newpin;
	@NotBlank
	private String confirmpin;
	public String getDefaultPin() {
		return DefaultPin;
	}
	public void setDefaultPin(String defaultPin) {
		DefaultPin = defaultPin;
	}
	public String getNewpin() {
		return newpin;
	}
	public void setNewpin(String newpin) {
		this.newpin = newpin;
	}
	public String getConfirmpin() {
		return confirmpin;
	}
	public void setConfirmpin(String confirmpin) {
		this.confirmpin = confirmpin;
	}
	
	
	

}
