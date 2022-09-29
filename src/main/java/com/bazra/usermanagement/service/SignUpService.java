package com.bazra.usermanagement.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.bazra.usermanagement.model.Account;
import com.bazra.usermanagement.model.AccountBalance;
import com.bazra.usermanagement.model.AccountType;
import com.bazra.usermanagement.model.AdminInfo;
import com.bazra.usermanagement.model.AgentInfo;
import com.bazra.usermanagement.model.AuthenticationType;
import com.bazra.usermanagement.model.BazraBalance;
import com.bazra.usermanagement.model.Levels;
import com.bazra.usermanagement.model.LoginIDType;
import com.bazra.usermanagement.model.MasterAgentInfo;
import com.bazra.usermanagement.model.MerchantInfo;
import com.bazra.usermanagement.model.Role;
import com.bazra.usermanagement.model.UserAuthentication;
import com.bazra.usermanagement.model.UserCredential;
import com.bazra.usermanagement.model.UserInfo;
import com.bazra.usermanagement.model.UserRoles;
import com.bazra.usermanagement.repository.AccountBalanceRepository;
import com.bazra.usermanagement.repository.AccountRepository;
import com.bazra.usermanagement.repository.AccountTypeRepository;
import com.bazra.usermanagement.repository.AdminRepository;
import com.bazra.usermanagement.repository.AgentRepository;
import com.bazra.usermanagement.repository.AuthTypeRepository;
import com.bazra.usermanagement.repository.BazraBalanceRepository;
import com.bazra.usermanagement.repository.LoginIdTypeRepository;
import com.bazra.usermanagement.repository.MasterAgentRepository;
import com.bazra.usermanagement.repository.MerchantRepository;
import com.bazra.usermanagement.repository.RoleRepository;
import com.bazra.usermanagement.repository.SecurityQuestionsRepository;
import com.bazra.usermanagement.repository.UserAuthenticationRepository;
import com.bazra.usermanagement.repository.UserCredentialRepository;
import com.bazra.usermanagement.repository.UserRepository;
import com.bazra.usermanagement.repository.UserRoleRepository;
import com.bazra.usermanagement.repository.UserSecurityRepository;
import com.bazra.usermanagement.request.AdminSignupRequest;
import com.bazra.usermanagement.request.AgentSignUpRequest;
import com.bazra.usermanagement.request.MasterSignupRequest;
import com.bazra.usermanagement.request.MasterSignupRequest2;
import com.bazra.usermanagement.request.SignUpPinRequest;
import com.bazra.usermanagement.request.SignUpPinVerificationRequest;
import com.bazra.usermanagement.request.SignUpRequest;
import com.bazra.usermanagement.response.AdminSignupResponse;
import com.bazra.usermanagement.response.MasterSignupResponse;
import com.bazra.usermanagement.response.ResponseError;
import com.bazra.usermanagement.response.SignUpResponse;
import com.bazra.usermanagement.response.UpdateResponse;

@Service
public class SignUpService {
	@Autowired
	UserRepository userRepository;
	@Autowired
	AgentRepository agentRepository;
	@Autowired
	MerchantRepository merchantRepository;
	@Autowired
	UserInfoService userInfoService;
	@Autowired
	AccountService accountservice;
	@Autowired
	AccountRepository accountRepository;
	@Autowired
	UserSecurityRepository userSecurityRepository;
	@Autowired
	AdminRepository adminRepository;
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	SecurityQuestionsRepository securityQuestionsRepository;
	@Autowired
	AccountTypeRepository accountTypeRepository;
	@Autowired
	MasterAgentRepository masterAgentRepository;
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	UserRoleRepository userRoleRepository;
	@Autowired
	AccountBalanceRepository accountBalanceRepository;
	@Autowired
	BazraBalanceRepository bazraBalanceRepository;
	@Autowired
	LoginIdTypeRepository loginIdTypeRepository;
	@Autowired
	AuthTypeRepository authTypeRepository;
	@Autowired
	UserCredentialRepository userCredentialRepository;
	@Autowired
	UserAuthenticationRepository userAuthenticationRepository;
	@Autowired
	private RandomNumber randomNumber;
	@Value("${tin.upload.path}")
	private String tinPath;

	@Value("${tread.upload.path}")
	private String treadPath;

