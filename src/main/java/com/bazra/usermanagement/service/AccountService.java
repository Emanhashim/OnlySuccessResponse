package com.bazra.usermanagement.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.bazra.usermanagement.model.Account;
import com.bazra.usermanagement.model.AccountBalance;
import com.bazra.usermanagement.model.AccountType;
import com.bazra.usermanagement.model.BazraBalance;
import com.bazra.usermanagement.model.CreditTransaction;
import com.bazra.usermanagement.model.DebitTransaction;
import com.bazra.usermanagement.model.Levels;
import com.bazra.usermanagement.model.Transaction;
import com.bazra.usermanagement.model.Transactiontype;
import com.bazra.usermanagement.repository.AccountBalanceRepository;
import com.bazra.usermanagement.repository.AccountRepository;
import com.bazra.usermanagement.repository.AccountTypeRepository;
import com.bazra.usermanagement.repository.BazraBalanceRepository;
import com.bazra.usermanagement.repository.CreditTransactionRepository;
import com.bazra.usermanagement.repository.DebitTransactionRepository;
import com.bazra.usermanagement.repository.SettingRepository;
import com.bazra.usermanagement.repository.TransactionRepository;
import com.bazra.usermanagement.repository.TransactionTypeRepository;
import com.bazra.usermanagement.repository.UserRepository;
import com.bazra.usermanagement.request.AdminTransferRequest;
import com.bazra.usermanagement.request.DepositRequest;
import com.bazra.usermanagement.request.TransferRequest;
import com.bazra.usermanagement.request.WithdrawRequest;
import com.bazra.usermanagement.response.DepositResponse;
import com.bazra.usermanagement.response.ResponseError;
import com.bazra.usermanagement.response.TransferResponse;
import com.bazra.usermanagement.response.UpdateResponse;
import com.bazra.usermanagement.response.WithdrawResponse;

@Service
public class AccountService {
	@Autowired
	AccountRepository accountRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	TransactionRepository transactionRepository;

	@Autowired
	SettingRepository settingRepository;
	
	@Autowired
	AccountTypeRepository accountTypeRepository;
	
	@Autowired 
	TransactionTypeRepository transactionTypeRepository;
	@Autowired
	AccountBalanceRepository accountBalanceRepository;
	AccountBalance fromaccountBalance;
	AccountBalance toaccountBalance;
	BazraBalance bazraBalance;
	AccountBalance agAccountBalance;
	@Autowired
	BazraBalanceRepository bazraBalanceRepository;
	@Autowired
	CreditTransactionRepository creditTransactionRepository;
	@Autowired
	DebitTransactionRepository debitTransactionRepository;
	@Autowired
	RandomNumber randomNumber;
//	@Value("${withdrawfee}")
//	private BigDecimal withdrawfee;


	BigDecimal transactionfee;
	BigDecimal commissionfee;
	BigDecimal dailyTransferLimit;
	BigDecimal agentDepositLimit;
	BigDecimal maxBalanceL1;
	BigDecimal maxBalanceL2;
	BigDecimal maxBalanceL3;
//	BigDecimal daily = new BigDecimal(0);
	LocalDate daterepo;

	public Account save(Account account) {
		accountRepository.save(account);
		return accountRepository.findByAccountNumberEquals(account.getAccountNumber()).get();
	}

	public Account getAccount(String account) {
		Account accounts = accountRepository.findByAccountNumberEquals(account).get();
		return accounts;
	}

	public List<Account> findAll() {
		return accountRepository.findAll();
	}

//	public List<Transaction> findall(String accountnumber) {
//		Account account =accountRepository.findByAccountNumberEquals(accountnumber).get();
//		List<CreditTransaction> transactions = creditTransactionRepository.findByaccount(account);
//		List<DebitTransaction> debitTransaction = debitTransactionRepository.findByaccount(account);
//		
//		return transactions;
//	}

	public Account findByAccountNumber(String accountnumber) {
		Account account = accountRepository.findByAccountNumberEquals(accountnumber).get();
		return account;
	}

