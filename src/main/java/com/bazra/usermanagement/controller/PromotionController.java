package com.bazra.usermanagement.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bazra.usermanagement.model.Account;
import com.bazra.usermanagement.model.Promotion;
import com.bazra.usermanagement.repository.AccountRepository;
import com.bazra.usermanagement.repository.PromotionRepository;
import com.bazra.usermanagement.repository.SettingRepository;
import com.bazra.usermanagement.request.CreatePromotionRequest;
import com.bazra.usermanagement.request.PromotionUpdateRequest;
import com.bazra.usermanagement.response.ListOfResponse;
import com.bazra.usermanagement.response.ResponseError;
import com.bazra.usermanagement.response.SinglePromotionResponse;
import com.bazra.usermanagement.response.SuccessMessageResponse;
import com.bazra.usermanagement.response.UpdateResponse;
import com.bazra.usermanagement.service.UserInfoService;


@RestController
@CrossOrigin("*")
@RequestMapping("/Api/Promotion")
public class PromotionController{
	@Autowired
	PromotionRepository promotionRepository;
	@Autowired
	AccountRepository accountRepository;
	@Autowired
	SettingRepository settingRepository;
	@Value("${promotion.upload.path}")
	private String promophotoPath;
	@Value("${promotion.photo.path}")
	private String photopath;
	
	@PostMapping("/CreatePromotion")
	public ResponseEntity<?> createPromotion(@ModelAttribute CreatePromotionRequest createPromotionRequest, Authentication authentication) throws IOException {
		Optional<Promotion> promotionbytitle = promotionRepository.findByTitle(createPromotionRequest.getTitle());
//		Optional<Promotion> promotionbyexpirationDate = promotionRepository.findByExpirationDate(createPromotionRequest.getExpirationDate());
//		System.out.println(promotionbyexpirationDate);
		Account adminAccount= accountRepository.findByAccountNumberEquals(authentication.getName()).get();
		
		if (!adminAccount.getType().getAccounttype().matches("ADMIN")) {
			return ResponseEntity.badRequest().body(new ResponseError("Unauthorized request"));
		}
		if (promotionbytitle.isPresent()) {
			return ResponseEntity.badRequest().body(new ResponseError("Title already taken!"));
		}
		
		if(createPromotionRequest.getTitle().isEmpty()) {
			return ResponseEntity.badRequest().body(new ResponseError("Title cannot be empty!"));
		}
		if(createPromotionRequest.getDescription().isEmpty()) {
			return ResponseEntity.badRequest().body(new ResponseError("Description cannot be empty!"));
		}
		if(createPromotionRequest.getExpirationDate()== null) {
			return ResponseEntity.badRequest().body(new ResponseError("Expiration date cannot be empty!"));
		}
		if(createPromotionRequest.getPicture().isEmpty()) {
			return ResponseEntity.badRequest().body(new ResponseError("Picture cannot be empty!"));
		}
		
		
	
		
		String photoName = StringUtils.cleanPath(createPromotionRequest.getPicture().getOriginalFilename());
		String photouploadDir = promophotoPath;
		
		Promotion promo = new Promotion(createPromotionRequest.getTitle(),photoName, createPromotionRequest.getDescription(), createPromotionRequest.getExpirationDate(), false);
		String path =photopath+photoName;
		System.out.println(path);
		promo.setPhotopath(path);
		promotionRepository.save(promo);
		UserInfoService.savePhoto(photouploadDir, photoName, createPromotionRequest.getPicture());
	     return ResponseEntity.ok(new SuccessMessageResponse("Created Promotion successfully!!"));
	}
	
	@PutMapping("/UpdatePromotion/{id}")

