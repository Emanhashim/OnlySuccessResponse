package com.bazra.usermanagement.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@EqualsAndHashCode
@NoArgsConstructor
@Table(name = "security_questions")
public class SecurityQuestion {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "Question_id")
	private int id;
	@Column(unique=true)
	private String questionName;
	private Boolean status;
	private int count;
	@ManyToOne
	@JoinColumn(name = "language_id")
	private SupportedLanguages language;
	private LocalDate createDate;
	@ManyToOne
	@JoinColumn(name = "creator_id")
	private AdminInfo admin;
	
//	public SecurityQuestion() {
//		super();
//		// TODO Auto-generated constructor stub
//	}

	public SecurityQuestion(String questionName, Boolean status, int count) {
		super();
		this.questionName = questionName;
		this.status = status;
		this.count = count;
		
	}
	
	public SecurityQuestion(String questionName, Boolean status) {
		super();
		this.questionName = questionName;
		this.status = status;
	
	}

	public int getId() {
		return id;
	}
	
	
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}

	public String getQuestionName() {
		return questionName;
	}

	public void setQuestionName(String questionName) {
		this.questionName = questionName;
	}

	public SupportedLanguages getLanguage() {
		return language;
	}

	public void setLanguage(SupportedLanguages language) {
		this.language = language;
	}

	public LocalDate getCreateDate() {
		return createDate;
	}

	public void setCreateDate(LocalDate createDate) {
		this.createDate = createDate;
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
	
	
	

}