	public ResponseEntity<?> sendMoney(TransferRequest transferBalanceRequest, String name) {
		Optional<Account> optionfrom = accountRepository.findByAccountNumberEquals(name);
		if (!optionfrom.isPresent()) {
			return ResponseEntity.badRequest().body(new ResponseError("Invalid Account"));
		}
//		String toAccountNumber = transferBalanceRequest.getToAccountNumber();
		Account fromAccount = optionfrom.get();
		String toAccountNumber = "+251" + transferBalanceRequest.getToAccountNumber().substring(transferBalanceRequest.getToAccountNumber().length() - 9);

		Optional<Account> optionto =accountRepository.findByAccountNumberEquals(toAccountNumber);
		if (!optionto.isPresent()) {
			return ResponseEntity.badRequest().body(new ResponseError("Invalid Receipent Account"));
		}
		AccountType accountType = accountTypeRepository.findByaccounttype("ADMIN").get();
		Account bazraAccount = accountRepository.findByType(accountType).get();
		Transactiontype transactiontype = transactionTypeRepository.findBytransactionType("TRANSFER").get();
		if (fromAccount.isStatus()) {
			return ResponseEntity.badRequest().body(new ResponseError("Account Blocked"));
		}
		BigDecimal daily =new BigDecimal(0);
	
		LocalDate today = LocalDate.now(ZoneId.systemDefault());
		
		Levels levels = userRepository.findById(fromAccount.getUser().getId()).get().getLevels();
		
		if(levels==Levels.LEVEL_1) {
			transactionfee = settingRepository.findBysettingName("Transaction Fee(LEVEL_1)").get().getValue();
			dailyTransferLimit = settingRepository.findBysettingName("Daily Transfer Limit").get().getValue();
		}
		else if (levels==Levels.LEVEL_2) {
			transactionfee = settingRepository.findBysettingName("Transaction Fee(LEVEL_2)").get().getValue();
			dailyTransferLimit = settingRepository.findBysettingName("Daily Transfer Limit(LEVEL_2)").get().getValue();
		}
		else if (levels==Levels.LEVEL_3) {
			transactionfee = settingRepository.findBysettingName("Transaction Fee(LEVEL_3)").get().getValue();
			dailyTransferLimit = settingRepository.findBysettingName("Daily Transfer Limit(LEVEL_3)").get().getValue();
		}
		
		BigDecimal amount = transferBalanceRequest.getAmount();
		Account toAccount;
		try {
			toAccount = accountRepository.findByAccountNumberEquals(toAccountNumber).get();
			if(toAccount.equals(fromAccount)) {
				return ResponseEntity.badRequest().body(new ResponseError("Invalid receipient"));
			}
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ResponseError("Invalid receipient"));
		}

		if (toAccount.isStatus()) {
			return ResponseEntity.badRequest().body(new ResponseError("Account Blocked"));
		}

		if (toAccount == null) {
			return ResponseEntity.badRequest().body(new ResponseError("Invalid receipient"));
		}
		if (transferBalanceRequest.getAmount() == null) {
			return ResponseEntity.badRequest().body(new ResponseError("Enter Valid Amount"));
		}

		if (amount.compareTo(new BigDecimal(5)) == -1) {
			return ResponseEntity.badRequest().body(new ResponseError("Minimum amount to transfer is 5"));
		}
		if (transferBalanceRequest.getMessage() == null) {
			return ResponseEntity.badRequest().body(new ResponseError("Enter your remark"));
		}
		Levels toAccountLevel = userRepository.findByUsername(toAccount.getUser().getUsername()).get().getLevels();
		if(toAccountLevel.equals(Levels.LEVEL_1)) {
			maxBalanceL1 = settingRepository.findBysettingName("Balance Limit (LEVEL_1)").get().getValue();
		}
		else if (toAccountLevel.equals(Levels.LEVEL_2)) {
			maxBalanceL1 = settingRepository.findBysettingName("Balance Limit (LEVEL_2)").get().getValue();
		}
		else if (toAccountLevel.equals(Levels.LEVEL_3)) {
			maxBalanceL1 = settingRepository.findBysettingName("Balance Limit (LEVEL_3)").get().getValue();
		}
		List<DebitTransaction> debitcurr =new ArrayList<DebitTransaction>();
		List<DebitTransaction> debitTransactions=debitTransactionRepository.findBycreateDate(today);
		