	public ResponseEntity<?> updatePromotion(@ModelAttribute PromotionUpdateRequest promotionUpdateRequest,Authentication authentication,@PathVariable int id) throws IOException {
		Account adminaccount = accountRepository.findByAccountNumberEquals(authentication.getName()).get();
		if (!adminaccount.getType().getAccounttype().matches("ADMIN")) {
			return ResponseEntity.badRequest().body(new ResponseError("Unauthorized request"));

		}
		Optional<Promotion> optional = promotionRepository.findById(id);
		if (!optional.isPresent()) {
			return ResponseEntity.badRequest().body(new ResponseError("No promotion with this ID was found"));
		}
		Promotion promotion = optional.get();
		if(promotionUpdateRequest.getDescription()!=null) {
			promotion.setDescription(promotionUpdateRequest.getDescription());
		}
		if(promotionUpdateRequest.getExpirationDate()!=null) {
			promotion.setExpirationDate(promotionUpdateRequest.getExpirationDate());
		}
		if(promotionUpdateRequest.getPicture()!=null) {
			String photoName = StringUtils.cleanPath(promotionUpdateRequest.getPicture().getOriginalFilename());
			promotion.setPicture(photoName);
			String photouploadDir = promophotoPath;
			String path= photopath+photoName;
			promotion.setPhotopath(path);
			UserInfoService.savePhoto(photouploadDir, photoName, promotionUpdateRequest.getPicture());
		}
		List<Promotion> promotions =promotionRepository.findAll();
		int count = 0;
		for (int i = 0; i < promotions.size(); i++) {
			if (promotions.get(i).isStatus()==true) {
				count++;
				
			}
		}
		
		BigDecimal PromotionNo= settingRepository.findBysettingName("Promotion Number").get().getValue();
		BigDecimal countBigDecimal =BigDecimal.valueOf( count);
		if(countBigDecimal.compareTo(PromotionNo)==1) {
			return ResponseEntity.badRequest().body(new ResponseError("Promotion list full!!"));
		}
		else if(countBigDecimal.compareTo(PromotionNo)==0 && promotionUpdateRequest.isStatus()== true) {
			if (promotion.isStatus()==true) {
				return ResponseEntity.badRequest().body(new ResponseError("Status Already Active"));
			}
			return ResponseEntity.badRequest().body(new ResponseError("Promotion list full!!"));
		}
		if(promotionUpdateRequest.getTitle()!=null) {
			promotion.setTitle(promotionUpdateRequest.getTitle());
		}
		if(promotionUpdateRequest.isStatus()!= true) {
			promotion.setStatus(false);
		}
		if(promotionUpdateRequest.isStatus()!= false) {
			promotion.setStatus(true);
		}
		
		
		
		
		promotionRepository.save(promotion);
		return ResponseEntity.ok(new ResponseError("Updated promotion successfully!!!"));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deletePromotion(@PathVariable Integer id, Authentication authentication) {
		Account adminaccount = accountRepository.findByAccountNumberEquals(authentication.getName()).get();
		if (!adminaccount.getType().getAccounttype().matches("ADMIN")) {
			return ResponseEntity.badRequest().body(new ResponseError("Unauthorized request"));

		}
		if (promotionRepository.findById(id).isPresent()) {
			promotionRepository.deleteById(id);
			return ResponseEntity.ok(new UpdateResponse("Promotion Deleted successfully"));
		}
		else {
			return ResponseEntity.badRequest().body(new ResponseError("No Promotion found"));
		}
	}
	@GetMapping("/All")

	public ResponseEntity<?> all(@RequestParam Optional<String> sortBy) {
		
		List<Promotion> promotionsList = promotionRepository.findAll();
		if (promotionsList.isEmpty()) {
			return ResponseEntity.badRequest().body(new ResponseError("No Promotion found"));
		}
		return ResponseEntity.ok(new ListOfResponse(promotionsList));
	}
	
	@GetMapping("/AllActive")

	public ResponseEntity<?> allActive(@RequestParam Optional<String> sortBy) {
		
		List<Promotion> promotionsList = promotionRepository.findByStatus(true);
		if (promotionsList.isEmpty()) {
			return ResponseEntity.badRequest().body(new ResponseError("No Promotion found"));
		}
		return ResponseEntity.ok(new ListOfResponse(promotionsList));
	}
	
	@GetMapping(value="/All/{id}")
	public ResponseEntity<?> getPromotion(@PathVariable Integer id) throws IOException {
		
		Optional<Promotion> optional = promotionRepository.findById(id);
		if (!optional.isPresent()) {
			return ResponseEntity.badRequest().body(new ResponseError("No promotion found"));
		}
		
//		String photouploadDir = promophotoPath;
		
//		InputStream in = new BufferedInputStream(new FileInputStream(photouploadDir));
//
//		byte[] bytes = IOUtils.toByteArray(in);
		
		return ResponseEntity.ok(new SinglePromotionResponse(optional.get()));
	}

	

}
