package com.bazra.usermanagement.controller;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bazra.usermanagement.model.Account;
import com.bazra.usermanagement.model.AccountBalance;
import com.bazra.usermanagement.model.AdminInfo;
import com.bazra.usermanagement.model.AgentInfo;
import com.bazra.usermanagement.model.BazraBalance;
import com.bazra.usermanagement.model.MasterAgentInfo;
import com.bazra.usermanagement.model.MerchantInfo;
import com.bazra.usermanagement.model.MyUserDetails;
import com.bazra.usermanagement.model.ResetPin;
import com.bazra.usermanagement.model.UserAuthentication;
import com.bazra.usermanagement.model.UserCredential;
import com.bazra.usermanagement.model.UserInfo;
import com.bazra.usermanagement.repository.AccountBalanceRepository;
import com.bazra.usermanagement.repository.AccountRepository;
import com.bazra.usermanagement.repository.AdminRepository;
import com.bazra.usermanagement.repository.AgentRepository;
import com.bazra.usermanagement.repository.BazraBalanceRepository;
import com.bazra.usermanagement.repository.MasterAgentRepository;
import com.bazra.usermanagement.repository.MerchantRepository;
import com.bazra.usermanagement.repository.ResetPinRepository;
import com.bazra.usermanagement.repository.UserAuthenticationRepository;
import com.bazra.usermanagement.repository.UserCredentialRepository;
import com.bazra.usermanagement.repository.UserRepository;
import com.bazra.usermanagement.request.ActivateAccountRequest;
import com.bazra.usermanagement.request.AdminSigninRequest;
import com.bazra.usermanagement.request.AgentSignInRequest;
import com.bazra.usermanagement.request.MasterSignInRequest;
import com.bazra.usermanagement.request.ResetPasswordByPIN;
import com.bazra.usermanagement.request.SignInRequest;
import com.bazra.usermanagement.request.ValidatePassRequest;
import com.bazra.usermanagement.response.AdminSigninResponse;
import com.bazra.usermanagement.response.AgentSignInResponse;
import com.bazra.usermanagement.response.ResponseError;
import com.bazra.usermanagement.response.SignInResponse;
import com.bazra.usermanagement.response.SignUpResponse;
import com.bazra.usermanagement.response.SigninErrorResponse;
import com.bazra.usermanagement.response.SuccessMessageResponse;
import com.bazra.usermanagement.response.UpdateResponse;
import com.bazra.usermanagement.service.UserInfoService;
import com.bazra.usermanagement.util.JwtUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Signin Controller
 * 
 * @author Bemnet
 * @version 4/2022
 *
 */
@RestController
@CrossOrigin("*")
@RequestMapping("/Api")
@Api(value = "SignIn User Endpoint", description = "HERE WE TAKE USER'S PHONENUMEBR AND PASSWORD TO LOGGEDIN")
@ApiResponses(value = {

		@ApiResponse(code = 404, message = "web user that a requested page is not available "),
		@ApiResponse(code = 200, message = "The request was received and understood and is being processed "),
		@ApiResponse(code = 201, message = "The request has been fulfilled and resulted in a new resource being created "),
		@ApiResponse(code = 401, message = "The client request has not been completed because it lacks valid authentication credentials for the requested resource. "),
		@ApiResponse(code = 403, message = "Forbidden response status code indicates that the server understands the request but refuses to authorize it. ")

})
public class SignInUser {
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserInfoService userInfoService;

	private UserInfo userInfo;
	private ResetPin resetPin;
	private UserDetails userDetails;
	private AgentInfo agentInfo;
	private AdminInfo adminInfo;
	private MerchantInfo merchantInfo;
	private MasterAgentInfo masterAgentInfo;
	private UserAuthentication userAuthentication;
	private UserCredential userCredential;
	
	Account account;
	AccountBalance accountBalance;
	BazraBalance bazraBalance;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private MerchantRepository merchantRepository;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private AgentRepository agentRepository;
	@Autowired
	private AdminRepository adminRepository;
	@Autowired
	private MasterAgentRepository masterAgentRepository;
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	ResetPinRepository resetPinRepository;
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	AccountBalanceRepository accountBalanceRepository;
	@Autowired
	BazraBalanceRepository bazraBalanceRepository;
	@Autowired
	UserAuthenticationRepository userAuthenticationRepository;
	@Autowired
	UserCredentialRepository userCredentialRepository;

