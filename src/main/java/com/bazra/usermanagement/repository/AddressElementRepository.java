package com.bazra.usermanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bazra.usermanagement.model.AddressElement;
@Repository
public interface AddressElementRepository extends JpaRepository<AddressElement, Integer> {
	Optional<AddressElement> findBytitle(String username);

}


