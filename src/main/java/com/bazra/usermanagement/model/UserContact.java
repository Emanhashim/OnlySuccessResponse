package com.bazra.usermanagement.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@EqualsAndHashCode
@NoArgsConstructor
@Table(name = "user_contact")
public class UserContact {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int id;
	
	private String contact;
	private Boolean is_active;
	
	@ManyToOne
	@JoinColumn(name = "contact_type_id")
	private CommunicationMedium communicationMedium;
	private LocalDate registereDate;
	@ManyToOne
	@JoinColumn(name = "userid")
	private UserInfo userInfo;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public Boolean getIs_active() {
		return is_active;
	}
	public void setIs_active(Boolean is_active) {
		this.is_active = is_active;
	}
	public CommunicationMedium getCommunicationMedium() {
		return communicationMedium;
	}
	public void setCommunicationMedium(CommunicationMedium communicationMedium) {
		this.communicationMedium = communicationMedium;
	}
	public LocalDate getRegistereDate() {
		return registereDate;
	}
	public void setRegistereDate(LocalDate registereDate) {
		this.registereDate = registereDate;
	}
	public UserInfo getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	
	
	
}
