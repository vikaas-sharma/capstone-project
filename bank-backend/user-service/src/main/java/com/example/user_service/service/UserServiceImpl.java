package com.example.user_service.service;

import com.example.user_service.exception.UserNotFoundException;
import com.example.user_service.model.User;
import com.example.user_service.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public User saveUser(User user) {
    	// ✅ Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Integer userId, User user) {
        User existing = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Sirf null na hone par hi update karna
        if (user.getUsername() != null) {
            existing.setUsername(user.getUsername());
        }

        if (user.getAddress() != null) {
            existing.setAddress(user.getAddress());
        }

     // ⚡ Encode password before saving
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return userRepository.save(existing);
    }


    @Override
    public User getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(Integer userId) {
        userRepository.deleteById(userId);
    }
    
    // ✅ Implement existsById
    @Override
    public boolean existsById(Integer userId) {
        return userRepository.existsById(userId);
    }
}
