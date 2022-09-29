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
import com.bazra.usermanagement.repository.AddressElementRepository;
import com.bazra.usermanagement.repository.AdminRepository;
import com.bazra.usermanagement.request.CreateAddressElementRequest;
import com.bazra.usermanagement.response.ResponseError;
import com.bazra.usermanagement.response.UpdateResponse;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin("*")
@RequestMapping("/Api/AddressElement")
public class AddressElementController {
	@Autowired
	AddressElementRepository addressElementRepository;
	@Autowired
	AdminRepository adminRepository;
	@PostMapping("/CreateElement")
	@ApiOperation(value ="WE CAN CREATE ADDRESS ELEMENT")
    public ResponseEntity<?> createAddressElement(@RequestBody CreateAddressElementRequest createAddressElementRequest,Authentication authorization) {
    	Optional<AddressElement> accOptional =addressElementRepository.findBytitle(createAddressElementRequest.getTitle());
    	if (accOptional.isPresent()) {
    		return ResponseEntity.badRequest().body(new ResponseError("Address element already exist!"));
		}
    	AddressElement addressElement = new AddressElement();
    	addressElement.setTitle(createAddressElementRequest.getTitle());
    	addressElement.setRegisteredDate(LocalDate.now());
    	addressElement.setAdmin(adminRepository.findByUsername(authorization.getName()).get());
    	addressElementRepository.save(addressElement);
    	return ResponseEntity.ok(new UpdateResponse("Address element created successfully"));
	}

}
