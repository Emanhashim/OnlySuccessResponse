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
@Table(name = "bank")
public class Bank {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int id;

	@Column(unique = true)
	private String name;

	private LocalDate created_date;

	@ManyToOne
	@JoinColumn(name = "creator_id")
	private AdminInfo admin;

	public Bank() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AdminInfo getAdmin() {
		return admin;
	}

	public void setAdmin(AdminInfo admin) {
		this.admin = admin;
	}

	public void setId(int id) {
		this.id = id;
	}

	public LocalDate getCreated_date() {
		return created_date;
	}

	public void setCreated_date(LocalDate created_date) {
		this.created_date = created_date;
	}

	public Bank(String name) {
		super();
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
