package com.bazra.usermanagement.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.bazra.usermanagement.request.LanguageRequest;
import com.bazra.usermanagement.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.bazra.usermanagement.model.Account;
import com.bazra.usermanagement.model.SupportedLanguages;
import com.bazra.usermanagement.repository.AccountRepository;
import com.bazra.usermanagement.repository.SupportedLanguageRepository;
import com.bazra.usermanagement.request.CreateBankRequest;
import com.bazra.usermanagement.request.CreateLanguageRequest;

@RestController
@CrossOrigin("*")
@RequestMapping("/Api/Languages")
public class SupportedLanguagesController {
	@Autowired
	AccountRepository accountRepository;
	@Autowired
	SupportedLanguageRepository supportedLanguageRepository;
	@PostMapping("/CreateLanguage")
	public ResponseEntity<?> createLanguage(@RequestBody CreateLanguageRequest createLanguageRequest, Authentication authentication){
		Account adminAccount= accountRepository.findByAccountNumberEquals(authentication.getName()).get();
		if (!adminAccount.getType().getAccounttype().matches("ADMIN")) {
			return ResponseEntity.badRequest().body(new ResponseError("Unauthorized request"));
		}
		if(createLanguageRequest.getName().isEmpty()) {
			return ResponseEntity.badRequest().body(new ResponseError("Language name cannot be empty!"));
		}
		Boolean xBoolean = supportedLanguageRepository.findByName(createLanguageRequest.getName()).isPresent();
		if(xBoolean) {
			return ResponseEntity.badRequest().body(new ResponseError("Language already added!"));
		}
		SupportedLanguages supportedLanguages = new SupportedLanguages();
		supportedLanguages.setAdmin(adminAccount.getAdmin());
		supportedLanguages.setCreationDate(LocalDate.now());
		supportedLanguages.setName(createLanguageRequest.getName());
		supportedLanguageRepository.save(supportedLanguages);
		return ResponseEntity.ok(new SuccessMessageResponse("Language added successfully!!"));

	}
	@GetMapping("/All")
	public ResponseEntity<?> all(@RequestParam Optional<String> sortBy, Authentication authentication) {
		Account adminAccount= accountRepository.findByAccountNumberEquals(authentication.getName()).get();

		if (!adminAccount.getType().getAccounttype().matches("ADMIN")) {
			return ResponseEntity.badRequest().body(new ResponseError("Unauthorized request"));
		}
		List<SupportedLanguages> supportedLanguages = supportedLanguageRepository.findAll();
		if (supportedLanguages.isEmpty()) {
			return ResponseEntity.badRequest().body(new ResponseError("No Language Found"));
		}
		return ResponseEntity.ok(new ListOfLanguages(supportedLanguages));
	}

	@GetMapping("/All/{id}")
	public ResponseEntity<?> all(Authentication authentication,@PathVariable Integer id) {

		Optional<SupportedLanguages> supportedLanguageid = supportedLanguageRepository.findById(id);
		if (supportedLanguageid.isEmpty()) {
			return ResponseEntity.badRequest().body(new ResponseError("No language with this ID"));
		}
		return ResponseEntity.ok(new ListOfLanguages(supportedLanguageid));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deletePromotion(@PathVariable Integer id, Authentication authentication) {
		Account adminaccount = accountRepository.findByAccountNumberEquals(authentication.getName()).get();
		if (!adminaccount.getType().getAccounttype().matches("ADMIN")) {
			return ResponseEntity.badRequest().body(new ResponseError("Unauthorized request"));

		}
		if (supportedLanguageRepository.findById(id).isPresent()) {
//			i comment this out cause there is no boollean value on our database to get the status
//			if(supportedLanguageRepository.getById(id).getStatus()==true) {
//				return ResponseEntity.badRequest().body(new ResponseError("Language is being used! Cannot be deleted!"));
//			}
			supportedLanguageRepository.deleteById(id);
			return ResponseEntity.ok(new UpdateResponse("Language Deleted successfully"));
		}
		else {
			return ResponseEntity.badRequest().body(new ResponseError("No Language to be deleted"));
		}
	}

	//update
	@PutMapping("/Update/{id}")

	public ResponseEntity<?> updateSupportedLanguages(Authentication authentication, @RequestBody LanguageRequest languageRequest, @PathVariable int id) throws IOException {
		Account adminaccount = accountRepository.findByAccountNumberEquals(authentication.getName()).get();
		if (!adminaccount.getType().getAccounttype().matches("ADMIN")) {
			return ResponseEntity.badRequest().body(new ResponseError("Unauthorized request"));

		}

		Optional<SupportedLanguages> languagesOptional= supportedLanguageRepository.findById(id);

		//	this to use it for updated declare so that we get the request's
		SupportedLanguages supportedLanguages = languagesOptional.get();
		if (!languagesOptional.isPresent()) {
			return ResponseEntity.badRequest().body(new ResponseError("No Language with this ID was found"));
		}
		SupportedLanguages lang = languagesOptional.get();
		if(languageRequest.getName()!=null) {
			if( languageRequest.getName()!=null) {
				Boolean b= supportedLanguageRepository.findByName(languageRequest.getName()).isPresent();
				if(b) {
					return ResponseEntity.ok(new LanguageResponse("Language already exists!"));
				}
				supportedLanguages.setName(languageRequest.getName());
//			languageRequest.setName(String.valueOf(supportedLanguageRepository.findByName(languageRequest.getName()).get()));
				supportedLanguageRepository.save(lang);
				return ResponseEntity.ok(new LanguageResponse("Language Updated successfully"));
			}

			return ResponseEntity.badRequest().body(new ResponseError("Language is being used! Cannot be updated!"));
		}
		return ResponseEntity.badRequest().body(new ResponseError("No update value provided!"));

	}




}
