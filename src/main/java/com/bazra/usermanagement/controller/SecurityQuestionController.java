package com.bazra.usermanagement.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.core.layout.YamlLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bazra.usermanagement.model.Account;
import com.bazra.usermanagement.model.Bank;
import com.bazra.usermanagement.model.Promotion;
import com.bazra.usermanagement.model.SecurityQuestion;
import com.bazra.usermanagement.repository.AccountRepository;
import com.bazra.usermanagement.repository.SecurityQuestionsRepository;
import com.bazra.usermanagement.repository.SupportedLanguageRepository;
import com.bazra.usermanagement.request.CreateQuestionRequest;
import com.bazra.usermanagement.request.PromotionUpdateRequest;
import com.bazra.usermanagement.request.QuestionUpdateRequest;
import com.bazra.usermanagement.response.BankResponse;
import com.bazra.usermanagement.response.ListOfQuestions;
import com.bazra.usermanagement.response.ListOfResponse;
import com.bazra.usermanagement.response.ResponseError;
import com.bazra.usermanagement.response.SuccessMessageResponse;
import com.bazra.usermanagement.response.UpdateResponse;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;



@RestController
@CrossOrigin("*")
@RequestMapping("/Api/Question")
public class SecurityQuestionController {
	@Autowired
	AccountRepository accountRepository;
	@Autowired
	SecurityQuestionsRepository securityQuestionsRepository;
	@Autowired
	SupportedLanguageRepository supportedLanguageRepository;
	
	@PostMapping("/CreateQuestion")
	public ResponseEntity<?> createQuestion(@RequestBody CreateQuestionRequest createQuestionRequest, Authentication authentication){
		Account adminAccount= accountRepository.findByAccountNumberEquals(authentication.getName()).get();
		
		if (!adminAccount.getType().getAccounttype().matches("ADMIN")) {
			return ResponseEntity.badRequest().body(new ResponseError("Unauthorized request"));
		}
		if(createQuestionRequest.getQuestion()==null) {
			return ResponseEntity.badRequest().body(new ResponseError("Question cannot be empty!"));
		}
		Boolean xBoolean = securityQuestionsRepository.findByquestionName(createQuestionRequest.getQuestion()).isPresent();
		if(xBoolean) {
			return ResponseEntity.badRequest().body(new ResponseError("Question already exists!"));
		}
		SecurityQuestion securityQuestion = new SecurityQuestion();
		securityQuestion.setAdmin(adminAccount.getAdmin());
		securityQuestion.setCount(0);
		securityQuestion.setCreateDate(LocalDate.now());
		if (!supportedLanguageRepository.findByName(createQuestionRequest.getLanguage()).isPresent()) {
			return ResponseEntity.badRequest().body(new UpdateResponse("Not a valid language"));
		}
		
		securityQuestion.setLanguage(supportedLanguageRepository.findByName(createQuestionRequest.getLanguage()).get());
		securityQuestion.setQuestionName(createQuestionRequest.getQuestion());
		securityQuestion.setStatus(false);
		securityQuestionsRepository.save(securityQuestion);
		return ResponseEntity.ok(new SuccessMessageResponse("Created Question successfully!!"));
	}
	
	@GetMapping("/All")
	public ResponseEntity<?> all(@RequestParam Optional<String> sortBy,Authentication authentication) {
		Account adminAccount= accountRepository.findByAccountNumberEquals(authentication.getName()).get();
		
		if (!adminAccount.getType().getAccounttype().matches("ADMIN")) {
			return ResponseEntity.badRequest().body(new ResponseError("Unauthorized request"));
		}
		List<SecurityQuestion> securityQuestions = securityQuestionsRepository.findAll();
		if (securityQuestions.isEmpty()) {
			return ResponseEntity.badRequest().body(new ResponseError("No Promotion found"));
		}
		return ResponseEntity.ok(new ListOfQuestions(securityQuestions));
	}
	@GetMapping("/All/{id}")
	public ResponseEntity<?> all(Authentication authentication,@PathVariable Integer id) {
		
		Optional<SecurityQuestion> secuOptional = securityQuestionsRepository.findById(id);
		if (secuOptional.isEmpty()) {
			return ResponseEntity.badRequest().body(new ResponseError("No Question with this ID"));
		}
		return ResponseEntity.ok(new ListOfQuestions(secuOptional));
	}
	

