package com.bazra.usermanagement.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bazra.usermanagement.model.Account;
import com.bazra.usermanagement.model.SupportedCurrency;
import com.bazra.usermanagement.repository.AccountRepository;
import com.bazra.usermanagement.repository.CurrencyRepository;
import com.bazra.usermanagement.request.CreateBankRequest;
import com.bazra.usermanagement.request.SupportedCurrencyRequest;
import com.bazra.usermanagement.response.ResponseError;
import com.bazra.usermanagement.response.SuccessMessageResponse;

@RestController
@CrossOrigin("*")
@RequestMapping("/Api/Currency")
public class SupportedCurrencyController {
	@Autowired
	AccountRepository accountRepository;
	@Autowired
	CurrencyRepository currencyRepository;
	
	

	@PostMapping("/CreateCurrency")
	public ResponseEntity<?> createCurrency(@RequestBody SupportedCurrencyRequest supportedCurrencyRequest, Authentication authentication){
		Account adminAccount= accountRepository.findByAccountNumberEquals(authentication.getName()).get();
		if (!adminAccount.getType().getAccounttype().matches("ADMIN")) {
			return ResponseEntity.badRequest().body(new ResponseError("Unauthorized request"));
		}
		if(supportedCurrencyRequest.getName().isEmpty()) {
			return ResponseEntity.badRequest().body(new ResponseError("Currency name cannot be empty!"));
		}
		Boolean xBoolean = currencyRepository.findBycurrencyName(supportedCurrencyRequest.getName()).isPresent();
		if(xBoolean) {
			return ResponseEntity.badRequest().body(new ResponseError("Currency already added!"));
		}
		SupportedCurrency supportedCurrency = new SupportedCurrency();
		supportedCurrency.setAdmin(adminAccount.getAdmin());
		supportedCurrency.setCreatedDate(LocalDate.now());
		supportedCurrency.setCurrencyName(supportedCurrencyRequest.getName());
		supportedCurrency.setIsActive(true);
		currencyRepository.save(supportedCurrency);
		return ResponseEntity.ok(new SuccessMessageResponse("Currency created successfully!!"));
	}
}
