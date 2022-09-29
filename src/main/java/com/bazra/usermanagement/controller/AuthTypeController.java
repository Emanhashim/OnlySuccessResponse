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

import com.bazra.usermanagement.model.AddressElement;
import com.bazra.usermanagement.model.AuthenticationType;
import com.bazra.usermanagement.repository.AddressElementRepository;
import com.bazra.usermanagement.repository.AdminRepository;
import com.bazra.usermanagement.repository.AuthTypeRepository;
import com.bazra.usermanagement.request.CreateAddressElementRequest;
import com.bazra.usermanagement.request.CreateAuthTypeRequest;
import com.bazra.usermanagement.response.ResponseError;
import com.bazra.usermanagement.response.UpdateResponse;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin("*")
@RequestMapping("/Api/AuthType")
public class AuthTypeController {
	@Autowired
	AuthTypeRepository authTypeRepository;
	@Autowired
	AdminRepository adminRepository;
	@PostMapping("/CreateAuthType")
	@ApiOperation(value ="WE CAN CREATE AUTHENTICATION TYPES")
    public ResponseEntity<?> createAddressElement(@RequestBody CreateAuthTypeRequest createAuthTypeRequest,Authentication authorization) {
    	Optional<AuthenticationType> accOptional =authTypeRepository.findByauthenticationtype(createAuthTypeRequest.getAuthenticationtype());
    	if (accOptional.isPresent()) {
    		return ResponseEntity.badRequest().body(new ResponseError("Authentication type already exist!"));
		}
    	AuthenticationType authenticationType = new AuthenticationType();
    	authenticationType.setAuthenticationtype(createAuthTypeRequest.getAuthenticationtype());
    	authenticationType.setCreatedDateTime(LocalDate.now());
    	authenticationType.setCreatorid(adminRepository.findByUsername(authorization.getName()).get());
    	authTypeRepository.save(authenticationType);
    	return ResponseEntity.ok(new UpdateResponse("Authentication Type created successfully"));
	}

}
