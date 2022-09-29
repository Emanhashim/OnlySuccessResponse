package com.bazra.usermanagement.response;


import com.bazra.usermanagement.model.SupportedLanguages;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ListOfLanguages {
    private List<?> list = new ArrayList<>();
    private List<SupportedLanguages> language = new ArrayList<>();
    private List<SupportedLanguages> language1 = new ArrayList<>();
    private Optional<SupportedLanguages> languages;


    public List<SupportedLanguages> getLanguage() {
        return language;
    }

    public void setLanguage(List<SupportedLanguages> language) {
        this.language = language;
    }

    public List<SupportedLanguages> getLanguage1() {
        return language1;
    }

    public void setLanguage1(List<SupportedLanguages> language1) {
        this.language1 = language1;
    }

    public Optional<SupportedLanguages> getLanguages() {
        return languages;
    }

    public void setLanguages(Optional<SupportedLanguages> languages) {
        this.languages = languages;
    }

//    this to get data of all
    public ListOfLanguages(List<SupportedLanguages> list) {
        this.language=list;
    }

//   this to get data by id
    public ListOfLanguages(Optional<SupportedLanguages> langs) {


        this.languages=langs;
    }

    public ListOfLanguages(List<SupportedLanguages> language, List<SupportedLanguages> language1) {
        this.language = language;
        this.language1 = language1;
    }
}
