package com.bazra.usermanagement.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Bank_document")
public class Bank_Document {
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;
	private String document;
	@ManyToOne
    @JoinColumn(name = "document_type_id")
	private Document_Type document_Type;
	@ManyToOne
    @JoinColumn(name = "bank_id")
	private Bank bank;
	private LocalDate created_date;
	@ManyToOne
    @JoinColumn(name = "creatorid")
	private AdminInfo creatorid;
	public int getId() {
		return id;
	}

	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}
	
	public LocalDate getCreated_date() {
		return created_date;
	}
	public void setCreated_date(LocalDate created_date) {
		this.created_date = created_date;
	}

	public Document_Type getDocument_Type() {
		return document_Type;
	}

	public void setDocument_Type(Document_Type document_Type) {
		this.document_Type = document_Type;
	}

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	public AdminInfo getCreatorid() {
		return creatorid;
	}

	public void setCreatorid(AdminInfo creatorid) {
		this.creatorid = creatorid;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	
	
}