	@GetMapping("/GetRandom")

	public ResponseEntity<?> random(@RequestParam Optional<String> sortBy) {
		
		List<SecurityQuestion> promotionsList = securityQuestionsRepository.findAll();
		if (promotionsList.isEmpty()) {
			return ResponseEntity.badRequest().body(new ResponseError("No Promotion found"));
		}
		Long count = securityQuestionsRepository.count();
		List<Integer> numbers1 = new ArrayList<>();
		List<Integer> numbers2 = new ArrayList<>();
		ArrayList<Integer> list = new ArrayList<Integer>();
		if(count<8) {
			return ResponseEntity.badRequest().body(new ResponseError("There are no enough questions!"));
		}
        for (int i=1; i<count; i++) list.add(i);
        Collections.shuffle(list);
        System.out.println("list"+list);
        for (int i=0; i<4; i++) numbers1.add(list.get(i));
        for (int i=4;i<8;i++) numbers2.add(list.get(i));

		List<SecurityQuestion> securityQuestions1 = new ArrayList<>();
		List<SecurityQuestion> securityQuestions2 = new ArrayList<>();
		for(int i=0;i<4;i++) {
			SecurityQuestion securityQuestion1 =promotionsList.get(numbers1.get(i));
			SecurityQuestion securityQuestion2 =promotionsList.get(numbers2.get(i));
			securityQuestions1.add(securityQuestion1);
			securityQuestions2.add(securityQuestion2);
		}
		return ResponseEntity.ok(new ListOfQuestions(securityQuestions1,securityQuestions2));
	}
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deletePromotion(@PathVariable Integer id, Authentication authentication) {
		Account adminaccount = accountRepository.findByAccountNumberEquals(authentication.getName()).get();
		if (!adminaccount.getType().getAccounttype().matches("ADMIN")) {
			return ResponseEntity.badRequest().body(new ResponseError("Unauthorized request"));

		}
		if (securityQuestionsRepository.findById(id).isPresent()) {
			if(securityQuestionsRepository.getById(id).getStatus()==true) {
				return ResponseEntity.badRequest().body(new ResponseError("Question is being used! Cannot be deleted!"));
			}
			securityQuestionsRepository.deleteById(id);
			return ResponseEntity.ok(new UpdateResponse("Question Deleted successfully"));
		}
		else {
			return ResponseEntity.badRequest().body(new ResponseError("No Question found"));
		}
	}
	@PutMapping("/UpdateQuestion/{id}")

	public ResponseEntity<?> updateSecurityQuestion(Authentication authentication,@RequestBody QuestionUpdateRequest questionUpdateRequest,@PathVariable int id) throws IOException {
		Account adminaccount = accountRepository.findByAccountNumberEquals(authentication.getName()).get();
		if (!adminaccount.getType().getAccounttype().matches("ADMIN")) {
			return ResponseEntity.badRequest().body(new ResponseError("Unauthorized request"));

		}
		Optional<SecurityQuestion> securityOptional = securityQuestionsRepository.findById(id);
		if (!securityOptional.isPresent()) {
			return ResponseEntity.badRequest().body(new ResponseError("No Question with this ID was found"));
		}
		SecurityQuestion securityQuestion = securityOptional.get();
		if(questionUpdateRequest.getQuestion()!=null) {
			if( securityQuestion.getStatus()!=true) {
				Boolean b= securityQuestionsRepository.findByquestionName(questionUpdateRequest.getQuestion()).isPresent();
				if(b) {
					return ResponseEntity.ok(new UpdateResponse("Question already exists!"));
				}
			securityQuestion.setQuestionName(questionUpdateRequest.getQuestion());
			securityQuestion.setLanguage(supportedLanguageRepository.findByName(questionUpdateRequest.getLanguage()).get());
			securityQuestionsRepository.save(securityQuestion);
			return ResponseEntity.ok(new UpdateResponse("Question Updated successfully"));
			}
			return ResponseEntity.badRequest().body(new ResponseError("Question is being used! Cannot be updated!"));
		}
		return ResponseEntity.badRequest().body(new ResponseError("No update value provided!"));
		
	}
}
