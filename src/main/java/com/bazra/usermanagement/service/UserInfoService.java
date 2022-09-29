package com.bazra.usermanagement.service;

import static java.lang.Character.isUpperCase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bazra.usermanagement.model.AdminInfo;
import com.bazra.usermanagement.model.AgentInfo;
import com.bazra.usermanagement.model.MasterAgentInfo;
import com.bazra.usermanagement.model.MerchantInfo;
import com.bazra.usermanagement.model.MyUserDetails;
import com.bazra.usermanagement.model.UserAuthentication;
import com.bazra.usermanagement.model.UserCredential;
import com.bazra.usermanagement.model.UserInfo;
import com.bazra.usermanagement.repository.AccountRepository;
import com.bazra.usermanagement.repository.AdminRepository;
import com.bazra.usermanagement.repository.AgentRepository;
import com.bazra.usermanagement.repository.MasterAgentRepository;
import com.bazra.usermanagement.repository.MerchantRepository;
import com.bazra.usermanagement.repository.UserAuthenticationRepository;
import com.bazra.usermanagement.repository.UserCredentialRepository;
import com.bazra.usermanagement.repository.UserRepository;
import com.bazra.usermanagement.request.SignUpRequest;
import com.bazra.usermanagement.request.UpdateRequest;
import com.bazra.usermanagement.request.ValidatePassRequest;
import com.bazra.usermanagement.response.ResponseError;
import com.bazra.usermanagement.response.SignUpResponse;
import com.bazra.usermanagement.response.SigninErrorResponse;
import com.bazra.usermanagement.response.UpdateResponse;

import java.io.*;
import java.nio.file.*;

/**
 * User information service
 * 
 * @author Bemnet
 * @version 4/2022
 *
 */
@Service
public class UserInfoService implements UserDetailsService {

	@Autowired
	UserRepository userRepository;
	@Autowired
	AgentRepository agentRepository;

	@Autowired
	AccountService accountservice;
	@Autowired
	AccountRepository accountRepository;
	@Autowired
	MerchantRepository merchantRepository;
	@Autowired
	MasterAgentRepository masterAgentRepository;
	@Autowired
	AdminRepository adminRepository;
	@Autowired
	UserAuthenticationRepository userAuthenticationRepository;
	@Autowired
	UserCredentialRepository userCredentialRepository;
	

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		String password = "";
		if (agentRepository.findByUsername(email).isPresent()) {
			AgentInfo agentInfo = agentRepository.findByUsername(email).get();
			password = agentInfo.getPassword();
			return new User(email, password, new ArrayList<>());
		} else if (merchantRepository.findByUsername(email).isPresent()) {
			MerchantInfo merchantInfo = merchantRepository.findByUsername(email).get();
			password = merchantInfo.getPassword();
			return new User(email, password, new ArrayList<>());

		} else if (masterAgentRepository.findByuserName(email).isPresent()) {
			MasterAgentInfo masterAgentInfo = masterAgentRepository.findByuserName(email).get();
			password = masterAgentInfo.getPassword();
			return new User(email, password, new ArrayList<>());

		} else if (adminRepository.findByUsername(email).isPresent()) {
			AdminInfo adminInfo = adminRepository.findByUsername(email).get();
			password = adminInfo.getPassword();
			return new User(email, password, new ArrayList<>());

		}
		Optional<UserInfo> userinfOptional = userRepository.findByUsername(email);

		UserInfo userInfo = userRepository.findByUsername(email).get();
		UserAuthentication userAuthentication = userAuthenticationRepository.findByuserInfo(userInfo).get();
		password = userAuthentication.getAuthenticationValue();
		UserCredential userCredential = userCredentialRepository.findByUserInfo(userInfo).get();
		email=userCredential.getLoginID();
		return new MyUserDetails(userInfo,userAuthentication,userCredential);

	}