	public ResponseEntity<?> adminSignup(AdminSignupRequest request) {
		String phone = "+251" + request.getUsername().substring(request.getUsername().length() - 9);
		boolean userExists1 = adminRepository.findByUsername(phone).isPresent();
//		if (!adminRepository.findAll().isEmpty()) {
//			return ResponseEntity.badRequest().body(new SignUpResponse("Only one Admin Account is permitted"));
//		}
		if (userExists1) {
			return ResponseEntity.badRequest().body(new SignUpResponse("User already exists "));
		}
		String pass1 = request.getPassword();

		if (!userInfoService.checkString(pass1)) {
			return ResponseEntity.badRequest().body(
					new SignUpResponse("Your password must have atleast 1 number, 1 uppercase and 1 lowercase letter"));
		} else if (pass1.chars().filter(ch -> ch != ' ').count() < 8
				|| pass1.chars().filter(ch -> ch != ' ').count() > 15) {
			return ResponseEntity.badRequest().body(new SignUpResponse("Your password must have 8 to 15 characters "));
		}

		if (request.getUsername().isBlank()) {

			return ResponseEntity.badRequest().body(new SignUpResponse("Enter your phone "));
		}
//		if (!roleRepository.findByrolename("ADMIN").isPresent()) {
//			return ResponseEntity.badRequest().body(new UpdateResponse("No such Role found"));
//		}
		if ((!userInfoService.checkUsername(request.getUsername()))) {

			return ResponseEntity.badRequest()
					.body(new SignUpResponse("Phone Number Should be Integer Only and Minimum 10 Digit"));
		}

		AdminInfo adminInfo = new AdminInfo(phone, passwordEncoder.encode(request.getPassword()));
		adminInfo.setUsername(phone);
		adminInfo.setRoles("ADMIN");
		adminRepository.save(adminInfo);
		Optional<AdminInfo> adminOptional = adminRepository.findByUsername(phone);

		Optional<AccountType> accOptional = accountTypeRepository.findByaccounttype("ADMIN");
		if (!accOptional.isPresent()) {
			AdminInfo adminInfo2 = adminOptional.get();
			AccountType accountType = new AccountType();
			accountType.setAccounttype("ADMIN");
			accountType.setAdminInfo(adminInfo2);
			accountType.setCreatedDateTime(LocalDate.now());
			accountTypeRepository.save(accountType);

		}
		Optional<AccountType> accOptionall = accountTypeRepository.findByaccounttype("ADMIN");
		AccountType accountType = accOptionall.get();

		AdminInfo adminInfo2 = adminRepository.findByUsername(phone).get();
		Optional<Role> roleOptional = roleRepository.findByrolename("ADMIN");
		if (!roleOptional.isPresent()) {
			Role role = new Role();
			role.setAdmin(adminInfo);
			role.setCreatedDate(LocalDate.now());
			role.setRolename("ADMIN");
			roleRepository.save(role);
		}

		UserRoles userRoles = new UserRoles();
		userRoles.setCreated_date(LocalDate.now());
		userRoles.setRole(roleRepository.findByrolename("ADMIN").get());
		userRoles.setAdminInfo(adminInfo2);
		userRoleRepository.save(userRoles);
		Account account = new Account();
		
		accountTypeRepository.save(accountType);
		AccountType accounttype = accountTypeRepository.findByaccounttype("ADMIN").get();
		account.setAccountNumber(phone);
		account.setCreationDate(LocalDate.now());
		account.setType(accountType);
		account.setAdmin(adminInfo2);
		account.setStatus(true);
		account.setType(accounttype);
		accountRepository.save(account);
		Account account2 = accountRepository.findByAdmin(adminInfo2).get();
		BazraBalance accountBalance = new BazraBalance();
		accountBalance.setBalance(new BigDecimal(1000));
		accountBalance.setAccount(account2);
		accountBalance.setCreateDate(LocalDate.now());
		bazraBalanceRepository.save(accountBalance);
		

		return ResponseEntity.ok(new AdminSignupResponse(adminInfo2.getUsername(), "Successfully Registered"));
	}

