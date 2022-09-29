package com.bazra.usermanagement.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "User_Document")
public class User_Document {
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int id;
	@ManyToOne
    @JoinColumn(name = "userid")
	private UserInfo userInfo;
	private String documentPath;
	private boolean is_valid;
	@ManyToOne
    @JoinColumn(name = "confirmedBy")
	private AdminInfo confirmedBy;
	private LocalDate confirmedDate;
	private LocalDate uploadeDate;
	@ManyToOne
    @JoinColumn(name = "document_type_id")
	private Document_Type documentType;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public UserInfo getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	public String getDocumentPath() {
		return documentPath;
	}
	public void setDocumentPath(String documentPath) {
		this.documentPath = documentPath;
	}
	public boolean isIs_valid() {
		return is_valid;
	}
	public void setIs_valid(boolean is_valid) {
		this.is_valid = is_valid;
	}
	public AdminInfo getConfirmedBy() {
		return confirmedBy;
	}
	public void setConfirmedBy(AdminInfo confirmedBy) {
		this.confirmedBy = confirmedBy;
	}
	public LocalDate getConfirmedDate() {
		return confirmedDate;
	}
	public void setConfirmedDate(LocalDate confirmedDate) {
		this.confirmedDate = confirmedDate;
	}
	public LocalDate getUploadeDate() {
		return uploadeDate;
	}
	public void setUploadeDate(LocalDate uploadeDate) {
		this.uploadeDate = uploadeDate;
	}
	public Document_Type getDocumentType() {
		return documentType;
	}
	public void setDocumentType(Document_Type documentType) {
		this.documentType = documentType;
	}
	
	
	
}
