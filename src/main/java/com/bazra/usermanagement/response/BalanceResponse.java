package com.bazra.usermanagement.response;

import java.math.BigDecimal;

import com.bazra.usermanagement.model.UserInfo;

public class BalanceResponse {
    private BigDecimal balance;
    private String message;
    private UserInfo userInfo;
    private BigDecimal amount;
    
    
    
	public UserInfo getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal balance) {
        this.balance = balance;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	
	public BalanceResponse( BigDecimal balance, String message, UserInfo userInfo,BigDecimal amount) {
    	
        this.userInfo=userInfo;
        this.balance = balance;
        this.message= message;
        this.amount= amount;
       }
    public BalanceResponse(String message) {
    	
        this.message=message;
    }
}
