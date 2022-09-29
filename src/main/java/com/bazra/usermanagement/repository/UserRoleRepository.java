package com.bazra.usermanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bazra.usermanagement.model.Role;
import com.bazra.usermanagement.model.UserRoles;
@Repository
@Transactional(readOnly = true)
public interface UserRoleRepository extends JpaRepository<UserRoles, Integer> {
	Optional<UserRoles> findByrole(Role name);

}
