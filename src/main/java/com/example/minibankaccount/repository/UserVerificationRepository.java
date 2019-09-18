package com.example.minibankaccount.repository;

import com.example.minibankaccount.model.user.UserVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserVerificationRepository extends JpaRepository<UserVerificationToken, Long> {
    UserVerificationToken findByConfirmationToken(String confirmationToken);
}
