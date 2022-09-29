package com.bazra.usermanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bazra.usermanagement.model.LoginIDType;

@Repository
public interface LoginIdTypeRepository extends JpaRepository<LoginIDType, Integer> {
	Optional<LoginIDType> findBylogintype(String logintype);
}
