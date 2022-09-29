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
@Table(name = "authentication_type")
@EntityListeners(AuditingEntityListener.class)
public class AuthenticationType {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int id;
	private String authenticationtype;
	@ManyToOne
    @JoinColumn(name = "creatorid")
	private AdminInfo creatorid;
	private LocalDate createdDateTime;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	
	public String getAuthenticationtype() {
		return authenticationtype;
	}
	public void setAuthenticationtype(String authenticationtype) {
		this.authenticationtype = authenticationtype;
	}
	public AdminInfo getCreatorid() {
		return creatorid;
	}
	public void setCreatorid(AdminInfo creatorid) {
		this.creatorid = creatorid;
	}
	public LocalDate getCreatedDateTime() {
		return createdDateTime;
	}
	public void setCreatedDateTime(LocalDate createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	
	
}
