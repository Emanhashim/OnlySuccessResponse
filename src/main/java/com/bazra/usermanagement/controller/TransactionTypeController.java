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
import com.bazra.usermanagement.model.Transactiontype;
import com.bazra.usermanagement.repository.AccountRepository;
import com.bazra.usermanagement.repository.TransactionTypeRepository;
import com.bazra.usermanagement.request.CreateTransactionTypeRequest;
import com.bazra.usermanagement.response.ResponseError;
import com.bazra.usermanagement.response.SuccessMessageResponse;

@RestController
@CrossOrigin("*")
@RequestMapping("/Api/TransactionTypes")
public class TransactionTypeController {
	
	@Autowired
	AccountRepository accountRepository;
	
	@Autowired
	TransactionTypeRepository transactionTypeRepository;
	
	@PostMapping("/CreateTransactionType")
	public ResponseEntity<?> createTransactionType(@RequestBody CreateTransactionTypeRequest createTransactionTypeRequest, Authentication authentication){
		Account adminAccount= accountRepository.findByAccountNumberEquals(authentication.getName()).get();
		if (!adminAccount.getType().getAccounttype().matches("ADMIN")) {
			return ResponseEntity.badRequest().body(new ResponseError("Unauthorized request"));
		}
		if(createTransactionTypeRequest.getName().isEmpty()) {
			return ResponseEntity.badRequest().body(new ResponseError("Language name cannot be empty!"));
		}
		Boolean xBoolean = transactionTypeRepository.findBytransactionType(createTransactionTypeRequest.getName()).isPresent();
		if(xBoolean) {
			return ResponseEntity.badRequest().body(new ResponseError("Transaction Type already added!"));
		}
		Transactiontype transactiontype = new Transactiontype();
		transactiontype.setAdmin(adminAccount.getAdmin());
		transactiontype.setCreationDate(LocalDate.now());
		transactiontype.setTransactionType(createTransactionTypeRequest.getName());
		transactionTypeRepository.save(transactiontype);
		return ResponseEntity.ok(new SuccessMessageResponse("Transaction Type added successfully!!"));
	
	}
}