	public ResponseEntity<?> createWallet(SignUpRequest request) {
		if (request.getPhone()==null) {
			return ResponseEntity.badRequest().body(new SignUpResponse("Enter your phone number "));
		}
		if (request.getEmail()==null) {
			return ResponseEntity.badRequest().body(new SignUpResponse("Enter your email "));
		}
		if (request.getFirstName()==null) {

			return ResponseEntity.badRequest().body(new SignUpResponse("Enter your name "));
		}
		if (request.getFatheName()==null) {
			return ResponseEntity.badRequest().body(new SignUpResponse("Enter your father's name "));
		}
		if (request.getMotherName()==null) {
			return ResponseEntity.badRequest().body(new SignUpResponse("Enter your mother's name "));
		}
		if (request.getPhone()==null) {
			return ResponseEntity.badRequest().body(new SignUpResponse("Enter your phone number "));
		}
		
		if ((!userInfoService.checkBirthdate(request.getBirthDay()))) {

			return ResponseEntity.badRequest().body(new SignUpResponse("Invalid date value"));
		}
		
	
		String username = "+251" + request.getPhone().substring(request.getPhone().length() - 9);
		Optional<UserInfo> userOptional = userRepository.findByUsername(username);
		boolean userExists1 = userOptional.isPresent();
//		boolean userExist1 = userRepository.findByEmail(request.getEmail()).isPresent();
//		boolean agentExist1 = agentRepository.findByEmail(request.getEmail()).isPresent();
//		boolean merchantExist1 = merchantRepository.findByEmail(request.getEmail()).isPresent();
		
		boolean roleexist = roleRepository.findByrolename("USER").isPresent();
		if (!roleexist) {
			return ResponseEntity.badRequest().body(new SignUpResponse("Role not defined "));
		}
		Role role = roleRepository.findByrolename("USER").get();
//		if (userExist1 || agentExist1 || merchantExist1 ) {
//			return ResponseEntity.badRequest().body(new SignUpResponse("Error: Email already in use!"));
//		}
		if (!userExists1) {
			return ResponseEntity.badRequest().body(new SignUpResponse("No User found "));
		}
		if ((!userInfoService.checkname(request.getFirstName()))) {

			return ResponseEntity.badRequest().body(new SignUpResponse(
					"First Name Should Start with One Uppercase and LoweCase letter, Minimum input 4 character and  String Character only"));
		}
		if ((!userInfoService.checkLastname(request.getFatheName()))) {

			return ResponseEntity.badRequest().body(new SignUpResponse(
					"FatherName Should Start with One Uppercase and LoweCase letter, Minimum input 4 character and  String Character only"));
		}
		if ((!userInfoService.checkLastname(request.getMotherName()))) {

			return ResponseEntity.badRequest().body(new SignUpResponse(
					"MotherName Should Start with One Uppercase and LoweCase letter, Minimum input 4 character and  String Character only"));
		}
//		
		if (request.getEmail().isBlank()) {
			return ResponseEntity.badRequest().body(new SignUpResponse("Enter your email "));
		}
		if (request.getFirstName().isBlank()) {

			return ResponseEntity.badRequest().body(new SignUpResponse("Enter your name "));
		}
		if (request.getFatheName().isBlank()) {
			return ResponseEntity.badRequest().body(new SignUpResponse("Enter your father's name "));
		}
		if (request.getMotherName().isBlank()) {
			return ResponseEntity.badRequest().body(new SignUpResponse("Enter your mother's name "));
		}
		if (request.getPhone().isBlank()) {
			return ResponseEntity.badRequest().body(new SignUpResponse("Enter your phone number "));
		}
		
		if ((!userInfoService.checkBirthdate(request.getBirthDay()))) {

			return ResponseEntity.badRequest().body(new SignUpResponse("Invalid date value"));
		}
		
		
		Optional<AccountType> accOptional = accountTypeRepository.findByaccounttype("USER");
		if (!accOptional.isPresent()) {
			return ResponseEntity.badRequest().body(new UpdateResponse("No Account Type found"));
		}
		

		UserInfo userInfo = userOptional.get();
		userInfo.setBirthDay(request.getBirthDay());
		userInfo.setEmail(request.getEmail());
		userInfo.setFatherName(request.getFatheName());
		userInfo.setMotherName(request.getMotherName());
		userInfo.setRoles("USER");
		userInfo.setLevels(Levels.LEVEL_1);
		userInfo.setName(request.getFirstName());
		
		userRepository.save(userInfo);
		UserInfo userInfo2 = userRepository.findByUsername(username).get();
		UserRoles userRoles = new UserRoles();
		userRoles.setCreated_date(LocalDate.now());
		userRoles.setRole(roleRepository.findByrolename("USER").get());
		userRoles.setUserInfo(userInfo2);
		userRoleRepository.save(userRoles);

		AccountType accountType = accOptional.get();
		Account account = new Account();
		AccountBalance accountBalance = new AccountBalance();
		account.setUser(userInfo2);
		account.setType(accountType);
		account.setAccountNumber(userInfo2.getUsername());
		accountRepository.save(account);
		accountBalance.setAccount(account);
		accountBalance.setBalance(new BigDecimal(1000));
		accountBalance.setCreateDate(LocalDate.now());
		accountBalanceRepository.save(accountBalance);
		return ResponseEntity
				.ok(new SignUpResponse(userInfo2.getName(), userInfo2.getRoles(),
						"Successfully Registered", userInfo2.getName(), userInfo2.getFatherName(), userInfo.getLevels()));
	}

