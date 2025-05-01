//package com.cni.plateformetesttechnique.repository;

//
//import com.cni.plateformetesttechnique.model.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//import java.util.Optional;
//
//@Repository
//public interface UserRepository extends JpaRepository<User, Long> {
//
//    Optional<User> findByEmail(String email);
//
//    Optional<User> findByUsername(String username);
//
//	Boolean existsByUsername(String username);
//
//	Boolean existsByEmail(String email);
//
//	Optional<User> findByActivationToken(String token);
//}
package com.cni.plateformetesttechnique.repository;

import com.cni.plateformetesttechnique.model.User;


import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	List<User> findByActiveFalse();
	List<User> findByActiveTrue();
	    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);

	Optional<User> findByActivationToken(String token);
	long countByActiveFalse();
	




}


