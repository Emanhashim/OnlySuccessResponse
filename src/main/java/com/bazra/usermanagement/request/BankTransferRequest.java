package com.bazra.usermanagement.request;

import javax.validation.constraints.NotBlank;

public class BankTransferRequest {
	@NotBlank
	private String name;
	@NotBlank
	private String accountNumber;
	@NotBlank
	private String amount;
	@NotBlank
	private String remark;
	
	
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	
	

}
