package com.bazra.usermanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bazra.usermanagement.model.Account;
import com.bazra.usermanagement.model.CreditTransaction;
@Repository
public interface CreditTransactionRepository extends JpaRepository<CreditTransaction, Integer> {
	List<CreditTransaction> findByaccount(Account account);
	

}
