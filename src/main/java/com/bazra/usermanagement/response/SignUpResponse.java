package com.bazra.usermanagement.response;

import com.bazra.usermanagement.model.Levels;

/**
 * SignUp Response
 * 
 * @author MOSS
 * @version 4/2022
 */
public class SignUpResponse {
    private String message;
    private String firstName;
    private String lastName;
    private Levels level;
    private String username;
    private String roles;
 

    public SignUpResponse(String message) {
        this.message = message;
    }

    public SignUpResponse(String username, String string, String message,String firstname,String lastname,Levels levels) {
        this.username = username;
        this.roles = string;
        this.message = message;
        this.firstName =firstname;
        this.lastName = lastname;
        this.level=levels;
    }
    
    
    
    public SignUpResponse(String username, String roles, String string, String firstName, String lastName) {
		this.username=username;
		this.roles=roles;
		this.message=string;
		this.firstName=firstName;
		this.lastName=lastName;
		
	}
    
    
    
	public Levels getLevel() {
		return level;
	}

	public void setLevel(Levels level) {
		this.level = level;
	}

	public String getFirstname() {
        return firstName;
    }

    public void setFirstname(String firstName) {
        this.firstName = firstName;
    }

    public String getLastname() {
        return lastName;
    }

    public void setLastname(String lastName) {
        this.lastName = lastName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

  

   

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}
