package com.bazra.usermanagement.response;

public class LanguageResponse {
    private String message;

    public LanguageResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
