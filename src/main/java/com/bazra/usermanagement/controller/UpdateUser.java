package com.bazra.usermanagement.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.session.RequestedUrlRedirectInvalidSessionStrategy;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.bazra.usermanagement.model.AddressElement;
import com.bazra.usermanagement.model.Document_Type;
import com.bazra.usermanagement.model.Levels;
import com.bazra.usermanagement.model.UserAddress;
import com.bazra.usermanagement.model.UserAuthentication;
import com.bazra.usermanagement.model.UserInfo;
import com.bazra.usermanagement.model.User_Document;
import com.bazra.usermanagement.repository.AddressElementRepository;
import com.bazra.usermanagement.repository.DocumentTypeRepository;
import com.bazra.usermanagement.repository.UserAddressRepository;
import com.bazra.usermanagement.repository.UserAuthenticationRepository;
import com.bazra.usermanagement.repository.UserDocumentRepository;
import com.bazra.usermanagement.repository.UserRepository;
import com.bazra.usermanagement.request.UpdateRequest;
import com.bazra.usermanagement.request.UserInfoUpdateRequest;
import com.bazra.usermanagement.response.ResponseError;
import com.bazra.usermanagement.response.UpdateResponse;
import com.bazra.usermanagement.service.UserInfoService;

/**
 * Update Controller
 * 
 * @author Bemnet
 * @version 4/2022
 *
 */

@RestController
@CrossOrigin("*")
@RequestMapping("/Api/Users")
@Api(value = "Signup User Endpoint", description = "HERE WE TAKE USER'S DATA TO UPDATE THE EXISTING DATA'S")

@ApiResponses(value = { @ApiResponse(code = 404, message = "web user that a requested page is not available "),
		@ApiResponse(code = 200, message = "The request was received and understood and is being processed "),
		@ApiResponse(code = 201, message = "The request has been fulfilled and resulted in a new resource being created "),
		@ApiResponse(code = 401, message = "The client request has not been completed because it lacks valid authentication credentials for the requested resource. "),
		@ApiResponse(code = 403, message = "Forbidden response status code indicates that the server understands the request but refuses to authorize it. ")

})
public class UpdateUser {

	@Autowired
	private UserInfoService userInfoService;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	UserRepository userRepository;

	@Autowired
	DocumentTypeRepository documentTypeRepository;

	@Autowired
	UserDocumentRepository userDocumentRepository;

	@Autowired
	UserAddressRepository userAddressRepository;

	@Autowired
	AddressElementRepository addressElementRepository;

	@Autowired
	UserAuthenticationRepository userAuthenticationRepository;

	@Value("${id.upload.path}")
	private String idPath;

	@Value("${photo.upload.path}")
	private String photoPath;

	/**
	 * Checks
	 * 
	 * @param update request
	 * @return status for update request
	 */
	@PostMapping("/Update")

	@ApiOperation(value = "UPDATE PASSWORD BY PUTTING PREVIOUS AND NEW ONE AS REQUIRED")
	public ResponseEntity<?> update(@RequestBody UpdateRequest request) {
		String pho = request.getPassword();
		String pho2 = request.getNewPassword();
		String newpassword = passwordEncoder.encode(pho2);
		if (pho.matches(pho2)) {

			return ResponseEntity.badRequest()
					.body(new UpdateResponse("Password same as before chose a different one"));
		}

		return userInfoService.updatePassword(request, newpassword, pho);
	}

	@PostMapping("/UpdateInfo")

