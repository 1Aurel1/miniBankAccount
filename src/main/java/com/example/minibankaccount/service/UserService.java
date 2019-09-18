package com.example.minibankaccount.service;

import com.example.minibankaccount.exeption.AppException;
import com.example.minibankaccount.exeption.ResourceNotFoundException;
import com.example.minibankaccount.model.user.User;
import com.example.minibankaccount.model.user.UserVerificationToken;
import com.example.minibankaccount.payload.ApiResponse;
import com.example.minibankaccount.payload.user.UserSummary;
import com.example.minibankaccount.payload.user.UserUpdateRequest;
import com.example.minibankaccount.repository.UserRepository;
import com.example.minibankaccount.repository.UserVerificationRepository;
import com.example.minibankaccount.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserVerificationRepository verificationRepository;
    private final MailService mailService;


    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserVerificationRepository verificationRepository, MailService mailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationRepository = verificationRepository;
        this.mailService = mailService;
    }

    public ResponseEntity<?> updateUser(UserUpdateRequest userUpdateRequest, UserPrincipal currentUser){
        User user = userRepository.findById(currentUser.getId()).orElseThrow(()->new ResourceNotFoundException("User", "id", currentUser.getId()));
        if (userUpdateRequest.getFirstName().length() > 1) {
            user.setFirstName(userUpdateRequest.getFirstName());
        }
        else if(userUpdateRequest.getFirstName().isEmpty()){}else return new ResponseEntity<>(new ApiResponse("error", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), "First Name must be at least 2 characters!"), HttpStatus.BAD_REQUEST);

        if (userUpdateRequest.getLastName().length() > 1)
            user.setLastName(userUpdateRequest.getLastName());
        else if(userUpdateRequest.getLastName().isEmpty()){}else return new ResponseEntity<>(new ApiResponse("error", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), "Last Name must be at least 2 characters!"), HttpStatus.BAD_REQUEST);

        if (userUpdateRequest.getEmail().length() > 1)
        {
            user.setEmail(userUpdateRequest.getEmail());
            user.setConfirmedEmail(false);
            UserVerificationToken confirmationToken = new UserVerificationToken(user);
            verificationRepository.save(confirmationToken);

            emailConfSender(user);
        }
        else if(userUpdateRequest.getEmail().isEmpty()){}else return new ResponseEntity<>(new ApiResponse("error", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), "Last Name must be at least 2 characters!"), HttpStatus.BAD_REQUEST);

        if (userUpdateRequest.getPassword().length() > 1)
            user.setPassword(passwordEncoder.encode(userUpdateRequest.getPassword()));
        else if(userUpdateRequest.getPassword().isEmpty()){}else return new ResponseEntity<>(new ApiResponse("error", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), "Last Name must be at least 2 characters!"), HttpStatus.BAD_REQUEST);


        userRepository.save(user);

        UserSummary userSummary = new UserSummary((user.getFirstName() + " " + user.getLastName()), user.getEmail());

        return new ResponseEntity<>(userSummary, HttpStatus.OK);

    }


    private void emailConfSender(User user){


        UserVerificationToken confirmationToken = new UserVerificationToken(user);

        if (!user.isEnabled()){

            confirmationToken = verificationRepository.save(confirmationToken);

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(user.getEmail());
            mailMessage.setSubject("Complete Registration!");
            mailMessage.setFrom("aurel.marishta@atis.com");
            mailMessage.setText("This is your token to confirm your account: "
                    + confirmationToken.getConfirmationToken());

            System.out.println(mailMessage);

            mailService.sendEmail(mailMessage);}
        else {
            throw new AppException("Email alredy confiremed!");
        }

    }


}
