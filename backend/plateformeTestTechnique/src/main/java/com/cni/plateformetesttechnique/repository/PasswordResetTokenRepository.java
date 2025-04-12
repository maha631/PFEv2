package com.cni.plateformetesttechnique.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cni.plateformetesttechnique.model.PasswordResetToken;
import com.cni.plateformetesttechnique.model.User;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
	  void deleteByUser(User user);

	PasswordResetToken findByToken(String token);
	PasswordResetToken findByUser(User user);
}