//    public UserDetails loadAgentByUsername(String email) throws UsernameNotFoundException {
//
//        AgentInfo agentInfo = agentRepository.findByUsername(email).get();
//        String passwor = agentInfo.getPassword();
//
//        return new User(email, passwor, new ArrayList<>());
//
//    }

	// this is birthday checker for only integer value present
	public boolean checkBirthdate(String input) {
		char currentCharacter;
		boolean numberPresent = false;
		boolean lowerCasePresent = true;
		boolean upperCasePresent = true;
		for (int i = 1; i < input.length(); i++) {
			currentCharacter = input.charAt(i);
			if (Character.isDigit(currentCharacter)) {
				numberPresent = true;
			} else if (isUpperCase(currentCharacter)) {
				upperCasePresent = false;
			} else if (Character.isLowerCase(currentCharacter)) {
				lowerCasePresent = false;
			}
		}
		return numberPresent && upperCasePresent && lowerCasePresent;
	}

	// this checks user phone number
	public boolean checkUsername(String input) {
		char currentCharacter;
		boolean numberPresent = false;
		boolean lowerCasePresent = true;
		boolean upperCasePresent = true;
		for (int i = 0; i < input.length(); i++) {
			currentCharacter = input.charAt(i);

			if (Character.isDigit(currentCharacter)) {
				numberPresent = true;
			} else if (isUpperCase(currentCharacter)) {
				upperCasePresent = false;
			} else if (Character.isLowerCase(currentCharacter)) {
				lowerCasePresent = false;
			}
		}
		return numberPresent && upperCasePresent && lowerCasePresent;
	}

	public String removeFirst(String str) {

		// Removing first and last character
		// of a string using substring() method
		str = str.substring(1, str.length());

		// Return the modified string
		return str;
	}

	// this for first name
	public boolean checkname(String input) {
		char currentCharacter;
		boolean numberPresent = true;
		boolean lowerCasePresent = false;
		boolean upperCasePresent = true;
		for (int i = 4; i < input.length(); i++) {
			currentCharacter = input.charAt(i);
			if (Character.isDigit(currentCharacter)) {
				numberPresent = false;
			} else if (isUpperCase(currentCharacter)) {
				upperCasePresent = false;
			} else if (Character.isLowerCase(currentCharacter)) {
				lowerCasePresent = true;
			}
		}
		return numberPresent && upperCasePresent && lowerCasePresent;
	}

	// this function checks lastName inputs
	public boolean checkLastname(String input) {
		char currentCharacter;
		boolean numberPresent = true;
		boolean lowerCasePresent = false;
		boolean upperCasePresent = true;
		for (int i = 4; i < input.length(); i++) {
			currentCharacter = input.charAt(i);
			if (Character.isDigit(currentCharacter)) {
				numberPresent = false;
			} else if (isUpperCase(currentCharacter)) {
				upperCasePresent = false;
			} else if (Character.isLowerCase(currentCharacter)) {
				lowerCasePresent = true;
			}
		}
		return numberPresent && lowerCasePresent && upperCasePresent;
	}

	// this for password checker
	public boolean checkString(String input) {
		char currentCharacter;
		boolean numberPresent = false;
		boolean upperCasePresent = false;
		boolean lowerCasePresent = false;

		for (int i = 0; i < input.length(); i++) {
			currentCharacter = input.charAt(i);
			if (Character.isDigit(currentCharacter)) {
				numberPresent = true;
			} else if (isUpperCase(currentCharacter)) {
				upperCasePresent = true;
			} else if (Character.isLowerCase(currentCharacter)) {
				lowerCasePresent = true;
			}
		}

		return numberPresent && upperCasePresent && lowerCasePresent;
	}

	// this checks email
	public static boolean valEmail(String input) {
		String emailRegex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\\\.[A-Z]{2,6}$";
		Pattern emailPat = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = emailPat.matcher(input);
		return matcher.find();

	}

	/**
	 * check of password validity
	 * 
	 * @param str
	 * @return
	 */
	public ResponseEntity<?> Validation(SignUpRequest request) {

		return ResponseEntity.badRequest().body(new SignUpResponse("Error: Invalid Password"));
	}

	/**
	 * validates field for user signup
	 * 
	 * @param userInfo
	 * @return signup status
	 */

	public static String NumericString(int n) {

		// chose a Character random from this String
		String AlphaNumericString = "0123456789";

		// create StringBuffer size of AlphaNumericString
		StringBuilder sb = new StringBuilder(n);

		for (int i = 0; i < n; i++) {

			// generate a random number between
			// 0 to AlphaNumericString variable length
			int index = (int) (AlphaNumericString.length() * Math.random());

			// add Character one by one in end of sb
			sb.append(AlphaNumericString.charAt(index));
		}

		return sb.toString();
	}

	/**
	 * update password
	 * 
	 * @param request
	 * @param newpassword
	 * @return update status
	 */
	public ResponseEntity<?> updatePassword(UpdateRequest request, String newpassword, String onldpass) {

		Optional<UserInfo> optional = userRepository.findByUsername(request.getUsername());
		UserInfo userInfo = optional.get();
		Optional<UserAuthentication> userauthOptional = userAuthenticationRepository.findByuserInfo(userInfo);
		if (!userauthOptional.isPresent()) {
			return ResponseEntity.badRequest().body(new SigninErrorResponse("Error: No valid user"));
		}
		UserAuthentication userAuthentication = userauthOptional.get();

		userAuthentication.setAuthenticationValue(newpassword);
		userRepository.save(userInfo);

		return ResponseEntity.ok(new UpdateResponse("successfully updated password"));

	}

	public static ResponseEntity<?> savePhoto(String photouploadDir, String photoName, MultipartFile photo)
			throws IOException {
		Path uploadPath = Paths.get(photouploadDir);

		if (!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}

		try (InputStream inputStream = photo.getInputStream()) {
			Path filePath = uploadPath.resolve(photoName);
			Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
			return ResponseEntity.badRequest().body(new ResponseError("Uploaded photo successfully!"));

		} catch (IOException ioe) {
			return ResponseEntity.badRequest().body(new ResponseError("Could not save image file"));
		}
	}

	public static ResponseEntity<?> saveID(String uploadDir, String fileName, MultipartFile multipartFile)
			throws IOException {
		Path uploadPath = Paths.get(uploadDir);

		if (!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}

		try (InputStream inputStream = multipartFile.getInputStream()) {
			Path filePath = uploadPath.resolve(fileName);
			Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
			return ResponseEntity.badRequest().body(new ResponseError("Uploaded ID successfully!"));

		} catch (IOException ioe) {
			return ResponseEntity.badRequest().body(new ResponseError("Could not save image file"));
		}
	}

	public static ResponseEntity<?> saveTIN(String uploadDir, String fileName, MultipartFile multipartFile)
			throws IOException {
		Path uploadPath = Paths.get(uploadDir);

		if (!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}

		try (InputStream inputStream = multipartFile.getInputStream()) {
			Path filePath = uploadPath.resolve(fileName);
			Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
			return ResponseEntity.badRequest().body(new ResponseError("Uploaded TIN successfully!"));

		} catch (IOException ioe) {
			return ResponseEntity.badRequest().body(new ResponseError("Could not save file"));
		}
	}

	public static ResponseEntity<?> saveTREAD(String uploadDir, String fileName, MultipartFile multipartFile)
			throws IOException {
		Path uploadPath = Paths.get(uploadDir);

		if (!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}

		try (InputStream inputStream = multipartFile.getInputStream()) {
			Path filePath = uploadPath.resolve(fileName);
			Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
			return ResponseEntity.badRequest().body(new ResponseError("Uploaded TREAD successfully!"));

		} catch (IOException ioe) {
			return ResponseEntity.badRequest().body(new ResponseError("Could not save  file"));
		}
	}

}
