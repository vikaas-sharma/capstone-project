package com.example.user_service.service;

import java.util.List;

import com.example.user_service.model.User;

public interface UserService {
    User saveUser(User user);
    User updateUser(Integer userId, User user);
    User getUserById(Integer userId);
    List<User> getAllUsers();
    void deleteUser(Integer userId);
	boolean existsById(Integer userId);
}