	public ResponseEntity<?> signUpAgent(AgentSignUpRequest request) {
		String phone = "+251" + request.getUsername().substring(request.getUsername().length() - 9);
		boolean userExists1 = agentRepository.findByUsername(phone).isPresent();
//        Aagent = accountRepository.findByusername(request.getUsername());
		if (userExists1) {

			return ResponseEntity.badRequest().body(new SignUpResponse("Error: Username is already taken!"));
		}
		String pass1 = request.getPassword();
		String pass2 = request.getConfirmPassword();

		if (!pass1.matches(pass2)) {
			return ResponseEntity.badRequest().body(new SignUpResponse("Error: Passwords don't match!"));
		}
		if (accountRepository.findByAccountNumberEquals(phone).isPresent()) {
			return ResponseEntity.badRequest().body(new SignUpResponse("Account already taken"));
		}

		if (!userInfoService.checkString(pass1)) {
			return ResponseEntity.badRequest().body(
					new SignUpResponse("Your password must have atleast 1 number, 1 uppercase and 1 lowercase letter"));
		} else if (pass1.chars().filter(ch -> ch != ' ').count() < 8
				|| pass1.chars().filter(ch -> ch != ' ').count() > 15) {
			return ResponseEntity.badRequest().body(new SignUpResponse("Your password must have 8 to 15 characters "));
		}

		if ((!userInfoService.checkname(request.getFirstName()))) {

			return ResponseEntity.badRequest().body(new SignUpResponse(
					"First Name Should Start with One Uppercase and LoweCase letter, Minimum input 4 character and  String Character only"));
		}
		if ((!userInfoService.checkLastname(request.getLastName()))) {

			return ResponseEntity.badRequest().body(new SignUpResponse(
					"FatherName Should Start with One Uppercase and LoweCase letter, Minimum input 4 character and  String Character only"));
		}

		if (request.getFirstName().isBlank()) {

			return ResponseEntity.badRequest().body(new SignUpResponse("Enter your name "));
		}
		if (request.getLastName().isBlank()) {

			return ResponseEntity.badRequest().body(new SignUpResponse("Enter your father name "));
		}
		if (request.getUsername().isBlank()) {

			return ResponseEntity.badRequest().body(new SignUpResponse("Enter your phone "));
		}
		if (request.getLicenceNumber().isBlank()) {

			return ResponseEntity.badRequest().body(new SignUpResponse("Enter your licence number "));
		}
		if (request.getBusinessLNum().isBlank()) {

			return ResponseEntity.badRequest().body(new SignUpResponse("Enter your business licence number "));
		}

		if ((!userInfoService.checkUsername(request.getUsername()))) {

			return ResponseEntity.badRequest()
					.body(new SignUpResponse("Phone Number Should be Integer Only and Minimum 10 Digit"));
		}

		
		AgentInfo agentInfo = new AgentInfo(request.getFirstName(), request.getLastName(),
				passwordEncoder.encode(request.getPassword()), phone);
		agentInfo.setLicenceNumber(request.getLicenceNumber());
		agentInfo.setCompanyName(request.getCompanyName());
		agentInfo.setBusinessLNum(request.getBusinessLNum());
		agentInfo.setRoles("AGENT");

		agentRepository.save(agentInfo);
		Optional<AccountType> accOptional = accountTypeRepository.findByaccounttype("AGENT");
		if (!accOptional.isPresent()) {
			return ResponseEntity.badRequest().body(new UpdateResponse("No Account Type found "));
		}
		AccountType accountType = accOptional.get();
		AgentInfo agentInfo2 = agentRepository.findByUsername(phone).get();
		UserRoles userRoles = new UserRoles();
		userRoles.setCreated_date(LocalDate.now());
		userRoles.setRole(roleRepository.findByrolename("AGENT").get());
		userRoles.setAgentInfo(agentInfo2);
		userRoleRepository.save(userRoles);
		Account account = new Account();
		account.setAccountNumber(phone);
		account.setAgent(agentInfo2);
		account.setCommission(new BigDecimal(0));
		account.setStatus(true);
		account.setCreationDate(LocalDate.now());
		account.setType(accountType);
		
		accountRepository.save(account);
		Account account2 = accountRepository.findByAccountNumberEquals(phone).get();
		AccountBalance accountBalance = new AccountBalance();
		accountBalance.setBalance(new BigDecimal(1000));
		accountBalance.setAccount(account2);
		accountBalance.setCreateDate(LocalDate.now());
		accountBalanceRepository.save(accountBalance);
		return ResponseEntity.ok(new SignUpResponse(phone, agentInfo2.getRoles(),
				"Successfully Registered", agentInfo2.getFirstName(), agentInfo2.getLastName()));
	}

