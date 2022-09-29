package com.bazra.usermanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bazra.usermanagement.model.Bank;
import com.bazra.usermanagement.model.SupportedLanguages;

public interface SupportedLanguageRepository extends JpaRepository<SupportedLanguages, Integer> {
	Optional<SupportedLanguages> findByName(String name);

}
