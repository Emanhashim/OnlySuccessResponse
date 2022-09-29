package com.bazra.usermanagement.controller;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bazra.usermanagement.model.AccountType;
import com.bazra.usermanagement.repository.AccountTypeRepository;
import com.bazra.usermanagement.repository.AdminRepository;
import com.bazra.usermanagement.repository.UserRepository;
import com.bazra.usermanagement.request.CreateAccountTypeRequest;
import com.bazra.usermanagement.response.ResponseError;
import com.bazra.usermanagement.response.UpdateResponse;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin("*")
@RequestMapping("/Api/Accounttype")
public class AccountTypeController {
	@Autowired
	AccountTypeRepository accountTypeRepository;
	@Autowired
	AdminRepository adminRepository;
	@PostMapping("/CreateType")
	@ApiOperation(value ="WE CAN CREATE ACCOUNT TYPE")
    public ResponseEntity<?> createSetting(@RequestBody CreateAccountTypeRequest createAccountTypeRequest,Authentication authentication) {
    	Optional<AccountType> accOptional =accountTypeRepository.findByaccounttype(createAccountTypeRequest.getAccounttype());
    	if (accOptional.isPresent()) {
    		return ResponseEntity.badRequest().body(new ResponseError("Account Type already exist!"));
		}
    	AccountType accountType = new AccountType();
    	accountType.setAccounttype(createAccountTypeRequest.getAccounttype());
    	accountType.setCreatedDateTime(LocalDate.now());
    	accountType.setAdminInfo(adminRepository.findByUsername(authentication.getName()).get());
    	accountTypeRepository.save(accountType);
    	return ResponseEntity.ok(new UpdateResponse("Account type created successfully"));
    }
}
