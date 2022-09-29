package com.bazra.usermanagement.request;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 *
 * @author Bemnet
 * @version 4/2022
 *
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode
@ToString

public class SignUpRequest {
	@ApiModelProperty(value = "This is users first name ")
	@NotBlank
	@Size(min = 4, message = "Name must be at least 4 characters")
	private String firstName;

	@ApiModelProperty(value = "This is users father name ")
	@NotBlank
	@Size(min = 4, message = "Name must be at least 4 characters")
	private String fatheName;

	@ApiModelProperty(value = "This is users mother name ")
	@NotBlank
	@Size(min = 4, message = "Name must be at least 4 characters")
	private String motherName;

	@ApiModelProperty(value = "This is Users Email ")
	@NotBlank(message = "Enter your email")
	@Column(unique = true)
	@Email
	private String email;

	@ApiModelProperty(value = "This is Users BirthDate ")
	private String birthDay;

	@NotBlank
	private String nationality;

	@NotBlank
	private String phone;
	
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getFatheName() {
		return fatheName;
	}

	public void setFatheName(String fatheName) {
		this.fatheName = fatheName;
	}

	public String getMotherName() {
		return motherName;
	}

	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getBirthDay() {
		return birthDay;
	}

	public void setBirthDay(String birthday) {
		this.birthDay = birthday;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstname) {
		this.firstName = firstname;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

}