	@ApiOperation(value = "UPDATE USER-INFOS")
	public ResponseEntity<?> update(@ModelAttribute UserInfoUpdateRequest request, Authentication authentication)
			throws IOException {
		String username = "+251" + authentication.getName().substring(authentication.getName().length() - 9);
		String photoName = StringUtils.cleanPath(request.getFileImage().getOriginalFilename());
		String idName = StringUtils.cleanPath(request.getFileID().getOriginalFilename());
		Optional<UserInfo> optional = userRepository.findByUsername(username);
		
		if (!optional.isPresent()) {
			return ResponseEntity.badRequest().body(new ResponseError("No user found"));
		}
		UserInfo userInfo2 = optional.get();

		if (request.getName() != null) {
			userInfo2.setName(request.getName());
		}
		if (request.getBirthDay() != null) {
			userInfo2.setBirthDay(request.getBirthDay());
		}
		if (request.getMotherName() != null) {
			userInfo2.setMotherName(request.getMotherName());
		}
		if (request.getLastName() != null) {
			userInfo2.setLastname(request.getLastName());
		}
		if (request.getFatherName() != null) {
			userInfo2.setFatherName(request.getFatherName());
		}
		if (request.getName() != null) {
			userInfo2.setName(request.getName());
		}
		if (request.getGender() != null) {
			userInfo2.setGender(request.getGender());
		}
		if (request.getCity() != null) {
			Optional<AddressElement> optionaladdresselement = addressElementRepository.findBytitle("CITY");
			if (!optionaladdresselement.isPresent()) {
				return ResponseEntity.badRequest().body(new ResponseError("No address element CITY found"));
			}
			AddressElement addressElement = optionaladdresselement.get();
			List<UserAddress> optionalUserAddress = userAddressRepository.findByuser(userInfo2);
			UserAddress userAddress = new UserAddress();
			if (optionalUserAddress.size()>=1) {
				for (int i = 0; i < optionalUserAddress.size(); i++) {
					if (optionalUserAddress.get(i).getAddressElement().equals(addressElement)) {
						userAddress=optionalUserAddress.get(i);
					}
					

				}
				if (userAddress.equals(null)) {
					UserAddress userAddress1 = new UserAddress();
					userAddress1.setAddress(request.getCity());
					userAddress1.setAddressElement(addressElement);
					userAddress1.setIsactive(true);
					userAddress1.setRegisteredDate(LocalDate.now());
					userAddress1.setUser(userInfo2);
					userAddressRepository.save(userAddress1);
				}
				else {
					userAddress.setAddress(request.getCity());
					userAddressRepository.save(userAddress);
				}
		
				


			}
			else {
				UserAddress userAddress1 = new UserAddress();
				userAddress1.setAddress(request.getCity());
				userAddress1.setAddressElement(addressElement);
				userAddress1.setIsactive(true);
				userAddress1.setRegisteredDate(LocalDate.now());
				userAddress1.setUser(userInfo2);
				userAddressRepository.save(userAddress1);

			}
//System.out.println(optionalUserAddress.isEmpty());
			
		}
		if (request.getMotherName() != null) {
			userInfo2.setMotherName(request.getMotherName());
		}

		if (request.getHouseNo() != null) {
			Optional<AddressElement> optionaladdresselement = addressElementRepository.findBytitle("HOUSE NUMBER");
			if (!optionaladdresselement.isPresent()) {
				return ResponseEntity.badRequest().body(new ResponseError("No address element HOUSE NUMBER found"));
			}
			AddressElement addressElement = optionaladdresselement.get();
			List<UserAddress> optionalUserAddress = userAddressRepository.findByuser(userInfo2);
			UserAddress userAddress = new UserAddress();
			if (optionalUserAddress.size()>=1) {
				for (int i = 0; i < optionalUserAddress.size(); i++) {
					if (optionalUserAddress.get(i).getAddressElement().equals(addressElement)) {
						userAddress=optionalUserAddress.get(i);
					}
					

				}
				if (userAddress.equals(null)) {
					UserAddress userAddress1 = new UserAddress();
					userAddress1.setAddress(request.getHouseNo());
					userAddress1.setAddressElement(addressElement);
					userAddress1.setIsactive(true);
					userAddress1.setRegisteredDate(LocalDate.now());
					userAddress1.setUser(userInfo2);
					userAddressRepository.save(userAddress1);
				}
				else {
					userAddress.setAddress(request.getHouseNo());
					userAddressRepository.save(userAddress);
				}
		
				


			}
			else {
				UserAddress userAddress1 = new UserAddress();
				userAddress1.setAddress(request.getHouseNo());
				userAddress1.setAddressElement(addressElement);
				userAddress1.setIsactive(true);
				userAddress1.setRegisteredDate(LocalDate.now());
				userAddress1.setUser(userInfo2);
				userAddressRepository.save(userAddress1);

			}
			

//			UserAddress userAddress = new UserAddress();
////			AddressElement addressElement = addressElementRepository.findBytitle("HOUSE NUMBER").get();

		}

		if (request.getRegion() != null) {
			Optional<AddressElement> optionaladdresselement = addressElementRepository.findBytitle("REGION");
			if (!optionaladdresselement.isPresent()) {
				return ResponseEntity.badRequest().body(new ResponseError("No address element REGION found"));
			}
			AddressElement addressElement = optionaladdresselement.get();
			List<UserAddress> optionalUserAddress = userAddressRepository.findByuser(userInfo2);
			UserAddress userAddress = new UserAddress();
			if (optionalUserAddress.size()>=1) {
				for (int i = 0; i < optionalUserAddress.size(); i++) {
					if (optionalUserAddress.get(i).getAddressElement().equals(addressElement)) {
						userAddress=optionalUserAddress.get(i);
					}
					

				}
				if (userAddress.equals(null)) {
					UserAddress userAddress1 = new UserAddress();
					userAddress1.setAddress(request.getRegion());
					userAddress1.setAddressElement(addressElement);
					userAddress1.setIsactive(true);
					userAddress1.setRegisteredDate(LocalDate.now());
					userAddress1.setUser(userInfo2);
					userAddressRepository.save(userAddress1);
				}
				else {
					userAddress.setAddress(request.getRegion());
					userAddressRepository.save(userAddress);
				}
		
				


			}
			else {
				UserAddress userAddress1 = new UserAddress();
				userAddress1.setAddress(request.getRegion());
				userAddress1.setAddressElement(addressElement);
				userAddress1.setIsactive(true);
				userAddress1.setRegisteredDate(LocalDate.now());
				userAddress1.setUser(userInfo2);
				userAddressRepository.save(userAddress1);

			}
			

			

//			UserAddress userAddress = new UserAddress();
////			AddressElement addressElement = addressElementRepository.findBytitle("REGION").get();

		}

		if (request.getSubCity() != null) {
			Optional<AddressElement> optionaladdresselement = addressElementRepository.findBytitle("SUB CITY");
			if (!optionaladdresselement.isPresent()) {
				return ResponseEntity.badRequest().body(new ResponseError("No address element SUB CITY found"));
			}
			AddressElement addressElement = optionaladdresselement.get();

			List<UserAddress> optionalUserAddress = userAddressRepository.findByuser(userInfo2);
			UserAddress userAddress = new UserAddress();
			if (optionalUserAddress.size()>=1) {
				for (int i = 0; i < optionalUserAddress.size(); i++) {
					if (optionalUserAddress.get(i).getAddressElement().equals(addressElement)) {
						userAddress=optionalUserAddress.get(i);
					}
					

				}
				if (userAddress.equals(null)) {
					UserAddress userAddress1 = new UserAddress();
					userAddress1.setAddress(request.getSubCity());
					userAddress1.setAddressElement(addressElement);
					userAddress1.setIsactive(true);
					userAddress1.setRegisteredDate(LocalDate.now());
					userAddress1.setUser(userInfo2);
					userAddressRepository.save(userAddress1);
				}
				else {
					userAddress.setAddress(request.getSubCity());
					userAddressRepository.save(userAddress);
				}
		
				


			}
			else {
				UserAddress userAddress1 = new UserAddress();
				userAddress1.setAddress(request.getSubCity());
				userAddress1.setAddressElement(addressElement);
				userAddress1.setIsactive(true);
				userAddress1.setRegisteredDate(LocalDate.now());
				userAddress1.setUser(userInfo2);
				userAddressRepository.save(userAddress1);

			}
			

			
//
//			UserAddress userAddress = new UserAddress();
////			AddressElement addressElement = addressElementRepository.findBytitle("SUB CITY").get();

		}
//		if (request.getIdNumber()!=null) {
//			userInfo2.setIdNumber(request.getIdNumber());
//			
//		}
		if (request.getWoreda() != null) {
			Optional<AddressElement> optionaladdresselement = addressElementRepository.findBytitle("WOREDA");
			if (!optionaladdresselement.isPresent()) {
				return ResponseEntity.badRequest().body(new ResponseError("No address element WOREDA found"));
			}
			AddressElement addressElement = optionaladdresselement.get();
			List<UserAddress> optionalUserAddress = userAddressRepository.findByuser(userInfo2);
			UserAddress userAddress = new UserAddress();
			if (optionalUserAddress.size()>=1) {
				for (int i = 0; i < optionalUserAddress.size(); i++) {
					if (optionalUserAddress.get(i).getAddressElement().equals(addressElement)) {
						userAddress=optionalUserAddress.get(i);
					}
					

				}
				if (userAddress.equals(null)) {
					UserAddress userAddress1 = new UserAddress();
					userAddress1.setAddress(request.getWoreda());
					userAddress1.setAddressElement(addressElement);
					userAddress1.setIsactive(true);
					userAddress1.setRegisteredDate(LocalDate.now());
					userAddress1.setUser(userInfo2);
					userAddressRepository.save(userAddress1);
				}
				else {
					userAddress.setAddress(request.getCity());
					userAddressRepository.save(userAddress);
				}
		
				


			}
			else {
				UserAddress userAddress1 = new UserAddress();
				userAddress1.setAddress(request.getWoreda());
				userAddress1.setAddressElement(addressElement);
				userAddress1.setIsactive(true);
				userAddress1.setRegisteredDate(LocalDate.now());
				userAddress1.setUser(userInfo2);
				userAddressRepository.save(userAddress1);

			}
			

			
//			UserAddress userAddress = new UserAddress();
////			AddressElement addressElement = addressElementRepository.findBytitle("WOREDA").get();

		}

		userRepository.save(userInfo2);
		if (request.getFileID() != null) {
			if (!documentTypeRepository.findByName(request.getIdType()).isPresent()) {
				return ResponseEntity.badRequest()
						.body(new ResponseError("No document type" + request.getIdType() + " found"));
			}
			String iduploadDir = idPath + userInfo2.getUsername();
			idName = StringUtils.cleanPath(request.getFileID().getOriginalFilename());
			UserInfoService.saveID(iduploadDir, idName, request.getFileID());
			String path = idPath + photoName;
			Document_Type document_Type = documentTypeRepository.findByName(request.getIdType()).get();
			User_Document user_Document = new User_Document();
			user_Document.setDocumentType(document_Type);
			user_Document.setDocumentPath(path);
			user_Document.setIs_valid(false);
			user_Document.setUploadeDate(LocalDate.now());
			user_Document.setUserInfo(userInfo2);
			userDocumentRepository.save(user_Document);

		}
		if (request.getFileImage() != null) {
			String photouploadDir = photoPath + userInfo2.getUsername();
			photoName = StringUtils.cleanPath(request.getFileImage().getOriginalFilename());
			UserInfoService.savePhoto(photouploadDir, photoName, request.getFileImage());
			String path = photoPath + photoName;
			Document_Type document_Type1 = documentTypeRepository.findByName("PHOTO").get();
			User_Document user_Document1 = new User_Document();
			user_Document1.setDocumentType(document_Type1);
			user_Document1.setDocumentPath(path);
			user_Document1.setIs_valid(false);
			user_Document1.setUploadeDate(LocalDate.now());
			user_Document1.setUserInfo(userInfo2);
			userDocumentRepository.save(user_Document1);

		}
		UserInfo userInfo = userRepository.findByUsername(username).get();
//		UserAddress userAddress = userAddressRepository.findByuser(userInfo).get();
//		User_Document user_Document = userDocumentRepository.findByUserInfo(userInfo).get();
		if (userInfo.getBirthDay() != null || userInfo.getEmail() != null || userInfo.getFatherName() != null
				|| userInfo.getGender() != null || userInfo.getLastname() != null || userInfo.getLevels() != null
				|| userInfo.getMotherName() != null || userInfo.getName() != null || userInfo.getRoles() != null
				|| userInfo.getUsername() != null) {
			userInfo.setLevels(Levels.LEVEL_2);
			userRepository.save(userInfo);

		}
		return ResponseEntity.ok(new UpdateResponse("Updated successfully"));
	}

