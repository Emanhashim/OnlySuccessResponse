package com.bazra.usermanagement.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@SequenceGenerator(name = "seq", initialValue = 1000, allocationSize = 100)
@Entity
@Table(name = "account_type")
@EntityListeners(AuditingEntityListener.class)
public class AccountType {
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int id;
	private String accounttype;
	private LocalDate createdDateTime;
	@ManyToOne
    @JoinColumn(name = "creator_id")
    private AdminInfo adminInfo;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getAccounttype() {
		return accounttype;
	}
	public void setAccounttype(String accounttype) {
		this.accounttype = accounttype;
	}
	public LocalDate getCreatedDateTime() {
		return createdDateTime;
	}
	public void setCreatedDateTime(LocalDate createdDateTime) {
		this.createdDateTime = createdDateTime;
	}
	public AdminInfo getAdminInfo() {
		return adminInfo;
	}
	public void setAdminInfo(AdminInfo adminInfo) {
		this.adminInfo = adminInfo;
	}
	
	
	
	
	
}
