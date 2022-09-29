package com.bazra.usermanagement.request;

public class QuestionUpdateRequest {
	private String question;
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
