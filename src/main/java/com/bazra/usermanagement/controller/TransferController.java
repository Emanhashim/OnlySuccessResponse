package com.bazra.usermanagement.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bazra.usermanagement.controller.ResetPinController.RemindTask;
import com.bazra.usermanagement.model.Account;
import com.bazra.usermanagement.model.AccountBalance;
import com.bazra.usermanagement.model.Bank;
import com.bazra.usermanagement.model.BankTransfer;
import com.bazra.usermanagement.model.LocalTransfer;
import com.bazra.usermanagement.model.ResetPin;
import com.bazra.usermanagement.model.UserInfo;
import com.bazra.usermanagement.repository.AccountBalanceRepository;
import com.bazra.usermanagement.repository.AccountRepository;
import com.bazra.usermanagement.repository.BankRepository;
import com.bazra.usermanagement.repository.BankTransferRepository;
import com.bazra.usermanagement.repository.LocalTransferRepository;
import com.bazra.usermanagement.request.BankTransferRequest;
import com.bazra.usermanagement.request.FinalizeLocalTransferRequest;
import com.bazra.usermanagement.request.LocalTransferRequest;
import com.bazra.usermanagement.response.ResponseError;
import com.bazra.usermanagement.response.UpdateResponse;
import com.bazra.usermanagement.service.RandomNumber;

@RestController
@CrossOrigin("*")
@RequestMapping("/Api")
public class TransferController {
	@Autowired
	AccountRepository accountRepository;
	@Autowired
	BankTransferRepository bankTransferRepository;
	@Autowired
	BankRepository bankRepository;
	@Autowired
	LocalTransferRepository localTransferRepository;
	@Autowired
	AccountBalanceRepository accountBalanceRepository;
	
	RandomNumber randomNumber;
	Timer timer;
	AccountBalance fromAccountBalance;
	@PostMapping("/BankTransfer")
	public ResponseEntity<?> createBankTransfer(@RequestBody BankTransferRequest bankTransferRequest, Authentication authentication){
		Account fromAccount = accountRepository.findByAccountNumberEquals(authentication.getName()).get();
		Bank bank =bankRepository.findByName(bankTransferRequest.getName()).get();
		if(bankTransferRequest.getName()==null) {
			return ResponseEntity.badRequest().body(new ResponseError("Bank name cannot be empty!"));
		}
		if(bankTransferRequest.getAccountNumber().isEmpty()) {
			return ResponseEntity.badRequest().body(new ResponseError("You haven't inserted an account number"));
		}
		if(bankTransferRequest.getAmount()==null) {
			return ResponseEntity.badRequest().body(new ResponseError("Amount cannot be empty!"));
		}
		if (!bankRepository.findByName(bankTransferRequest.getName()).isPresent()) {
			return ResponseEntity.badRequest().body(new ResponseError("Not a registered bank"));
		}
		try {
			fromAccountBalance = accountBalanceRepository.findByaccount(fromAccount).get();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ResponseError("Account Balace not found"));
		}
		if (fromAccountBalance.getBalance().compareTo(new BigDecimal(bankTransferRequest.getAmount())) == 1) {
			fromAccountBalance.setBalance(fromAccountBalance.getBalance().subtract(new BigDecimal(bankTransferRequest.getAmount())));
			accountRepository.save(fromAccount);
			BankTransfer bankTransfer = new BankTransfer();
			bankTransfer.setAccount(fromAccount);
			bankTransfer.setTobankaccount(bankTransferRequest.getAccountNumber());
			bankTransfer.setAmount(bankTransferRequest.getAmount());
			bankTransfer.setTransferDate(LocalDate.now());
			bankTransfer.setBank(bank);
			bankTransfer.setRemark(bankTransferRequest.getRemark());
			bankTransferRepository.save(bankTransfer);
			return ResponseEntity.ok(new UpdateResponse("Transfer successful"));
		}
		return ResponseEntity.badRequest().body(new ResponseError("You don't have enough credit for this request!"));
		
	}
	@PostMapping("/LocalTransfer")
	public ResponseEntity<?> createLocalTransfer(@RequestBody LocalTransferRequest localTransferRequest, Authentication authentication){
		Account fromAccount = accountRepository.findByAccountNumberEquals(authentication.getName()).get();
		if(localTransferRequest.getReceiverName()==null) {
			return ResponseEntity.badRequest().body(new ResponseError("Receiver name cannot be empty!"));
		}
		if(localTransferRequest.getReceiverPhone()==null) {
			return ResponseEntity.badRequest().body(new ResponseError("Receiver phone cannot be empty!"));
		}
		if(localTransferRequest.getAmount()==null) {
			return ResponseEntity.badRequest().body(new ResponseError("Amount cannot be empty!"));
		}
		try {
			fromAccountBalance = accountBalanceRepository.findByaccount(fromAccount).get();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ResponseError("Account Balace not found"));
		}
		System.out.println(new BigDecimal(localTransferRequest.getAmount()));
		System.out.println(fromAccountBalance.getBalance());
		if (fromAccountBalance.getBalance().compareTo(new BigDecimal(localTransferRequest.getAmount())) == 1) {
			
			String pin ="";
			for (int j = 0; j < 6; j++) {
				
				pin=pin+randomNumber.randomNumberGenerator(0, 9);
			}
			System.out.println(pin);
			LocalTransfer localTransfer = new LocalTransfer();
			localTransfer.setAmount(localTransferRequest.getAmount());
			localTransfer.setExiredDate(LocalDateTime.now().plusDays(2));
			localTransfer.setFromAccount(fromAccount);
			localTransfer.setReceiverName(localTransferRequest.getReceiverName());
			localTransfer.setReceiverPhone(localTransferRequest.getReceiverPhone());
			localTransfer.setTransactionCode(pin);
			localTransfer.setTransterDate(LocalDate.now());
			localTransfer.setOntime(false);
			localTransferRepository.save(localTransfer);
			LocalTransfer localTransfer2 = localTransferRepository.findByTransactionCode(pin).get();
			timer = new Timer();
	        timer.schedule(new RemindTask(localTransfer2), 172800*1000);
//			LocalTransfer localTransfer = new LocalTransfer(localTransferRequest.getReceiverName(),localTransferRequest.getReceiverPhone(),localTransferRequest.getAmount(),pin,fromAccount.getAccountNumber());
			
			return ResponseEntity.ok(new UpdateResponse("Transfer is being processed!"));
		}
		return ResponseEntity.badRequest().body(new ResponseError("You don't have enough credit for this request!"));
		
	}
