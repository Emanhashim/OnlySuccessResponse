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
import com.bazra.usermanagement.model.AdminInfo;
import com.bazra.usermanagement.model.Role;
import com.bazra.usermanagement.repository.AccountRepository;
import com.bazra.usermanagement.repository.RoleRepository;
import com.bazra.usermanagement.request.CreateBankRequest;
import com.bazra.usermanagement.request.CreateRoleRequest;
import com.bazra.usermanagement.response.ResponseError;
import com.bazra.usermanagement.response.SuccessMessageResponse;

@RestController
@CrossOrigin("*")
@RequestMapping("/Api/Role")
public class RoleController {
	@Autowired
	RoleRepository roleRepository;

	@Autowired
	AccountRepository accountRepository;

	@PostMapping("/CreateRole")
	public ResponseEntity<?> createBank(@RequestBody CreateRoleRequest createRoleRequest,
			Authentication authentication) {
		
		 Account adminAccount=
		 accountRepository.findByAccountNumberEquals(authentication.getName()).get();
		if (!adminAccount.getType().getAccounttype().matches("ADMIN")) {
			return ResponseEntity.badRequest().body(new ResponseError("Unauthorized request"));
		}
		if (createRoleRequest.getRolename().isEmpty()) {
			return ResponseEntity.badRequest().body(new ResponseError("Role Name Cannot be empty"));
		}
		if (roleRepository.findByrolename(createRoleRequest.getRolename()).isPresent()) {
			return ResponseEntity.badRequest().body(new ResponseError("Role already exists!"));
		}
		Role role = new Role();
		role.setAdmin(adminAccount.getAdmin());
		role.setCreatedDate(LocalDate.now());
		role.setRolename(createRoleRequest.getRolename());
		roleRepository.save(role);
		return ResponseEntity.ok(new SuccessMessageResponse("Role created successfully!!"));
	}
}