	@PostMapping("/UpdateLevel")

	@ApiOperation(value = "LEVEL UP USER BY ADDING PHOTO AND ID")
	public ResponseEntity<?> updatePhoto(@ModelAttribute UserInfoUpdateRequest request, Authentication authentication)
			throws IOException {

		String photoName = StringUtils.cleanPath(request.getFileImage().getOriginalFilename());
		String idName = StringUtils.cleanPath(request.getFileID().getOriginalFilename());
		String userName = authentication.getName();
//		if(request.getUsername().isEmpty()|| request.getWoreda().isEmpty()|| request.getIdNumber().isEmpty()|| request.getSubCity().isEmpty()|| request.getIdType().isEmpty()|| request.getRegion().isEmpty()|| request.getLastName().isEmpty()|| request.getKebeleID().isEmpty()|| request.getHouseNo().isEmpty()|| request.getFirstName().isEmpty()||request.getGender().isEmpty()|| request.getEmail().isEmpty()|| request.getMotherName().isEmpty()|| photoName.isEmpty()|| idName.isEmpty()||request.getBirthDay().isEmpty()||request.getCity().isEmpty()) {
//			return ResponseEntity.badRequest().body(new ResponseError("Both ID and Photo needed for upgrade!"));
//		}
		UserInfo userInfo2 = userRepository.findByUsername(userName).get();
		if (request.getFileID() != null) {
			String iduploadDir = idPath + userInfo2.getUsername();
			idName = StringUtils.cleanPath(request.getFileID().getOriginalFilename());
			UserInfoService.saveID(iduploadDir, idName, request.getFileID());
			String path = idPath + photoName;
			Document_Type document_Type = documentTypeRepository.findByName(request.getIdType()).get();
			User_Document user_Document = new User_Document();
			user_Document.setDocumentType(document_Type);
			user_Document.setDocumentPath(path);
			user_Document.setIs_valid(false);
			user_Document.setUploadeDate(LocalDate.now());
			user_Document.setUserInfo(userInfo2);
			userDocumentRepository.save(user_Document);

		}
		if (request.getBirthDay() != null) {
			userInfo2.setBirthDay(request.getBirthDay());
		}

		if (request.getMotherName() != null) {
			userInfo2.setMotherName(request.getMotherName());
		}
		if (request.getFatherName() != null) {
			userInfo2.setFatherName(request.getFatherName());
		}
		if (request.getName() != null) {
			userInfo2.setName(request.getName());
		}
		if (request.getGender() != null) {
			userInfo2.setGender(request.getGender());
		}

		if (request.getFileImage() != null) {
			String photouploadDir = photoPath + userInfo2.getUsername();
			photoName = StringUtils.cleanPath(request.getFileImage().getOriginalFilename());
			UserInfoService.savePhoto(photouploadDir, photoName, request.getFileImage());
			String path = photoPath + photoName;
			Document_Type document_Type1 = documentTypeRepository.findByName("Photo").get();
			User_Document user_Document1 = new User_Document();
			user_Document1.setDocumentType(document_Type1);
			user_Document1.setDocumentPath(path);
			user_Document1.setIs_valid(false);
			user_Document1.setUploadeDate(LocalDate.now());
			user_Document1.setUserInfo(userInfo2);
			userDocumentRepository.save(user_Document1);

		}

//		if (request.getIdType()!=null) {
//			userInfo2.setIdType(request.getIdType());
//			
//		}

//		if (request.getIdNumber()!=null) {
//			userInfo2.setIdNumber(request.getIdNumber());
//			
//		}

		userRepository.save(userInfo2);
		String photouploadDir = photoPath + userInfo2.getUsername();

		return ResponseEntity.badRequest().body(new ResponseError("Your Level is updated! Now you are Level 3"));
	}

//	@PostMapping("/UpdateLevel")
//
//	@ApiOperation(value ="LEVEL UP USER BY ADDING PHOTO AND ID")
//	public ResponseEntity<?> updatePhoto(@RequestParam("fileImage") MultipartFile photo,@RequestParam("fileID") MultipartFile id	,Authentication authentication ) throws IOException {
//		
//		String photoName = StringUtils.cleanPath(photo.getOriginalFilename());
//		String idName = StringUtils.cleanPath(id.getOriginalFilename());
//		String userName = authentication.getName();
//		if(photoName.isEmpty()|| idName.isEmpty()) {
//			return ResponseEntity.badRequest().body(new ResponseError("Both ID and Photo needed for upgrade!"));
//		}
//		
//		UserInfo userInfo2 = userRepository.findByUsername(userName).get();
//		
//		if (!userInfo2.getPhoto().isEmpty() || !userInfo2.getKebeleID().isEmpty()) {
//			return ResponseEntity.badRequest().body(new ResponseError("User Already Updated"));
//		}
////		userRepository.save(userInfo2);
//		userInfo2.setPhoto(photoName);
//		userInfo2.setKebeleID(idName);
//		userInfo2.setLevels(Levels.LEVEL_3);
//		
//		userRepository.save(userInfo2);
//		String photouploadDir = photoPath + userInfo2.getUsername();
//		String iduploadDir = idPath + userInfo2.getUsername();
//		
//		UserInfoService.savePhoto(photouploadDir, photoName, photo);
//		UserInfoService.saveID(iduploadDir, idName, id);
//		
//		return ResponseEntity.badRequest().body(new ResponseError("Your Level is updated! Now you are Level 3"));
//	}

}
