package com.bazra.usermanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bazra.usermanagement.model.UserAuthentication;
import com.bazra.usermanagement.model.UserInfo;

public interface UserAuthenticationRepository extends JpaRepository<UserAuthentication, Integer> {
	Optional<UserAuthentication> findByuserInfo(UserInfo userInfo);

}