	/**
	 * Generate authentication token
	 * 
	 * @param authenticationRequest
	 * @return user info plus jwt
	 * @throws AuthenticationException
	 */

	@PostMapping("/SignIn/User")
	@ApiOperation(value ="SIGNIN USER WITH PHONENUMBER AND PASSWORD VALUE")
	public ResponseEntity<?> signinUser(@RequestBody SignInRequest authenticationRequest)
			throws AuthenticationException {
		String username = "+251" + authenticationRequest.getUsername().substring(authenticationRequest.getUsername().length() - 9);
		boolean userExists = userRepository.findByUsername(username).isPresent();
		if (userExists) {

			userDetails = userInfoService.loadUserByUsername(username);
			userInfo = userRepository.findByUsername(username).get();
			
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
					username, authenticationRequest.getPassword(),userDetails.getAuthorities());
			Authentication authentication2 = authenticationManager.authenticate(authentication);
			final String jwt = jwtUtil.generateTokenUser(userDetails);

			System.out.println();
			SecurityContextHolder.getContext().setAuthentication(authentication2);
			System.out.println(SecurityContextHolder.getContext().getAuthentication().getAuthorities());

			try {
				account = accountRepository.findByUser(userInfo).get();
				
			} catch (Exception e) {
				return ResponseEntity.badRequest().body(new ResponseError("Account not found"));
			}
			try {
				accountBalance = accountBalanceRepository.findByaccount(account).get();
			} catch (Exception e) {
				return ResponseEntity.badRequest().body(new ResponseError("Account Balace not found"));
			}
//            Account account = accountRepository.findByAccountNumberEquals(userInfo.getUsername()).get();
			return ResponseEntity.ok(new SignInResponse(userInfo.getId(), userInfo.getUsername(),
					userInfo.getName(), userInfo.getRoles(), accountBalance.getBalance()
					, jwt));

		}