	public ResponseEntity<?> signUpMerchant(AgentSignUpRequest request) {
		String phone = "+251" + request.getUsername().substring(request.getUsername().length() - 9);
		boolean userExists1 = merchantRepository.findByUsername(phone).isPresent();
//        Aagent = accountRepository.findByusername(request.getUsername());
		if (userExists1) {

			return ResponseEntity.badRequest().body(new SignUpResponse("Error: Username is already taken!"));
		}
		String pass1 = request.getPassword();
		String pass2 = request.getConfirmPassword();

		if (!pass1.matches(pass2)) {
			return ResponseEntity.badRequest().body(new SignUpResponse("Error: Passwords don't match!"));
		}
		if (accountRepository.findByAccountNumberEquals(phone).isPresent()) {
			return ResponseEntity.badRequest().body(new SignUpResponse("Account already taken"));
		}

		if (!userInfoService.checkString(pass1)) {
			return ResponseEntity.badRequest().body(
					new SignUpResponse("Your password must have atleast 1 number, 1 uppercase and 1 lowercase letter"));
		} else if (pass1.chars().filter(ch -> ch != ' ').count() < 8
				|| pass1.chars().filter(ch -> ch != ' ').count() > 15) {
			return ResponseEntity.badRequest().body(new SignUpResponse("Your password must have 8 to 15 characters "));
		}

		if ((!userInfoService.checkname(request.getFirstName()))) {

			return ResponseEntity.badRequest().body(new SignUpResponse(
					"First Name Should Start with One Uppercase and LoweCase letter, Minimum input 4 character and  String Character only"));
		}
		if ((!userInfoService.checkLastname(request.getLastName()))) {

			return ResponseEntity.badRequest().body(new SignUpResponse(
					"FatherName Should Start with One Uppercase and LoweCase letter, Minimum input 4 character and  String Character only"));
		}

		if (request.getFirstName().isBlank()) {

			return ResponseEntity.badRequest().body(new SignUpResponse("Enter your name "));
		}
		if (request.getLastName().isBlank()) {

			return ResponseEntity.badRequest().body(new SignUpResponse("Enter your father name "));
		}
		if (request.getUsername().isBlank()) {

			return ResponseEntity.badRequest().body(new SignUpResponse("Enter your phone "));
		}
		if (request.getLicenceNumber().isBlank()) {

			return ResponseEntity.badRequest().body(new SignUpResponse("Enter your licence number "));
		}
		if (request.getBusinessLNum().isBlank()) {

			return ResponseEntity.badRequest().body(new SignUpResponse("Enter your business licence number "));
		}

		if ((!userInfoService.checkUsername(request.getUsername()))) {

			return ResponseEntity.badRequest()
					.body(new SignUpResponse("Phone Number Should be Integer Only and Minimum 10 Digit"));
		}


		MerchantInfo merchantInfo = new MerchantInfo(request.getFirstName(), request.getLastName(),
				passwordEncoder.encode(request.getPassword()), phone);
		merchantInfo.setLicenceNumber(request.getLicenceNumber());
		merchantInfo.setCompanyName(request.getCompanyName());
		merchantInfo.setBusinessLNum(request.getBusinessLNum());
		merchantInfo.setRoles("MERCHANT");

		merchantRepository.save(merchantInfo);

		Optional<AccountType> accOptional = accountTypeRepository.findByaccounttype("MERCHANT");
		if (!accOptional.isPresent()) {
			return ResponseEntity.badRequest().body(new UpdateResponse("No Account Type found "));
		}
		AccountType accountType = accOptional.get();
		MerchantInfo merchantInfo1 = merchantRepository.findByUsername(phone).get();
		UserRoles userRoles = new UserRoles();
		userRoles.setCreated_date(LocalDate.now());
		userRoles.setRole(roleRepository.findByrolename("MERCHANT").get());
		userRoles.setMerchantInfo(merchantInfo1);
		userRoleRepository.save(userRoles);
		Account account = new Account();
		account.setAccountNumber(phone);
		account.setMerchant(merchantInfo1);
		account.setCommission(new BigDecimal(0));
		account.setStatus(true);
		account.setCreationDate(LocalDate.now());
		account.setType(accountType);

		accountRepository.save(account);
		Account account2 = accountRepository.findByAccountNumberEquals(phone).get();
		AccountBalance accountBalance = new AccountBalance();
		accountBalance.setBalance(new BigDecimal(1000));
		accountBalance.setAccount(account2);
		accountBalance.setCreateDate(LocalDate.now());
		accountBalanceRepository.save(accountBalance);
		return ResponseEntity.ok(new SignUpResponse(phone, merchantInfo1.getRoles(),
				"Successfully Registered", merchantInfo1.getFirstName(), merchantInfo1.getLastName()));
	}

