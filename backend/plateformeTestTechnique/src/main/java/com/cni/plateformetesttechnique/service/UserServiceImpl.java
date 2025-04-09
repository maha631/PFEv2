package com.cni.plateformetesttechnique.service;

import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cni.plateformetesttechnique.model.PasswordResetToken;
import com.cni.plateformetesttechnique.model.User;
import com.cni.plateformetesttechnique.repository.PasswordResetTokenRepository;
import com.cni.plateformetesttechnique.repository.UserRepository;

import jakarta.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void createPasswordResetTokenForUser(User user, String token) {
        // Supprimer les tokens existants pour cet utilisateur
        passwordResetTokenRepository.deleteByUser(user);
        
        PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(myToken);
    }
    
    @Override
    public User findUserByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        return userOptional.orElse(null);
    }
    
    @Override
    public void changeUserPassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    // Méthodes supplémentaires qui pourraient être utiles
    @Override
    public boolean userExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    public User findByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        return userOptional.orElse(null);
    }
}