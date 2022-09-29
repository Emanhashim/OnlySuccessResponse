package com.bazra.usermanagement.request;

import javax.validation.constraints.NotBlank;

public class CreateQuestionRequest {
	@NotBlank(message = "Enter Security Question")
	private String question;
	@NotBlank
	private String language;

	
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	


}
