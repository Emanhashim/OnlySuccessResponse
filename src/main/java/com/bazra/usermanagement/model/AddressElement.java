package com.bazra.usermanagement.model;

import java.time.LocalDate;

import javax.persistence.*;

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
@Table(name = "address_element")
@EntityListeners(AuditingEntityListener.class)
public class AddressElement {
	 @Id
	    @GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int id;
	private String title;
	private boolean isrequired;
	private boolean isactive;
	private LocalDate registeredDate;
	@ManyToOne
    @JoinColumn(name = "creator_id")
	private AdminInfo admin;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public boolean isIsrequired() {
		return isrequired;
	}
	public void setIsrequired(boolean isrequired) {
		this.isrequired = isrequired;
	}
	public boolean isIsactive() {
		return isactive;
	}
	public void setIsactive(boolean isactive) {
		this.isactive = isactive;
	}
	public LocalDate getRegisteredDate() {
		return registeredDate;
	}
	public void setRegisteredDate(LocalDate registeredDate) {
		this.registeredDate = registeredDate;
	}
	public AdminInfo getAdmin() {
		return admin;
	}
	public void setAdmin(AdminInfo admin) {
		this.admin = admin;
	}
	
	
	

}