		for (int i = 0; i < debitTransactions.size(); i++) {
			if (debitTransactions.get(i).getAccount().getAccountNumber().equals(name)) {
				debitcurr.add(debitTransactions.get(i));

			}

		}
		BigDecimal minbalance = amount.add(transactionfee);
		try {
			fromaccountBalance = accountBalanceRepository.findByaccount(fromAccount).get();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ResponseError("Account Balance Does not Exist"));
		}
		
		try {
			toaccountBalance = accountBalanceRepository.findByaccount(toAccount).get();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ResponseError("Account Balance Does not Exist"));
		}
		try {
			bazraBalance = bazraBalanceRepository.findByaccount(bazraAccount).get();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ResponseError("Account Balance Does not Exist"));
		}
		String transactioncode ="";
		for (int j = 0; j < 6; j++) {
			
			transactioncode=transactioncode+randomNumber.randomNumberGenerator(0, 9);
		}
		if(debitcurr.isEmpty()) {
			if (fromaccountBalance.getBalance().compareTo(BigDecimal.ONE) == 1
					&& fromaccountBalance.getBalance().compareTo(minbalance) == 1) {
				

				daily = daily.add(transferBalanceRequest.getAmount());

				
				if (maxBalanceL1.compareTo(toaccountBalance.getBalance().add(transferBalanceRequest.getAmount()))==-1) {
					return ResponseEntity.badRequest().body(new ResponseError("Receiver not able to accept"));
				}
				if (dailyTransferLimit.compareTo(daily.negate()) == 1) {
					fromaccountBalance.setBalance(fromaccountBalance.getBalance().subtract(minbalance));
					fromAccount.setDaily(daily.negate().add(transferBalanceRequest.getAmount()));
					accountRepository.save(fromAccount);
					
					bazraBalance.setBalance(bazraBalance.getBalance().add(transactionfee.multiply(amount)));
					toaccountBalance.setBalance(toaccountBalance.getBalance().add(amount));
					accountRepository.save(toAccount);
					accountRepository.save(bazraAccount);
					
					
					Transaction transaction = new Transaction();
					transaction.setCreateDate(LocalDate.now());
					transaction.setTransactionAmount(amount);
					transaction.setTransactionCode(transactioncode);
					transaction.setTransactiontype(transactiontype);
					transactionRepository.save(transaction);
					Transaction transaction2 = transactionRepository.findBytransactionCode(transactioncode).get();
					DebitTransaction debitTransaction = new DebitTransaction();
					debitTransaction.setAccount(fromAccount);
					debitTransaction.setCreateDate(LocalDate.now());
					debitTransaction.setTransaction(transaction2);
					debitTransactionRepository.save(debitTransaction);
					
					CreditTransaction creditTransaction = new CreditTransaction();
					creditTransaction.setAccount(toAccount);
					creditTransaction.setCreateDate(LocalDate.now());
					creditTransaction.setTransaction(transaction2);
					creditTransactionRepository.save(creditTransaction);
					return ResponseEntity.ok(new UpdateResponse("Successful Transfer"));
				}
				else {
					
					return ResponseEntity.badRequest().body(new ResponseError("Daily Limit overpassed"));
				}
			}
		}	
		else {
			daterepo= debitTransactions.get(debitTransactions.size()-1).getCreateDate();

			if (fromaccountBalance.getBalance().compareTo(BigDecimal.ONE) == 1
					&& fromaccountBalance.getBalance().compareTo(minbalance) == 1) {
			

				
				for (int i = 0; i < debitcurr.size(); i++) {
					
						daily = daily.add(debitcurr.get(i).getTransaction().getTransactionAmount());
				
				}
				
				if (maxBalanceL1.compareTo(toaccountBalance.getBalance().add(transferBalanceRequest.getAmount()))==-1) {
					return ResponseEntity.badRequest().body(new ResponseError("Receiver not able to accept"));
				}
				if (dailyTransferLimit.compareTo(daily.negate().add(minbalance)) == 1) {
					fromaccountBalance.setBalance(fromaccountBalance.getBalance().subtract(minbalance));
					fromAccount.setDaily(daily.negate().add(transferBalanceRequest.getAmount()));
					accountRepository.save(fromAccount);
					
					bazraBalance.setBalance(bazraBalance.getBalance().add(transactionfee.multiply(amount)));
					toaccountBalance.setBalance(toaccountBalance.getBalance().add(amount));
					accountRepository.save(toAccount);
					accountRepository.save(bazraAccount);
					Transaction transaction = new Transaction();
					transaction.setCreateDate(LocalDate.now());
					transaction.setTransactionAmount(amount);
					transaction.setTransactionCode(transactioncode);
					transaction.setTransactiontype(transactiontype);
					transactionRepository.save(transaction);
					Transaction transaction2 = transactionRepository.findBytransactionCode(transactioncode).get();
					DebitTransaction debitTransaction = new DebitTransaction();
					debitTransaction.setAccount(fromAccount);
					debitTransaction.setCreateDate(LocalDate.now());
					debitTransaction.setTransaction(transaction2);
					debitTransactionRepository.save(debitTransaction);
					
					CreditTransaction creditTransaction = new CreditTransaction();
					creditTransaction.setAccount(toAccount);
					creditTransaction.setCreateDate(LocalDate.now());
					creditTransaction.setTransaction(transaction2);
					creditTransactionRepository.save(creditTransaction);
					
					return ResponseEntity.ok(new UpdateResponse("Successful Transfer"));
				} else {

					return ResponseEntity.badRequest().body(new ResponseError("Daily Limit overpassed"));
				}

			}
		}
		
		return ResponseEntity.badRequest().body(new ResponseError("Insufficient balance"));
	}

	public ResponseEntity<?> adminSendMoney(AdminTransferRequest adminTransferRequest, String adminusername) {
		Optional<Account> optionfrom = accountRepository.findByAccountNumberEquals(adminTransferRequest.getFromAccountNumber());
		Optional<Account> optionAdmin = accountRepository.findByAccountNumberEquals(adminusername);
		String toAccountNumber = adminTransferRequest.getToAccountNumber();
		Optional<Account> optionTo =accountRepository.findByAccountNumberEquals(toAccountNumber);
		Account toAccount;
		BigDecimal amount = adminTransferRequest.getAmount();
		if (!optionAdmin.isPresent()) {
			return ResponseEntity.badRequest().body(new ResponseError("Invalid Admin Account"));
		}
		Account bazraAccount = optionAdmin.get();
		if (!optionfrom.isPresent()) {
			return ResponseEntity.badRequest().body(new ResponseError("Invalid Account"));
		}
		if (!optionTo.isPresent()) {
			return ResponseEntity.badRequest().body(new ResponseError("Invalid Destination Account"));
		}
		Account fromAccount = optionfrom.get();
		try {
			toAccount = accountRepository.findByAccountNumberEquals(toAccountNumber).get();
			if (toAccount.equals(fromAccount)) {
				return ResponseEntity.badRequest().body(new ResponseError("Invalid receipient"));
			}
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ResponseError("Invalid receipient"));
		}
		Transactiontype transactiontype = transactionTypeRepository.findBytransactionType("TRANSFER").get();
		BigDecimal daily = new BigDecimal(0);
		if (fromAccount.isStatus()) {
			return ResponseEntity.badRequest().body(new ResponseError("Account Blocked"));
		}
		LocalDate today = LocalDate.now(ZoneId.systemDefault());

		Levels levels = userRepository.findById(fromAccount.getUser().getId()).get().getLevels();

		if (levels == Levels.LEVEL_1) {
			transactionfee = settingRepository.findBysettingName("Transaction Fee(LEVEL_1)").get().getValue();
			dailyTransferLimit = settingRepository.findBysettingName("Daily Transfer Limit").get().getValue();
		} else if (levels == Levels.LEVEL_2) {
			transactionfee = settingRepository.findBysettingName("Transaction Fee(LEVEL_2)").get().getValue();
			dailyTransferLimit = settingRepository.findBysettingName("Daily Transfer Limit(LEVEL_2)").get().getValue();
		} else if (levels == Levels.LEVEL_3) {
			transactionfee = settingRepository.findBysettingName("Transaction Fee(LEVEL_3)").get().getValue();
			dailyTransferLimit = settingRepository.findBysettingName("Daily Transfer Limit(LEVEL_3)").get().getValue();
		}
		
		

		if (toAccount.isStatus()) {
			return ResponseEntity.badRequest().body(new ResponseError("Account Blocked"));
		}

		if (toAccount == null) {
			return ResponseEntity.badRequest().body(new ResponseError("Invalid receipient"));
		}
		if (adminTransferRequest.getAmount() == null) {
			return ResponseEntity.badRequest().body(new ResponseError("Enter Valid Amount"));
		}

		if (amount.compareTo(new BigDecimal(5)) == -1) {
			return ResponseEntity.badRequest().body(new ResponseError("Minimum amount to transfer is 5"));
		}
		if (adminTransferRequest.getMessage() == null) {
			return ResponseEntity.badRequest().body(new ResponseError("Enter your remark"));
		}
		Levels toAccountLevel = userRepository.findByUsername(toAccount.getUser().getUsername()).get().getLevels();
		if (toAccountLevel.equals(Levels.LEVEL_1)) {
			maxBalanceL1 = settingRepository.findBysettingName("Balance Limit (LEVEL_1)").get().getValue();
		} else if (toAccountLevel.equals(Levels.LEVEL_2)) {
			maxBalanceL1 = settingRepository.findBysettingName("Balance Limit (LEVEL_2)").get().getValue();
		} else if (toAccountLevel.equals(Levels.LEVEL_3)) {
			maxBalanceL1 = settingRepository.findBysettingName("Balance Limit (LEVEL_3)").get().getValue();
		}
		List<Transaction> debitcurr = new ArrayList<Transaction>();
		List<DebitTransaction> debitTransactions = debitTransactionRepository.findBycreateDate(today);
		String transactioncode ="";
		for (int j = 0; j < 6; j++) {
			
			transactioncode=transactioncode+randomNumber.randomNumberGenerator(0, 9);
		}
		for (int i = 0; i < debitTransactions.size(); i++) {
			if (debitTransactions.get(i).getAccount().getAccountNumber().equals(fromAccount.getAccountNumber())) {
				debitcurr.add(debitcurr.get(i));

			}

		}
		BigDecimal minbalance = amount.add(transactionfee);
		try {
			fromaccountBalance = accountBalanceRepository.findByaccount(fromAccount).get();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ResponseError("Account Balance Does not Exist"));
		}

		try {
			toaccountBalance = accountBalanceRepository.findByaccount(toAccount).get();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ResponseError("Account Balance Does not Exist"));
		}
		try {
			bazraBalance = bazraBalanceRepository.findByaccount(bazraAccount).get();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ResponseError("Account Balance Does not Exist"));
		}
		if (debitcurr.isEmpty()) {
			if (fromaccountBalance.getBalance().compareTo(BigDecimal.ONE) == 1
					&& fromaccountBalance.getBalance().compareTo(minbalance) == 1) {

				daily = daily.add(adminTransferRequest.getAmount());

				if (maxBalanceL1.compareTo(toaccountBalance.getBalance().add(adminTransferRequest.getAmount())) == -1) {
					return ResponseEntity.badRequest().body(new ResponseError("Receiver not able to accept"));
				}
				if (dailyTransferLimit.compareTo(daily.negate()) == 1) {
					fromaccountBalance.setBalance(fromaccountBalance.getBalance().subtract(minbalance));
					fromAccount.setDaily(daily.negate().add(adminTransferRequest.getAmount()));
					accountRepository.save(fromAccount);
					bazraBalance.setBalance(bazraBalance.getBalance().add(transactionfee.multiply(amount)));
					toaccountBalance.setBalance(toaccountBalance.getBalance().add(amount));
					accountRepository.save(toAccount);
					accountRepository.save(bazraAccount);
					Transaction transaction = new Transaction();
					transaction.setCreateDate(LocalDate.now());
					transaction.setTransactionAmount(amount);
					transaction.setTransactionCode(transactioncode);
					transaction.setTransactiontype(transactiontype);
					transactionRepository.save(transaction);
					Transaction transaction2 = transactionRepository.findBytransactionCode(transactioncode).get();
					DebitTransaction debitTransaction = new DebitTransaction();
					debitTransaction.setAccount(fromAccount);
					debitTransaction.setCreateDate(LocalDate.now());
					debitTransaction.setTransaction(transaction2);
					debitTransactionRepository.save(debitTransaction);
					
					CreditTransaction creditTransaction = new CreditTransaction();
					creditTransaction.setAccount(toAccount);
					creditTransaction.setCreateDate(LocalDate.now());
					creditTransaction.setTransaction(transaction2);
					creditTransactionRepository.save(creditTransaction);
					return ResponseEntity.ok(new UpdateResponse("Successful Transfer"));
				} else {

					return ResponseEntity.badRequest().body(new ResponseError("Daily Limit overpassed"));
				}
			}
		} else {
			daterepo = debitTransactions.get(debitTransactions.size() - 1).getCreateDate();

			if (fromaccountBalance.getBalance().compareTo(BigDecimal.ONE) == 1
					&& fromaccountBalance.getBalance().compareTo(minbalance) == 1) {

				for (int i = 0; i < debitcurr.size(); i++) {

					daily = daily.add(debitcurr.get(i).getTransactionAmount());

				}

				if (maxBalanceL1.compareTo(toaccountBalance.getBalance().add(adminTransferRequest.getAmount())) == -1) {
					return ResponseEntity.badRequest().body(new ResponseError("Receiver not able to accept"));
				}
				if (dailyTransferLimit.compareTo(daily.negate().add(minbalance)) == 1) {
					fromaccountBalance.setBalance(fromaccountBalance.getBalance().subtract(minbalance));
					fromAccount.setDaily(daily.negate().add(adminTransferRequest.getAmount()));
					accountRepository.save(fromAccount);
					bazraBalance.setBalance(bazraBalance.getBalance().add(transactionfee.multiply(amount)));
					toaccountBalance.setBalance(toaccountBalance.getBalance().add(amount));
					accountRepository.save(toAccount);
					accountRepository.save(bazraAccount);
					Transaction transaction = new Transaction();
					transaction.setCreateDate(LocalDate.now());
					transaction.setTransactionAmount(amount);
					transaction.setTransactionCode(transactioncode);
					transaction.setTransactiontype(transactiontype);
					transactionRepository.save(transaction);
					
					Transaction transaction2 = transactionRepository.findBytransactionCode(transactioncode).get();
					DebitTransaction debitTransaction = new DebitTransaction();
					debitTransaction.setAccount(fromAccount);
					debitTransaction.setCreateDate(LocalDate.now());
					debitTransaction.setTransaction(transaction2);
					debitTransactionRepository.save(debitTransaction);
					
					CreditTransaction creditTransaction = new CreditTransaction();
					creditTransaction.setAccount(toAccount);
					creditTransaction.setCreateDate(LocalDate.now());
					creditTransaction.setTransaction(transaction2);
					creditTransactionRepository.save(creditTransaction);
					return ResponseEntity.ok(new UpdateResponse("Successful Transfer"));
				} else {

					return ResponseEntity.badRequest().body(new ResponseError("Daily Limit overpassed"));
				}

			}
		}

		return ResponseEntity.badRequest().body(new ResponseError("Insufficient balance"));
	}

	public ResponseEntity<?> withdraw(WithdrawRequest withdraw, String name) {
		Optional<Account> optionfrom =  accountRepository.findByAccountNumberEquals(name);
		AccountType accountType = accountTypeRepository.findByaccounttype("ADMIN").get();
		Optional<Account> optionAdmin = accountRepository.findByType(accountType);
		Account frAccount;
		
		Optional<Account> optionTo =accountRepository.findByAccountNumberEquals(withdraw.getFromAccountNumber());
		
		if (!optionAdmin.isPresent()) {
			return ResponseEntity.badRequest().body(new ResponseError("Invalid Admin Account"));
		}
		Account bazraAccount = optionAdmin.get();
		if (!optionfrom.isPresent()) {
			return ResponseEntity.badRequest().body(new ResponseError("Invalid Account"));
		}
		if (!optionTo.isPresent()) {
			return ResponseEntity.badRequest().body(new ResponseError("Invalid Destination Account"));
		}
		Account agAccount = optionTo.get();
		LocalDate today = LocalDate.now(ZoneId.systemDefault());
		Transactiontype transactiontype = transactionTypeRepository.findBytransactionType("WITHDRAW").get();
		BigDecimal daily = new BigDecimal(0);
		frAccount = optionfrom.get();
		
		if (frAccount.isStatus()) {
			return ResponseEntity.badRequest().body(new ResponseError("Account Blocked"));
		}
		Levels levels = userRepository.findById(frAccount.getUser().getId()).get().getLevels();
		if (levels == Levels.LEVEL_1) {
			commissionfee = settingRepository.findBysettingName("commission fee(LEVEL_1)").get().getValue();
			transactionfee = settingRepository.findBysettingName("Transaction Fee(LEVEL_1)").get().getValue();
			dailyTransferLimit = settingRepository.findBysettingName("Daily Transfer Limit").get().getValue();
		} else if (levels == Levels.LEVEL_2) {
			commissionfee = settingRepository.findBysettingName("commission fee(LEVEL_2)").get().getValue();
			transactionfee = settingRepository.findBysettingName("Transaction Fee(LEVEL_2)").get().getValue();
			dailyTransferLimit = settingRepository.findBysettingName("Daily Transfer Limit(LEVEL_2)").get().getValue();
		} else if (levels == Levels.LEVEL_3) {
			commissionfee = settingRepository.findBysettingName("commission fee(LEVEL_3)").get().getValue();
			transactionfee = settingRepository.findBysettingName("Transaction Fee(LEVEL_3)").get().getValue();
			dailyTransferLimit = settingRepository.findBysettingName("Daily Transfer Limit(LEVEL_3)").get().getValue();
		}
		
		if (!agAccount.isStatus()) {
			return ResponseEntity.badRequest().body(new ResponseError("Account Blocked"));
		}
		if (!agAccount.getType().getAccounttype().matches("AGENT")) {
			return ResponseEntity.badRequest().body(new ResponseError("Not an agent account"));
		}
		if (withdraw.getAmount() == null) {
			return ResponseEntity.badRequest().body(new ResponseError("Enter Valid Amount"));
		}
		try {
			fromaccountBalance = accountBalanceRepository.findByaccount(frAccount).get();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ResponseError("Account Balance Does not Exist"));
		}

		try {
			agAccountBalance = accountBalanceRepository.findByaccount(agAccount).get();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ResponseError("Account Balance Does not Exist"));
		}
		try {
			bazraBalance = bazraBalanceRepository.findByaccount(bazraAccount).get();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ResponseError("Account Balance Does not Exist"));
		}
		if (withdraw.getAmount().compareTo(agAccountBalance.getBalance()) == 1) {
			return ResponseEntity.badRequest()
					.body(new ResponseError("Agent has insufficient balance for your request"));
		}
		String transactioncode ="";
		for (int j = 0; j < 6; j++) {
			
			transactioncode=transactioncode+randomNumber.randomNumberGenerator(0, 9);
		}
		List<DebitTransaction> debitcurr = new ArrayList<DebitTransaction>();
		List<DebitTransaction> debitTransactions = debitTransactionRepository.findBycreateDate(today);
