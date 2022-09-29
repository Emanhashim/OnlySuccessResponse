package com.bazra.usermanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bazra.usermanagement.model.AccountType;
@Repository
public interface AccountTypeRepository  extends JpaRepository<AccountType, Integer> {
	Optional<AccountType> findByaccounttype(String username);
}