	public ResponseEntity<?> signUpMaster(MasterSignupRequest request) {
		boolean userExists1 = masterAgentRepository.findByphoneNumber(request.getPhone()).isPresent();

		if (userExists1) {

			return ResponseEntity.badRequest().body(new SignUpResponse("Error: Username is already taken!"));
		}

		if (request.getPhone().isBlank()) {

			return ResponseEntity.badRequest().body(new SignUpResponse("Enter your phone number"));
		}

		if (request.getUsername().isBlank()) {

			return ResponseEntity.badRequest().body(new SignUpResponse("Enter your user name"));
		}

		if (request.getEmail().isBlank()) {

			return ResponseEntity.badRequest().body(new SignUpResponse("Enter your email"));
		}

		if (request.getRegion().isBlank()) {

			return ResponseEntity.badRequest().body(new SignUpResponse("Enter your region"));
		}

		if (request.getCity().isBlank()) {

			return ResponseEntity.badRequest().body(new SignUpResponse("Enter your city"));
		}

		if (request.getOrganizationType().isBlank()) {

			return ResponseEntity.badRequest().body(new SignUpResponse("Enter your organization type"));
		}

		MasterAgentInfo masterAgentInfo = new MasterAgentInfo(request.getPhone(), request.getUsername(),
				passwordEncoder.encode(request.getPassword()), request.getEmail(), request.getRegion(),
				request.getCity(), request.getOrganizationType());

		if ((!userInfoService.checkUsername(masterAgentInfo.getPhoneNumber()))) {

			return ResponseEntity.badRequest()
					.body(new SignUpResponse("Phone Number Should be Integer Only and Minimum 10 Digit"));
		}

		if (accountRepository.findByAccountNumberEquals(request.getPhone()).isPresent()) {
			return ResponseEntity.badRequest().body(new SignUpResponse("Account already taken"));
		}

		masterAgentInfo.setRoles("MASTER");
		Optional<AccountType> accOptional = accountTypeRepository.findByaccounttype("MASTER");
		if (!accOptional.isPresent()) {
			return ResponseEntity.badRequest().body(new UpdateResponse("No Account Type found "));
		}
		AccountType accountType = accOptional.get();
		masterAgentRepository.save(masterAgentInfo);
		Account account = new Account(masterAgentInfo.getPhoneNumber(), masterAgentInfo.getUsername());
		account.setType(accountType);
		accountservice.save(account);
		AccountBalance accountBalance = new AccountBalance();
		accountBalance.setAccount(account);
		accountBalance.setBalance(new BigDecimal(1000));
		accountBalance.setCreateDate(LocalDate.now());
		accountBalanceRepository.save(accountBalance);
		return ResponseEntity.ok(new MasterSignupResponse("Successfully Registered", masterAgentInfo.getUsername(),
				masterAgentInfo.getRoles(), masterAgentInfo.getEmail(), masterAgentInfo.getOrganizationType()));
		// return ResponseEntity.badRequest().body(new SignUpResponse("Enter your
		// business licence number "));
	}