//	@PostMapping("/FinalizeLocalTransfer")
//	public ResponseEntity<?> finalizeLocalTransfer(@RequestBody FinalizeLocalTransferRequest finalizeLocalTransferRequest, Authentication authentication){
//		Account agentaccount = accountRepository.findByAccountNumberEquals(authentication.getName()).get();
//		if (!agentaccount.getType().matches("AGENT")) {
//			return ResponseEntity.badRequest().body(new ResponseError("Unauthorized request"));
//		}
//		Optional<LocalTransfer> localOptional = localTransferRepository.findByPin(finalizeLocalTransferRequest.getPin());
//		if(!localOptional.isPresent()) {
//			return ResponseEntity.badRequest().body(new ResponseError("Not a valid PIN"));
//		}
//		LocalTransfer localTransfer = localOptional.get();
//		
//	}
	class RemindTask extends TimerTask {
		private UserInfo userInfo2;
		private boolean ontime;
		private boolean used;
		private LocalTransfer resetPin2;
		private LocalDateTime expirationDateTime;
		
		private  RemindTask(LocalTransfer pin) {
			this.ontime = pin.getOntime();
			this.expirationDateTime=pin.getExiredDate();
//			this.userInfo2 = userInfo;
			
			this.resetPin2 = localTransferRepository.findByTransactionCode(pin.getTransactionCode()).get();
		}
	    public void run() {
	    	if (!used && expirationDateTime.isBefore(LocalDateTime.now())) {
//				userInfo2.setPassword("null");
//				userRepository.save(userInfo2);
//				resetPin2.setIsUsed(true);
//				localTransferRepository.delete(resetPin2);
	    		resetPin2.setOntime(false);
				localTransferRepository.save(resetPin2);
				System.out.println("Not used");
				timer.cancel();
			}
	    
	    	System.out.println("Not used used");
	    	timer.cancel();
	    }
	}	
	
}
