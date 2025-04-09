package com.suki.bansachOnline.respository;


import com.suki.bansachOnline.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByFacebookId(String facebookId);
    Optional<User> findByGoogleId(String googleId);
    Optional<User> findByPhoneNumber(String phoneNumber);

}