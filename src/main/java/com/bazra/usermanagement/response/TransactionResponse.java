package com.bazra.usermanagement.response;

import java.util.ArrayList;
import java.util.List;

import com.bazra.usermanagement.model.Account;
import com.bazra.usermanagement.model.Transaction;

public class TransactionResponse {
//	@JsonProperty(value = "summary", required = true)
//    private List<Account> accounts;
	private List<Transaction> transaction;
	private List<Account> accounts;

	public List<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}

	// public List<Account> getAccounts() {
//		return accounts;
//	}
//	public void setAccounts(List<Account> accounts) {
//		this.accounts = accounts;
//	}
	public List<Transaction> getTransaction() {
		return transaction;
	}

	public void setTransaction(List<Transaction> transaction) {
		this.transaction = transaction;
	}

	public TransactionResponse(List<Transaction> transaction, List<Transaction> transaction2) {
		List<Transaction> stringArrayList = new ArrayList<Transaction>();
		stringArrayList.addAll(transaction);
		stringArrayList.addAll(transaction2);

		this.transaction = stringArrayList;

	}
//
    public TransactionResponse(List<Transaction> accounts) {
    	List<Transaction> stringArrayList = new ArrayList<Transaction>();
    	stringArrayList.addAll(accounts);
//        stringArrayList.addAll(transaction2);
        
        this.transaction= stringArrayList;
        
        
    }


}
