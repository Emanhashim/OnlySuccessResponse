package com.bazra.usermanagement.response;

import java.math.BigDecimal;

import com.bazra.usermanagement.model.UserInfo;

public class CommissionResponse {

	private BigDecimal commission;
	private String message;
	private UserInfo username;
	private BigDecimal amount;

	public CommissionResponse(BigDecimal commission, String message, UserInfo username,BigDecimal amount) {

		this.username = username;
		this.commission = commission;
		this.message = message;
		this.amount = amount;
	}
	

	public BigDecimal getCommission() {
		return commission;
	}


	public void setCommission(BigDecimal commission) {
		this.commission = commission;
	}


	public CommissionResponse(String message) {
		this.message = message;
	}

	public UserInfo getUsername() {
		return username;
	}

	public void setUsername(UserInfo username) {
		this.username = username;
	}


	public BigDecimal getAmount() {
		return amount;
	}


	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}


	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
