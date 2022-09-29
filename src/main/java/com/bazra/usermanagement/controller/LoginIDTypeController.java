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

import com.bazra.usermanagement.model.Account;
import com.bazra.usermanagement.model.Document_Type;
import com.bazra.usermanagement.model.LoginIDType;
import com.bazra.usermanagement.repository.AccountRepository;
import com.bazra.usermanagement.repository.LoginIdTypeRepository;
import com.bazra.usermanagement.request.CreateDocumentTypeRequest;
import com.bazra.usermanagement.request.LoginIdTypeRequest;
import com.bazra.usermanagement.response.ResponseError;
import com.bazra.usermanagement.response.SuccessMessageResponse;

@RestController
@CrossOrigin("*")
@RequestMapping("/Api/LoginIDType")
public class LoginIDTypeController {
	@Autowired
	LoginIdTypeRepository loginIdTypeRepository;
	@Autowired
	AccountRepository accountRepository;
	@PostMapping("/CreateLoginIDType")
	public ResponseEntity<?> createLoginIDType(@RequestBody LoginIdTypeRequest loginIdTypeRequest, Authentication authentication){
		Optional<LoginIDType> documenttype = loginIdTypeRepository.findBylogintype(loginIdTypeRequest.getLoginType());
		Account adminAccount= accountRepository.findByAccountNumberEquals(authentication.getName()).get();
		
		if (!adminAccount.getType().getAccounttype().matches("ADMIN")) {
			return ResponseEntity.badRequest().body(new ResponseError("Unauthorized request"));
		}
		if (documenttype.isPresent()) {
			return ResponseEntity.badRequest().body(new ResponseError("Login Type already exists!"));
		}
		if (loginIdTypeRequest.getDescription().isEmpty()) {
			return ResponseEntity.badRequest().body(new ResponseError("Description cannot be empty!"));
		}
		if (loginIdTypeRequest.getLoginType().isEmpty()) {
			return ResponseEntity.badRequest().body(new ResponseError("Name of Login Type cannot be empty!"));
		}
		LoginIDType loginIDType = new LoginIDType();
		loginIDType.setAdminInfo(adminAccount.getAdmin());
		loginIDType.setCreatedDate(LocalDate.now());
		loginIDType.setDescription(loginIdTypeRequest.getDescription());
		loginIDType.setLogintype(loginIdTypeRequest.getLoginType());
		loginIdTypeRepository.save(loginIDType);
		return ResponseEntity.ok(new SuccessMessageResponse("Created Login Type successfully!!"));
	}
}
