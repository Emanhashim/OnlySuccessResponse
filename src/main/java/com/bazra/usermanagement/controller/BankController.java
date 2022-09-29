package com.bazra.usermanagement.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bazra.usermanagement.model.Account;
import com.bazra.usermanagement.model.Bank;
import com.bazra.usermanagement.model.SecurityQuestion;
import com.bazra.usermanagement.repository.AccountRepository;
import com.bazra.usermanagement.repository.BankRepository;
import com.bazra.usermanagement.request.CreateBankRequest;
import com.bazra.usermanagement.request.CreateQuestionRequest;
import com.bazra.usermanagement.response.BankResponse;
import com.bazra.usermanagement.response.ListOfQuestions;
import com.bazra.usermanagement.response.ResponseError;
import com.bazra.usermanagement.response.SuccessMessageResponse;
import com.bazra.usermanagement.response.UpdateResponse;

@RestController
@CrossOrigin("*")
@RequestMapping("/Api/Bank")
public class BankController {
	@Autowired
	AccountRepository accountRepository;
	@Autowired
	BankRepository bankRepository;
	
	@PostMapping("/CreateBank")
	public ResponseEntity<?> createBank(@RequestBody CreateBankRequest createBankRequest, Authentication authentication){
		Account adminAccount= accountRepository.findByAccountNumberEquals(authentication.getName()).get();
		if (!adminAccount.getType().getAccounttype().matches("ADMIN")) {
			return ResponseEntity.badRequest().body(new ResponseError("Unauthorized request"));
		}
		if(createBankRequest.getName().isEmpty()) {
			return ResponseEntity.badRequest().body(new ResponseError("Bank name cannot be empty!"));
		}
		Boolean xBoolean = bankRepository.findByName(createBankRequest.getName()).isPresent();
		if(xBoolean) {
			return ResponseEntity.badRequest().body(new ResponseError("Bank already added!"));
		}
		
		Bank bank = new Bank(createBankRequest.getName());
		bank.setCreated_date(LocalDate.now());
		bank.setAdmin(adminAccount.getAdmin());
		bankRepository.save(bank);
		return ResponseEntity.ok(new SuccessMessageResponse("Bank added successfully!!"));
	}
	@GetMapping("/All")
	public ResponseEntity<?> allBanks(@RequestParam Optional<String> sortBy) {
		List<Bank> banks = bankRepository.findAll();
		if (banks.isEmpty()) {
			return ResponseEntity.badRequest().body(new ResponseError("No Registered Banks"));
		}
		return ResponseEntity.ok(new BankResponse(banks));
	}
	@GetMapping("/All/{id}")
	public ResponseEntity<?> getBank(@PathVariable Integer id) {
		Optional<Bank> banks = bankRepository.findById(id);
		if (banks.isEmpty()) {
			return ResponseEntity.badRequest().body(new ResponseError("No Registered Banks"));
		}
		return ResponseEntity.ok(new BankResponse(banks));
	}
	@DeleteMapping("/All/{id}")
	public ResponseEntity<?> deleteBank(@PathVariable Integer id, Authentication authentication) {
		Account adminaccount = accountRepository.findByAccountNumberEquals(authentication.getName()).get();
		if (!adminaccount.getType().getAccounttype().matches("ADMIN")) {
			return ResponseEntity.badRequest().body(new ResponseError("Unauthorized request"));

		}
		if (bankRepository.findById(id).isPresent()) {
			bankRepository.deleteById(id);
			return ResponseEntity.ok(new UpdateResponse("Bank removed successfully"));
		}
		else {
			return ResponseEntity.badRequest().body(new ResponseError("No Bank found"));
		}
	}
}
