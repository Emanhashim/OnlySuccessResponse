package com.bazra.usermanagement.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bazra.usermanagement.model.Account;
import com.bazra.usermanagement.model.DebitTransaction;
@Repository
public interface DebitTransactionRepository extends JpaRepository<DebitTransaction, Integer> {
	List<DebitTransaction> findByaccount(Account name);
	List<DebitTransaction> findBycreateDate(LocalDate localDate);


}
