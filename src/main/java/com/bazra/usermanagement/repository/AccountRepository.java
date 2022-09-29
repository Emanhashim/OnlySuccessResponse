package com.bazra.usermanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bazra.usermanagement.model.Account;
import com.bazra.usermanagement.model.AccountType;
import com.bazra.usermanagement.model.AdminInfo;
import com.bazra.usermanagement.model.AgentInfo;
import com.bazra.usermanagement.model.MerchantInfo;
import com.bazra.usermanagement.model.UserInfo;



@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
   Page<Account> findByAccountNumberEquals(String accountnumber,Pageable pageable);
//   Page<Account> findByusername(String username,Pageable pageable);
   Optional<Account> findByuser_id(Integer userid);
   Optional<Account> findByAccountNumberEquals(String accountnumber);
   Optional<Account> findByType(AccountType accountType);
   Optional<Account> findByUser(UserInfo userInfo);
   Optional<Account> findByAdmin(AdminInfo adminInfo);
   Optional<Account> findByAgent(AgentInfo agentInfo);
   Optional<Account> findByMerchant(MerchantInfo merchantInfo);
//   List<Account> findAll(String accountNumber,Sort sort);
}
