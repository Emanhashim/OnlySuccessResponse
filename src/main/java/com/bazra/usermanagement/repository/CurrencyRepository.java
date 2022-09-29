package com.bazra.usermanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bazra.usermanagement.model.SupportedCurrency;

public interface CurrencyRepository  extends JpaRepository<SupportedCurrency, Integer> {
	Optional<SupportedCurrency> findBycurrencyName(String name);

}
