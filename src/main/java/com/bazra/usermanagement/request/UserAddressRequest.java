package com.bazra.usermanagement.request;

import javax.validation.constraints.NotBlank;

public class UserAddressRequest {
	@NotBlank
	private String address;
	@NotBlank
	private String addresstype;
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getAddresstype() {
		return addresstype;
	}
	public void setAddresstype(String addresstype) {
		this.addresstype = addresstype;
	}
	
	
	
	

}