		return ResponseEntity.badRequest().body(new SigninErrorResponse("Error: Invalid username or password"));

	}
	
	@PostMapping("/SignReset/User")
	@ApiOperation(value ="SIGNIN FOR RESET")
	public ResponseEntity<?> signReset(@RequestBody ResetPasswordByPIN resetPasswordByPIN)
			throws AuthenticationException {
		
		int id =userRepository.findByUsername(resetPasswordByPIN.getUsername()).get().getId();
		boolean userExists = resetPinRepository.findByuserInfo(userInfo).isPresent();
		resetPin = resetPinRepository.findByuserInfo(userInfo).get();
		UserInfo userInfo =userRepository.findById(id).get();
//		boolean userExists = userRepository.findByUsername(authenticationRequest.getUsername()).isPresent();
		if (userExists && resetPin.getExpirationTime().isAfter(LocalDateTime.now()) && resetPin.isUsed()==false) {
			userInfo = userRepository.findByUsername(resetPasswordByPIN.getUsername()).get();
			Optional<UserAuthentication> userauthOptional =userAuthenticationRepository.findByuserInfo(userInfo);
			if (!userauthOptional.isPresent()) {
				return ResponseEntity.badRequest().body(new ResponseError("No user Authentication set"));
			}
			UserAuthentication userAuthentication= userauthOptional.get();
			
			
			if (!resetPasswordByPIN.getPin().matches(resetPin.getPin())) {
				return ResponseEntity.badRequest().body(new ResponseError("Invalid PIN"));
		
			}
			else if (resetPin.getPin().matches(resetPasswordByPIN.getPin())&&!resetPin.isUsed()) {
				if(resetPasswordByPIN.getNewpassword().matches(resetPin.getPin())) {
		        	
		            return ResponseEntity.badRequest().body(new UpdateResponse("Password cannot be same as pin"));
		        }
				if (resetPasswordByPIN.getNewpassword().matches(resetPasswordByPIN.getConfirmPassword())) {
					
					userAuthentication.setAuthenticationValue(passwordEncoder.encode(resetPasswordByPIN.getNewpassword()));
					resetPin.setUsed(true);
					userAuthenticationRepository.save(userAuthentication);
					resetPinRepository.save(resetPin);
					return ResponseEntity.ok(new SuccessMessageResponse("Successfully updated password!!"));
				}
				else {
					return ResponseEntity.badRequest().body(new ResponseError("Passwords don't match"));
				}
			}

		}
		return ResponseEntity.badRequest().body(new SigninErrorResponse("Invalid Request!"));

	}

	@PostMapping("/SignIn/Admin")
	@ApiOperation(value ="SIGNIN ADMIN WITH PHONENUMBER AND PASSWORD VALUE")
	public ResponseEntity<?> signinAdmin(@RequestBody AdminSigninRequest adminSigninRequest)
			throws AuthenticationException {
		String username = "+251" + adminSigninRequest.getUsername().substring(adminSigninRequest.getUsername().length() - 9);
		boolean userExists = adminRepository.findByUsername(username).isPresent();
		if (userExists) {
			
			userDetails = userInfoService.loadUserByUsername(username);
			adminInfo = adminRepository.findByUsername(username).get();
			UsernamePasswordAuthenticationToken
	        authentication = new UsernamePasswordAuthenticationToken(
	        		username,
	        		adminSigninRequest.getPassword(), adminInfo.getAuthorities());
			Authentication authentication2 =authenticationManager.authenticate(authentication);
			final String jwt = jwtUtil.generateTokenUser(adminInfo);
			
			adminRepository.findByUsername(username).get();
			SecurityContextHolder.getContext()
					.setAuthentication(authentication2);
			System.out.println(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
			
			try {
				account = accountRepository.findByAdmin(adminInfo).get();
			} catch (Exception e) {
				return ResponseEntity.badRequest().body(new ResponseError("Account not found"));
			}
			try {
				bazraBalance = bazraBalanceRepository.findByaccount(account).get();
			} catch (Exception e) {
				return ResponseEntity.badRequest().body(new ResponseError("Account Balace not found"));
			}
//            Account account = accountRepository.findByAccountNumberEquals(userInfo.getUsername()).get();
			return ResponseEntity.ok(new AdminSigninResponse(adminInfo.getId(), adminInfo.getUsername(),
					 adminInfo.getRoles(), bazraBalance.getBalance(), jwt));

		}
		return ResponseEntity.badRequest().body(new SigninErrorResponse("Error: Invalid username or password"));

		}

	@PostMapping("/SignIn/Agent")
	@ApiOperation(value ="SIGNIN AGENT WITH PHONENUMBER AND PASSWORD VALUE")
	public ResponseEntity<?> signinAgent(@RequestBody AgentSignInRequest agentSignInRequest)
			throws AuthenticationException {
		String username = "+251" + agentSignInRequest.getUsername().substring(agentSignInRequest.getUsername().length() - 9);
		boolean userExists = agentRepository.findByUsername(username).isPresent();
		if (userExists) {
			agentInfo = agentRepository.findByUsername(username).get();
			userDetails = userInfoService.loadUserByUsername(username);
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
					username, agentSignInRequest.getPassword(), agentInfo.getAuthorities());
			Authentication authentication2 = authenticationManager.authenticate(authentication);

			SecurityContextHolder.getContext().setAuthentication(authentication2);
			
			userDetails = userInfoService.loadUserByUsername(username);
			agentInfo = agentRepository.findByUsername(username).get();
			final String jwt = jwtUtil.generateTokenAgent(agentInfo);

			try {
				account = accountRepository.findByAgent(agentInfo).get();
				
			} catch (Exception e) {
				return ResponseEntity.badRequest().body(new ResponseError("Account not found"));
			}
			try {
				accountBalance = accountBalanceRepository.findByaccount(account).get();
			} catch (Exception e) {
				return ResponseEntity.badRequest().body(new ResponseError("Account Balace not found"));
			}
			return ResponseEntity.ok(new AgentSignInResponse(agentInfo.getId(), agentInfo.getUsername(),
					agentInfo.getRoles(), accountBalance.getBalance(), jwt));

		}

		return ResponseEntity.badRequest().body(new SigninErrorResponse("Error: Invalid username or password"));
	}

	@PostMapping("/SignIn/Merchant")
	@ApiOperation(value ="SIGNIN MERCHANT WITH PHONENUMBER AND PASSWORD VALUE")
	public ResponseEntity<?> signinMerchant(@RequestBody AgentSignInRequest agentSignInRequest)
			throws AuthenticationException {
		String username = "+251" + agentSignInRequest.getUsername().substring(agentSignInRequest.getUsername().length() - 9);
		boolean userExists = merchantRepository.findByUsername(username).isPresent();
		if (userExists) {
			merchantInfo = merchantRepository.findByUsername(username).get();
			userDetails = userInfoService.loadUserByUsername(username);
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
					username, agentSignInRequest.getPassword(), merchantInfo.getAuthorities());
			Authentication authentication2 = authenticationManager.authenticate(authentication);

			SecurityContextHolder.getContext().setAuthentication(authentication2);

			userDetails = userInfoService.loadUserByUsername(username);
			merchantInfo = merchantRepository.findByUsername(username).get();
			final String jwt = jwtUtil.generateTokenAgent(merchantInfo);

			try {
				account = accountRepository.findByMerchant(merchantInfo).get();

			} catch (Exception e) {
				return ResponseEntity.badRequest().body(new ResponseError("Account not found"));
			}
			try {
				accountBalance = accountBalanceRepository.findByaccount(account).get();
			} catch (Exception e) {
				return ResponseEntity.badRequest().body(new ResponseError("Account Balace not found"));
			}
			return ResponseEntity.ok(new AgentSignInResponse(merchantInfo.getId(), merchantInfo.getUsername(),
					merchantInfo.getRoles(), accountBalance.getBalance(), jwt));

		}

		return ResponseEntity.badRequest().body(new SigninErrorResponse("Error: Invalid username or password"));
	}

	@PostMapping("/SignIn/Master")

	@ApiOperation(value ="SIGNIN MASTERAGENT WITH PHONENUMBER AND PASSWORD VALUE")
	public ResponseEntity<?> signinMaster(@RequestBody MasterSignInRequest masterSignInRequest)
			throws AuthenticationException {
		boolean userExists = masterAgentRepository.findByuserName(masterSignInRequest.getUsername()).isPresent();
		if (userExists) {
			masterAgentInfo = masterAgentRepository.findByuserName(masterSignInRequest.getUsername()).get();
			System.out.println(masterAgentInfo.getUsername());
			userDetails = userInfoService.loadUserByUsername(masterSignInRequest.getUsername());
//			Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
//					agentSignInRequest.getUsername(), agentSignInRequest.getPassword()));

			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
					masterSignInRequest.getUsername(), masterSignInRequest.getPassword(),
					masterAgentInfo.getAuthorities());
			Authentication authentication2 = authenticationManager.authenticate(authentication);
//	        authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication2);

//        userDetails = userInfoService.loadUserByUsername(agentSignInRequest.getUsername());

			final String jwt = jwtUtil.generateTokenMerchant(userDetails);

			try {
				account = accountRepository.findByAccountNumberEquals(masterAgentInfo.getPhoneNumber()).get();
			} catch (Exception e) {
				return ResponseEntity.badRequest().body(new ResponseError("Account not found"));
			}
			try {
				accountBalance = accountBalanceRepository.findByaccount(account).get();
			} catch (Exception e) {
				return ResponseEntity.badRequest().body(new ResponseError("Account Balace not found"));
			}
//        Account account = accountRepository.findByAccountNumberEquals(merchantInfo.getUsername()).get();
			return ResponseEntity.ok(new AgentSignInResponse(masterAgentInfo.getId(), masterAgentInfo.getPhoneNumber(),
					masterAgentInfo.getRoles(), accountBalance.getBalance(), jwt));

		}

		return ResponseEntity.badRequest().body(new SigninErrorResponse("Error: Invalid username or password"));
	}
	
	
	@PostMapping("/ValidatePass")
	@ApiOperation(value ="PASSWORD VALIDATION FOR USER")
	public ResponseEntity<?> validatePassWord(@RequestBody ValidatePassRequest validate,Authentication authenticationn)
			{
		String username = "+251" + authenticationn.getName().substring(authenticationn.getName().length() - 9);
		boolean userExists = userRepository.findByUsername(username).isPresent();
		if (userExists) {

//			UserDetails userDetails = userInfoService.loadUserByUsername(username);
//			UserInfo userInfo = userRepository.findByUsername(authenticationn.getName()).get();
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username,
					validate);

			Authentication authentication2 = authenticationManager.authenticate(authentication);
			System.out.println("auth2   " + authentication2.isAuthenticated());
			if (authentication2.isAuthenticated()) {
				SecurityContextHolder.getContext().setAuthentication(authentication2);
				System.out.println(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
				return ResponseEntity.ok(new UpdateResponse("Correct Password!"));
			}

			return ResponseEntity.badRequest().body(new SigninErrorResponse("Error: Invalid password"));
		}

		return ResponseEntity.badRequest().body(new SigninErrorResponse("User Does not exist!"));
	
	}
