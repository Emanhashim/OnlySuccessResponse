package com.bazra.usermanagement.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "localtransfer")
public class LocalTransfer {
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;
	private String receiverName;
	private String receiverPhone;
	private String amount;
	private String transactionCode;
	@ManyToOne
    @JoinColumn(name = "accountid")
	private Account fromAccount;
	private LocalDate transterDate;
	private LocalDateTime exiredDate;
	private Boolean ontime;
	private Boolean used;
	
	
	public Boolean getUsed() {
		return used;
	}

	public void setUsed(Boolean used) {
		this.used = used;
	}

	public LocalTransfer() {
		super();
		// TODO Auto-generated constructor stub
	}

//	public LocalTransfer(String receiverName, String receiverPhone, String amount, String pin) {
//		super();
//		this.receiverName = receiverName;
//		this.receiverPhone = receiverPhone;
//		this.amount = amount;
//		this.transaction_code = pin;
//		this.fromAccount = from;
//	}

	
	
	

	public int getId() {
		return id;
	}
	

	public String getReceiverName() {
		return receiverName;
	}
	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}
	public String getReceiverPhone() {
		return receiverPhone;
	}
	public void setReceiverPhone(String receiverPhone) {
		this.receiverPhone = receiverPhone;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}

	

	public String getTransactionCode() {
		return transactionCode;
	}

	public void setTransactionCode(String transactionCode) {
		this.transactionCode = transactionCode;
	}

	public Account getFromAccount() {
		return fromAccount;
	}

	public void setFromAccount(Account fromAccount) {
		this.fromAccount = fromAccount;
	}

	public LocalDate getTransterDate() {
		return transterDate;
	}

	public void setTransterDate(LocalDate transterDate) {
		this.transterDate = transterDate;
	}

	public LocalDateTime getExiredDate() {
		return exiredDate;
	}

	public void setExiredDate(LocalDateTime exiredDate) {
		this.exiredDate = exiredDate;
	}

	public Boolean getOntime() {
		return ontime;
	}

	public void setOntime(Boolean ontime) {
		this.ontime = ontime;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	
	

}