//		Account bazraAccount = accountRepository.findByType("ADMIN").get();
		
		for (int i = 0; i < debitTransactions.size(); i++) {
			if (debitTransactions.get(i).getAccount().getAccountNumber().equals(name)) {
				debitcurr.add(debitTransactions.get(i));

			}

		}
		BigDecimal balance = fromaccountBalance.getBalance();
		BigDecimal minbalance = withdraw.getAmount().add(commissionfee).add(transactionfee);
		if (debitcurr.isEmpty()) {

			if (balance.compareTo(minbalance) == 1) {
				daily = daily.add(withdraw.getAmount());
				if (dailyTransferLimit.compareTo(daily.negate().add(minbalance)) == 1) {
					System.out.println("daily" + daily);
					fromaccountBalance.setBalance(balance.subtract(minbalance));
					frAccount.setDaily(daily.negate().add(withdraw.getAmount()));
					accountRepository.save(frAccount);
					agAccountBalance.setBalance(agAccountBalance.getBalance().add(withdraw.getAmount()));
					agAccount
							.setCommission(agAccount.getCommission().add(commissionfee.multiply(withdraw.getAmount())));
					accountRepository.save(agAccount);
					bazraBalance
							.setBalance(bazraBalance.getBalance().add(transactionfee.multiply(withdraw.getAmount())));
					accountRepository.save(bazraAccount);
					Transaction transaction = new Transaction();
					transaction.setCreateDate(LocalDate.now());
					transaction.setTransactionAmount(withdraw.getAmount());
					transaction.setTransactionCode(transactioncode);
					transaction.setTransactiontype(transactiontype);
					transactionRepository.save(transaction);
					Transaction transaction2 = transactionRepository.findBytransactionCode(transactioncode).get();
					DebitTransaction debitTransaction = new DebitTransaction();
					debitTransaction.setAccount(frAccount);
					debitTransaction.setCreateDate(LocalDate.now());
					debitTransaction.setTransaction(transaction2);
					debitTransactionRepository.save(debitTransaction);
					
					CreditTransaction creditTransaction = new CreditTransaction();
					creditTransaction.setAccount(agAccount);
					creditTransaction.setCreateDate(LocalDate.now());
					creditTransaction.setTransaction(transaction2);
					creditTransactionRepository.save(creditTransaction);
					return ResponseEntity.ok(new UpdateResponse("Successful Withdraw"));

				} else {
					return ResponseEntity.badRequest().body(new ResponseError("Daily Limit overpassed"));
				}
			} else {
				return ResponseEntity.badRequest().body(new ResponseError("Insufficient balance"));
			}

		} else {
			if (balance.compareTo(minbalance) == 1) {
				for (int i = 0; i < debitcurr.size(); i++) {

					daily = daily.add(debitcurr.get(i).getTransaction().getTransactionAmount());
					System.out.println("dddd" + daily);
				}
				if (dailyTransferLimit.compareTo(daily.negate().add(minbalance)) == 1) {
					System.out.println("daaaiiillllyyy" + daily);
					fromaccountBalance.setBalance(balance.subtract(minbalance));
					System.out.println(balance.subtract(minbalance));
					frAccount.setDaily(daily.negate().add(withdraw.getAmount()));
					accountRepository.save(frAccount);
					agAccountBalance.setBalance(agAccountBalance.getBalance().add(withdraw.getAmount()));
					agAccount
							.setCommission(agAccount.getCommission().add(commissionfee.multiply(withdraw.getAmount())));
					accountRepository.save(agAccount);
					bazraBalance
							.setBalance(bazraBalance.getBalance().add(transactionfee.multiply(withdraw.getAmount())));
					accountRepository.save(bazraAccount);
					Transaction transaction = new Transaction();
					transaction.setCreateDate(LocalDate.now());
					transaction.setTransactionAmount(withdraw.getAmount());
					transaction.setTransactionCode(transactioncode);
					transaction.setTransactiontype(transactiontype);
					transactionRepository.save(transaction);
					Transaction transaction2 = transactionRepository.findBytransactionCode(transactioncode).get();
					DebitTransaction debitTransaction = new DebitTransaction();
					debitTransaction.setAccount(frAccount);
					debitTransaction.setCreateDate(LocalDate.now());
					debitTransaction.setTransaction(transaction2);
					debitTransactionRepository.save(debitTransaction);
					
					CreditTransaction creditTransaction = new CreditTransaction();
					creditTransaction.setAccount(agAccount);
					creditTransaction.setCreateDate(LocalDate.now());
					creditTransaction.setTransaction(transaction2);
					creditTransactionRepository.save(creditTransaction);

					return ResponseEntity.ok(new UpdateResponse("Successful Withdraw"));
				} else {
					return ResponseEntity.badRequest()
							.body(new ResponseError("Daily Limit overpassed! Available transfer amount is "
									+ (dailyTransferLimit.subtract(frAccount.getDaily()))));
				}

			} else {
				return ResponseEntity.badRequest().body(new ResponseError("Insufficient balance"));
			}

		}

	}

	public ResponseEntity<?> Deposit(DepositRequest depositRequest, String name) {
		Optional<Account> optionfrom =  accountRepository.findByAccountNumberEquals(name);
		Optional<Account> optionto =  accountRepository.findByAccountNumberEquals(depositRequest.getToAccountNumber());
		AccountType accountType = accountTypeRepository.findByaccounttype("ADMIN").get();
		Optional<Account> optionAdmin = accountRepository.findByType(accountType);
		Transactiontype transactiontype = transactionTypeRepository.findBytransactionType("DEPOSIT").get();
		LocalDate today = LocalDate.now(ZoneId.systemDefault());
		if (!optionfrom.isPresent()) {
			return ResponseEntity.badRequest().body(new ResponseError("Invalid agent account"));
		}
		if (!optionto.isPresent()) {
			return ResponseEntity.badRequest().body(new ResponseError("Invalid user account"));
		}
		if (!optionAdmin.isPresent()) {
			return ResponseEntity.badRequest().body(new ResponseError("No bazra account found"));
		}
		agentDepositLimit = settingRepository.findBysettingName("Agent Deposit Limit").get().getValue();
		Account agAccount;
		Account toAccount;
		Account bazraAccount = optionAdmin.get();
		try {
			agAccount = optionfrom.get();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ResponseError("Invalid account"));
		}
		if (!agAccount.getType().getAccounttype().matches("AGENT")) {
			return ResponseEntity.badRequest().body(new ResponseError("Not an agent account"));
		}
		try {
			toAccount = optionto.get();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ResponseError("Invalid account"));
		}
		if (toAccount == null) {
			return ResponseEntity.badRequest().body(new ResponseError("Invalid account"));
		}
		if (toAccount.isStatus()) {
			return ResponseEntity.badRequest().body(new ResponseError("Account Blocked"));
		}

		if (depositRequest.getAmount() == null) {
			return ResponseEntity.badRequest().body(new ResponseError("Enter Valid Amount"));
		}
		try {
			toaccountBalance = accountBalanceRepository.findByaccount(toAccount).get();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ResponseError("Account Balance Does not Exist"));
		}

		try {
			agAccountBalance = accountBalanceRepository.findByaccount(agAccount).get();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ResponseError("Account Balance Does not Exist"));
		}
		try {
			bazraBalance = bazraBalanceRepository.findByaccount(bazraAccount).get();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ResponseError("Account Balance Does not Exist"));
		}
		String transactioncode ="";
		for (int j = 0; j < 6; j++) {
			
			transactioncode=transactioncode+randomNumber.randomNumberGenerator(0, 9);
		}
		Levels toAccountLevel = userRepository.findByUsername(toAccount.getUser().getUsername()).get().getLevels();
		if (toAccountLevel.equals(Levels.LEVEL_1)) {
			maxBalanceL1 = settingRepository.findBysettingName("Balance Limit (LEVEL_1)").get().getValue();
		} else if (toAccountLevel.equals(Levels.LEVEL_2)) {
			maxBalanceL1 = settingRepository.findBysettingName("Balance Limit (LEVEL_2)").get().getValue();
		} else if (toAccountLevel.equals(Levels.LEVEL_3)) {
			maxBalanceL1 = settingRepository.findBysettingName("Balance Limit (LEVEL_3)").get().getValue();
		}
		BigDecimal balance = agAccountBalance.getBalance();
		if (agentDepositLimit.compareTo(depositRequest.getAmount()) == -1) {
			return ResponseEntity.badRequest().body(new ResponseError("Deposit Limit overpassed"));
		}
		if (maxBalanceL1.compareTo(toaccountBalance.getBalance().add(depositRequest.getAmount())) == -1) {
			return ResponseEntity.badRequest().body(new ResponseError("User Account not able to accept"));
		}
		if (balance.compareTo(depositRequest.getAmount()) == 1) {
			agAccountBalance.setBalance(balance.subtract(depositRequest.getAmount()));
			accountRepository.save(agAccount);
			toaccountBalance.setBalance(toaccountBalance.getBalance().add(depositRequest.getAmount()));
			accountRepository.save(toAccount);
			Transaction transaction = new Transaction();
			transaction.setCreateDate(LocalDate.now());
			transaction.setTransactionAmount(depositRequest.getAmount());
			transaction.setTransactionCode(transactioncode);
			transaction.setTransactiontype(transactiontype);
			transactionRepository.save(transaction);
			Transaction transaction2 = transactionRepository.findBytransactionCode(transactioncode).get();
			DebitTransaction debitTransaction = new DebitTransaction();
			debitTransaction.setAccount(agAccount);
			debitTransaction.setCreateDate(LocalDate.now());
			debitTransaction.setTransaction(transaction2);
			debitTransactionRepository.save(debitTransaction);
			
			CreditTransaction creditTransaction = new CreditTransaction();
			creditTransaction.setAccount(toAccount);
			creditTransaction.setCreateDate(LocalDate.now());
			creditTransaction.setTransaction(transaction2);
			creditTransactionRepository.save(creditTransaction);
			return ResponseEntity
					.ok(new UpdateResponse("Successful Deposit"));
		}

		return ResponseEntity.badRequest().body(new ResponseError("Agent has Insufficient balance"));
	}

	public ResponseEntity<?> pay(DepositRequest depositRequest, String name) {
		Optional<Account> optionfrom =  accountRepository.findByAccountNumberEquals(name);
		Optional<Account> optionto =  accountRepository.findByAccountNumberEquals(depositRequest.getToAccountNumber());
		AccountType accountType = accountTypeRepository.findByaccounttype("ADMIN").get();
		Optional<Account> optionAdmin = accountRepository.findByType(accountType);
		Transactiontype transactiontype = transactionTypeRepository.findBytransactionType("PAY").get();
		LocalDate today = LocalDate.now(ZoneId.systemDefault());
		if (!optionfrom.isPresent()) {
			return ResponseEntity.badRequest().body(new ResponseError("Invalid account"));
		}
		if (!optionto.isPresent()) {
			return ResponseEntity.badRequest().body(new ResponseError("Invalid Merchant account"));
		}
		if (!optionAdmin.isPresent()) {
			return ResponseEntity.badRequest().body(new ResponseError("No valid admin account"));
		}
		Account frAccount = optionfrom.get();
		Account merAccount = optionto.get();
		Account bazraAccount = optionAdmin.get();
		try {
			merAccount = accountRepository.findByAccountNumberEquals(depositRequest.getToAccountNumber()).get();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ResponseError("Invalid account"));
		}
		if (merAccount == null) {
			return ResponseEntity.badRequest().body(new ResponseError("Invalid account"));
		}
		if (!merAccount.getType().getAccounttype().matches("MERCHANT")) {
			return ResponseEntity.badRequest().body(new ResponseError("Not a merchant account"));
		}
		if (depositRequest.getAmount() == null) {
			return ResponseEntity.badRequest().body(new ResponseError("Enter Valid Amount"));
		}
		try {
			fromaccountBalance = accountBalanceRepository.findByaccount(frAccount).get();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ResponseError("Account Balance Does not Exist"));
		}

		try {
			agAccountBalance = accountBalanceRepository.findByaccount(merAccount).get();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ResponseError("Account Balance Does not Exist"));
		}
		try {
			bazraBalance = bazraBalanceRepository.findByaccount(bazraAccount).get();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ResponseError("Account Balance Does not Exist"));
		}
		String transactioncode ="";
		for (int j = 0; j < 6; j++) {
			
			transactioncode=transactioncode+randomNumber.randomNumberGenerator(0, 9);
		}
		BigDecimal balance = fromaccountBalance.getBalance();
		if (balance.compareTo(depositRequest.getAmount()) == 1) {
			fromaccountBalance.setBalance(balance.subtract(depositRequest.getAmount()));
			accountRepository.save(frAccount);
			agAccountBalance.setBalance(agAccountBalance.getBalance().add(depositRequest.getAmount()));
			accountRepository.save(merAccount);
			Transaction transaction = new Transaction();
			transaction.setCreateDate(LocalDate.now());
			transaction.setTransactionAmount(depositRequest.getAmount());
			transaction.setTransactionCode(transactioncode);
			transaction.setTransactiontype(transactiontype);
			transactionRepository.save(transaction);
			Transaction transaction2 = transactionRepository.findBytransactionCode(transactioncode).get();
			DebitTransaction debitTransaction = new DebitTransaction();
			debitTransaction.setAccount(frAccount);
			debitTransaction.setCreateDate(LocalDate.now());
			debitTransaction.setTransaction(transaction2);
			debitTransactionRepository.save(debitTransaction);
			
			CreditTransaction creditTransaction = new CreditTransaction();
			creditTransaction.setAccount(merAccount);
			creditTransaction.setCreateDate(LocalDate.now());
			creditTransaction.setTransaction(transaction2);
			creditTransactionRepository.save(creditTransaction);
			return ResponseEntity
					.ok(new UpdateResponse("Payment Successful"));
		}

		return ResponseEntity.badRequest().body(new ResponseError("Insufficient balance"));
	}

}
