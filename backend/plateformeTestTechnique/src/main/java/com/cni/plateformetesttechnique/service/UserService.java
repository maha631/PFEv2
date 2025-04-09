package com.cni.plateformetesttechnique.service;

import com.cni.plateformetesttechnique.model.User;

public interface UserService {
    void createPasswordResetTokenForUser(User user, String token);
    User findUserByEmail(String email);
    void changeUserPassword(User user, String newPassword);
    boolean userExistsByEmail(String email);
    User findByUsername(String username);
}