//	@PostMapping("/SignIn/UserPin")
//	@ApiOperation(value ="SIGNIN USER WITH PHONENUMBER AND PASSWORD VALUE")
//	public ResponseEntity<?> signinUserPin(@RequestBody SignInRequest signInRequest){
//		String username = "+251" + signInRequest.getUsername().substring(signInRequest.getUsername().length() - 9);
//		boolean userExists = signUpPinRepository.findByphone(username).isPresent();
//		if (userExists) {
//
//			userDetails = userInfoService.loadUserByUsername(username);
//			userInfo = userRepository.findByUsername(username).get();
//			
//			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//					username, signInRequest.getPassword(),
//					userInfo.getAuthorities());
//			Authentication authentication2 = authenticationManager.authenticate(authentication);
//			final String jwt = jwtUtil.generateTokenUser(userInfo);
//
//
//			SecurityContextHolder.getContext().setAuthentication(authentication2);
//			System.out.println(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
//
//			try {
//				account = accountRepository.findByUser(userInfo).get();
//				
//			} catch (Exception e) {
//				return ResponseEntity.badRequest().body(new ResponseError("Account not found"));
//			}
//			try {
//				accountBalance = accountBalanceRepository.findByaccount(account).get();
//			} catch (Exception e) {
//				return ResponseEntity.badRequest().body(new ResponseError("Account Balace not found"));
//			}
////            Account account = accountRepository.findByAccountNumberEquals(userInfo.getUsername()).get();
//			return ResponseEntity.ok(new SignInResponse(userInfo.getId(), userInfo.getUsername(),
//					userInfo.getName(), userInfo.getRoles(), accountBalance.getBalance(),
//					userInfo.getGender(), jwt));
//
//		}
//		return ResponseEntity.badRequest().body(new SigninErrorResponse("Error: Invalid username or password"));
//	}
	@PostMapping("/ActivateAccount")
    @ApiOperation(value ="USER ACTIVATE ACCOUNT ")
    public ResponseEntity<?> userActivateAccount(@RequestBody ActivateAccountRequest request,Authentication authentication) {
		String username = "+251" + authentication.getName().substring(authentication.getName().length() - 9);
		System.out.println("username"+username);
		Optional<UserInfo> userinfOptional = userRepository.findByUsername(username);
		if (!userinfOptional.isPresent()) {
			return ResponseEntity.badRequest().body(new SigninErrorResponse("Error: Invalid user"));
		}
		UserInfo userInfo = userinfOptional.get();
		Optional<UserCredential> usercredOptional = userCredentialRepository.findByUserInfo(userInfo);
		if (!usercredOptional.isPresent()) {
			return ResponseEntity.badRequest().body(new SigninErrorResponse("Error: Invalid username"));
		}
		UserCredential userCredential = usercredOptional.get();
		Optional<UserAuthentication> userauthOptional =userAuthenticationRepository.findByuserInfo(userInfo);
		if (!userauthOptional.isPresent()) {
			return ResponseEntity.badRequest().body(new SigninErrorResponse("Error: Invalid username for auth"));
		}
		userDetails = userInfoService.loadUserByUsername(username);
		UserAuthentication userAuthentication = userauthOptional.get();
		UsernamePasswordAuthenticationToken authenticationn = new UsernamePasswordAuthenticationToken(username,
				request.getDefaultPin(),userDetails.getAuthorities());
//		System.out.println(authenticationn);
//		System.out.println("is authenticated"+authenticationn.isAuthenticated());
//		Authentication authentication2 = authenticationManager.authenticate(authenticationn);
	
		if (authenticationn.isAuthenticated()) {
			SecurityContextHolder.getContext().setAuthentication(authenticationn);
			if (!request.getNewpin().matches(request.getConfirmpin())) {
				return ResponseEntity.badRequest().body(new SignUpResponse("Error: New PINs don't match!"));
			}
			userAuthentication.setAuthenticationValue(passwordEncoder.encode(request.getNewpin()));
			userAuthenticationRepository.save(userAuthentication);
			return ResponseEntity.ok(new UpdateResponse("Account Activated successfully!")); 
		
		}
		
			

//		if (!userAuthentication.getAuthenticationValue().matches(request.getDefaultPin())) {

		
		
		
		return ResponseEntity.badRequest().body(new ResponseError("Invalid default PIN"));
    }
	
}
