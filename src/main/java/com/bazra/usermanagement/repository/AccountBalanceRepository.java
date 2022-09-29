package com.bazra.usermanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bazra.usermanagement.model.Account;
import com.bazra.usermanagement.model.AccountBalance;
@Repository
public interface AccountBalanceRepository extends JpaRepository<AccountBalance, Integer> {
	Optional<AccountBalance> findByaccount(Account account);
}
