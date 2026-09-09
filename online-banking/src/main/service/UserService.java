package com.bank.service;

import java.util.List;

import com.bank.entity.User;

public interface UserService {

    User getUserById(Long id);

    List<User> getAllUsers();

    User updateUser(Long id, User user);

    void deleteUser(Long id);
    
    void changePassword(
            String username,
            String oldPassword,
            String newPassword,
            String confirmPassword
    );
}