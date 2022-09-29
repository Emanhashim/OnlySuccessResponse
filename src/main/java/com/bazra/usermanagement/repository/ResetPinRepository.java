package com.bazra.usermanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bazra.usermanagement.model.Promotion;
import com.bazra.usermanagement.model.ResetPin;
import com.bazra.usermanagement.model.UserInfo;

public interface ResetPinRepository extends JpaRepository<ResetPin, Integer>{
	Optional<ResetPin> findByuserInfo(UserInfo userInfo);
//	Optional<ResetPin> findByuserid(Integer id);
	
}
