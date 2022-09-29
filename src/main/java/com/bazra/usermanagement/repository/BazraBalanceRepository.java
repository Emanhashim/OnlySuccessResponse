package com.bazra.usermanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bazra.usermanagement.model.Account;
import com.bazra.usermanagement.model.BazraBalance;

@Repository
public interface BazraBalanceRepository extends JpaRepository<BazraBalance, Integer> {
	Optional<BazraBalance> findByaccount(Account account);

}