	public ResponseEntity<?> signUpMaster2(MasterSignupRequest2 request, Authentication authentication,
			MultipartFile tin, MultipartFile tread) throws IOException {
		boolean userExists1 = masterAgentRepository.findByuserName(authentication.getName()).isPresent();

		if (!userExists1) {

			return ResponseEntity.badRequest().body(new SignUpResponse("No such user"));
		}
		if (!request.getTinNumber().isEmpty()) {
			return ResponseEntity.badRequest().body(new SignUpResponse("Already passed this stage"));
		}

		MasterAgentInfo masterAgentInfo = masterAgentRepository.findByuserName(authentication.getName()).get();
		if (request.getOrganizationName().isBlank()) {

			return ResponseEntity.badRequest().body(new SignUpResponse("Enter your Organization Name"));
		}

		if (request.getOrganizationType().isBlank()) {

			return ResponseEntity.badRequest().body(new SignUpResponse("Enter your organization type"));
		}

		if (request.getBusinessSector().isBlank()) {

			return ResponseEntity.badRequest().body(new SignUpResponse("Enter your business sector"));
		}

		if (request.getBusinessType().isBlank()) {

			return ResponseEntity.badRequest().body(new SignUpResponse("Enter your business type"));
		}

		if (request.getTinNumber().isBlank()) {

			return ResponseEntity.badRequest().body(new SignUpResponse("Enter your tin number"));
		}

		if (request.getCapital().equals(null)) {

			return ResponseEntity.badRequest().body(new SignUpResponse("Enter your capital"));
		}

		String tinName = StringUtils.cleanPath(tin.getOriginalFilename());
		String treadName = StringUtils.cleanPath(tread.getOriginalFilename());

		if (tinName.isEmpty()) {
			return ResponseEntity.badRequest().body(new ResponseError("Tin Certificate must be attached"));
		}
		if (treadName.isEmpty()) {
			return ResponseEntity.badRequest().body(new ResponseError("Tread Certificate must be attached"));
		}
		masterAgentInfo.setOrganizationName(request.getOrganizationName());
		masterAgentInfo.setOrganizationType(request.getOrganizationType());
		masterAgentInfo.setBusinessSector(request.getBusinessSector());
		masterAgentInfo.setBusinessType(request.getBusinessType());
		masterAgentInfo.setTinNumber(request.getTinNumber());
		masterAgentInfo.setCapital(request.getCapital());
		masterAgentInfo.setRegion(request.getRegion());
		masterAgentInfo.setCity(request.getCity());
		masterAgentInfo.setSubcity(request.getSubcity());
		masterAgentInfo.setSpecificLocation(request.getSpecificLocation());
		masterAgentInfo.setWoreda(request.getWoreda());
		masterAgentInfo.setHouseNumber(request.getHouseNumber());
		masterAgentInfo.setPhoneNumber(request.getPhoneNumber());
		masterAgentInfo.setTinDocument(tinName);
		masterAgentInfo.setTreadDocument(treadName);
		masterAgentRepository.save(masterAgentInfo);

		String tinuploadDir = tinPath + masterAgentInfo.getPhoneNumber();
		String treaduploadDir = treadPath + masterAgentInfo.getPhoneNumber();

		UserInfoService.saveTIN(tinuploadDir, tinName, tin);
		UserInfoService.saveTREAD(treaduploadDir, treadName, tread);

		return ResponseEntity
				.ok(new MasterSignupResponse("Successfully finished registration", masterAgentInfo.getUsername(),
						masterAgentInfo.getRoles(), masterAgentInfo.getEmail(), masterAgentInfo.getOrganizationType()));
		// return ResponseEntity.badRequest().body(new SignUpResponse("Enter your
		// business licence number "));
	}

