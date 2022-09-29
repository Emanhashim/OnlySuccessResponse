package com.bazra.usermanagement.model;

import java.time.LocalDateTime;

import javax.persistence.*;

@Entity
@Table(name = "Document_Type")
public class Document_Type {
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;
	@Column(unique=true)
	private String name;
	private String description;
	private boolean is_active;
	private LocalDateTime created_date;
	@ManyToOne
    @JoinColumn(name = "creatorid")
	private AdminInfo creator_id;

	
	public Document_Type(String name,String description) {
		super();
		this.name=name;
		this.description=description;
	
	}
	public Document_Type() {
		// TODO Auto-generated constructor stub
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isIs_active() {
		return is_active;
	}
	public void setIs_active(boolean is_active) {
		this.is_active = is_active;
	}
	public LocalDateTime getCreated_date() {
		return created_date;
	}
	public void setCreated_date(LocalDateTime created_date) {
		this.created_date = created_date;
	}
	public AdminInfo getCreator_id() {
		return creator_id;
	}
	public void setCreator_id(AdminInfo creator_id) {
		this.creator_id = creator_id;
	}
	public void setId(int id) {
		this.id = id;
	}

	
	
}
