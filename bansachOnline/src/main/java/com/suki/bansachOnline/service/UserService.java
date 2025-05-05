package com.suki.bansachOnline.service;






import com.suki.bansachOnline.model.User;
import com.suki.bansachOnline.respository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User saveOrUpdateUser(String providerId, String name, String picture, String email, String provider) {
        Optional<User> userOptional = provider.equals("google") ?
                userRepository.findByGoogleId(providerId) :
                userRepository.findByFacebookId(providerId);

        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            user.setName(name);
            user.setPicture(picture);
            user.setEmail(email);
        } else {
            user = new User();
            if (provider.equals("google")) {
                user.setGoogleId(providerId);
            } else {
                user.setFacebookId(providerId);
            }
            user.setName(name);
            user.setPicture(picture);
            user.setEmail(email);
            user.setRole("USER"); // Mặc định là USER
        }
        return userRepository.save(user);
    }

    public User saveUser(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    public boolean isProfileComplete(User user) {
        return user.getPhoneNumber() != null && user.getAddress() != null && user.getDateOfBirth() != null;
    }

    public Optional<User> findByGoogleId(String googleId) {
        return userRepository.findByGoogleId(googleId);
    }

    public Optional<User> findByFacebookId(String facebookId) {
        return userRepository.findByFacebookId(facebookId);
    }

    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}