	public ResponseEntity<?> signUpUserPin(SignUpPinRequest request) {
		if ((!userInfoService.checkUsername(request.getPhone()))) {

			return ResponseEntity.badRequest()
					.body(new SignUpResponse("Phone Number Should be Integer Only and Minimum 9 Digit"));
		}
		String phone = "+251" + request.getPhone().substring(request.getPhone().length() - 9);
		boolean userexist= accountRepository.findByAccountNumberEquals(phone).isPresent();
		boolean userExist = userRepository.findByUsername(phone).isPresent();
		boolean agentExist = agentRepository.findByUsername(phone).isPresent();
		boolean merchantExist = merchantRepository.findByUsername(phone).isPresent();
		boolean adminExist = adminRepository.findByUsername(phone).isPresent();
		
		if (userexist || agentExist || merchantExist || userExist || adminExist) {
			return ResponseEntity.badRequest().body(new ResponseError("Username already in use"));
		}
		if (userexist) {
			return ResponseEntity.badRequest().body(new ResponseError("Account already in use"));
		}
		String pin ="";
		for (int j = 0; j < 4; j++) {
			
			pin=pin+randomNumber.randomNumberGenerator(0, 9);
		}
		UserInfo userInfo = new UserInfo();
		userInfo.setUsername(phone);
		Optional<LoginIDType> optional = loginIdTypeRepository.findBylogintype("PHONE");
		if (!optional.isPresent()) {
			return ResponseEntity.badRequest().body(new ResponseError("Login ID Type doesnot exist!"));
		}
		Optional<AuthenticationType> optionalauth = authTypeRepository.findByauthenticationtype("PIN");
		if (!optionalauth.isPresent()) {
			return ResponseEntity.badRequest().body(new ResponseError("Authentication Type doesnot exist!"));
		}
		AuthenticationType authenticationType = optionalauth.get();
		LoginIDType loginIDType=optional.get();	
		userRepository.save(userInfo);
		UserInfo userInfo2 = userRepository.findByUsername(phone).get();
		UserCredential userCredential = new UserCredential();
		
		userCredential.setLoginID(phone);
		userCredential.setActive(true);
		userCredential.setLocalDate(LocalDate.now());
		userCredential.setLoginIDType(loginIDType);
		userCredential.setUserInfo(userInfo2);
		userCredentialRepository.save(userCredential);
		UserAuthentication userAuthentication = new UserAuthentication();
		userAuthentication.setAuthenticationType(authenticationType);
		userAuthentication.setAuthenticationValue(pin);
		userAuthentication.setLocalDate(LocalDate.now());
		userAuthentication.setUserInfo(userInfo2);
		userAuthenticationRepository.save(userAuthentication);
		return ResponseEntity
				.ok(new UpdateResponse("Your Verification Code is: " + pin));
	}

	public ResponseEntity<?> signUpUserPinVerification(SignUpPinVerificationRequest request) {
		String phone = "+251" + request.getPhone().substring(request.getPhone().length() - 9);
		Optional<UserInfo> optional = userRepository.findByUsername(phone);
		
		if (!optional.isPresent()) {
			return ResponseEntity.badRequest().body(new ResponseError("No user found!"));
		}
		UserInfo userInfo = optional.get();
		Optional<UserAuthentication> userOptional = userAuthenticationRepository.findByuserInfo(userInfo);
		if (!userOptional.isPresent()) {
			return ResponseEntity.badRequest().body(new ResponseError("User Authentication not set!"));
		}
		
		UserAuthentication userAuthentication = userOptional.get();
		Optional<UserCredential> usercredOptional = userCredentialRepository.findByUserInfo(userInfo);
		if (!usercredOptional.isPresent()) {
			return ResponseEntity.badRequest().body(new ResponseError("User Credential not set!"));
		}
		if (!userAuthentication.getAuthenticationValue().matches(request.getPin())) {
			return ResponseEntity.badRequest().body(new ResponseError("Not a Valid PIN"));
		}
		userAuthentication.setAuthenticationValue(passwordEncoder.encode(userAuthentication.getAuthenticationValue()));
		userAuthenticationRepository.save(userAuthentication);
		return ResponseEntity
				.ok(new UpdateResponse("Verified successfully!"));
	}

	

}
