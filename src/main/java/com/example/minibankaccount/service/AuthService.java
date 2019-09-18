package com.example.minibankaccount.service;

import com.example.minibankaccount.exeption.AppException;
import com.example.minibankaccount.exeption.ResourceNotFoundException;
import com.example.minibankaccount.model.role.Role;
import com.example.minibankaccount.model.role.RoleName;
import com.example.minibankaccount.model.user.User;
import com.example.minibankaccount.model.user.UserVerificationToken;
import com.example.minibankaccount.payload.ApiResponse;
import com.example.minibankaccount.payload.auth.JwtAuthenticationResponse;
import com.example.minibankaccount.payload.auth.LoginRequest;
import com.example.minibankaccount.payload.auth.SignUpRequest;
import com.example.minibankaccount.repository.RoleRepository;
import com.example.minibankaccount.repository.UserRepository;
import com.example.minibankaccount.repository.UserVerificationRepository;
import com.example.minibankaccount.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;


@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final MailService mailSender;
    private final UserVerificationRepository verificationToken;

    @Autowired
    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, MailService mailSender, UserVerificationRepository verificationToken) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.mailSender = mailSender;
        this.verificationToken = verificationToken;
    }

    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtTokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    public ResponseEntity<?> registerUser(SignUpRequest signUpRequest){
        if(userRepository.existsByUsername(signUpRequest.getUsername())){
            return new ResponseEntity<>(new ApiResponse("error",HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), "Username is already taken"), HttpStatus.BAD_REQUEST);
        }

        if(userRepository.existsByEmail(signUpRequest.getEmail())){
            return new ResponseEntity<>(new ApiResponse("error",HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), "Email is already taken"), HttpStatus.BAD_REQUEST);
        }
        String firstName = signUpRequest.getFirstName().substring(0, 1).toUpperCase() + signUpRequest.getFirstName().substring(1).toLowerCase();

        String lastName = signUpRequest.getLastName().substring(0, 1).toUpperCase() + signUpRequest.getLastName().substring(1).toLowerCase();

        String username = signUpRequest.getUsername().toLowerCase();

        String email = signUpRequest.getEmail().toLowerCase();

        User user = new User(firstName, lastName, username, email, signUpRequest.getPassword(), false, true, true, true);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (roleRepository.count() == 0) {
            Role role = new Role();
            role.setName(RoleName.ROLE_CLIENT);
            roleRepository.save(role);
            role.setName(RoleName.ROLE_MANAGER);
            roleRepository.save(role);
            role.setName(RoleName.ROLE_ADMIN);
            roleRepository.save(role);
        }

        List<Role> roles = new ArrayList<>();
        if(userRepository.count() == 0){
            roles.add(roleRepository.findByName(RoleName.ROLE_ADMIN).orElseThrow(() -> new AppException("User role not set")));
            roles.add(roleRepository.findByName(RoleName.ROLE_MANAGER).orElseThrow(() -> new AppException("User role not set")));
        } else{
            roles.add(roleRepository.findByName(RoleName.ROLE_CLIENT).orElseThrow(() -> new AppException("User role not set")));
        }

        user.setRoles(roles);

        User result = userRepository.save(user);

        UserVerificationToken confirmationToken = new UserVerificationToken(user);

        verificationToken.save(confirmationToken);

        emailConfSender(result);

        return new ResponseEntity<>(new ApiResponse("success", HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), "Email confirmation pendenting!"), HttpStatus.OK);
    }

    public ResponseEntity<?> resendConfToken(String email){
        User user = userRepository.findByEmail(email).orElseThrow(()-> new ResourceNotFoundException("User", "email", email));

        emailConfSender(user);

        return new ResponseEntity<>(new ApiResponse("success", HttpStatus.OK.value() ,HttpStatus.OK.getReasonPhrase(), "Token set successfully"), HttpStatus.OK);

    }




    public ResponseEntity<?> confirmAccount(String confirmationToken){
        UserVerificationToken token = verificationToken.findByConfirmationToken(confirmationToken);

        if (token != null) {
            User user = userRepository.findByEmailIgnoreCase(token.getUser().getEmail());
            user.setEnabled(true);

            User result = userRepository.save(user);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentContextPath().path("/api/users/{userId}")
                    .buildAndExpand(result.getId()).toUri();

            return ResponseEntity.created(location).body(new ApiResponse("success", HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), "Email confirmed succesfully"));
        }

        return new ResponseEntity<>(new ApiResponse("error",HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), "Token not found"), HttpStatus.BAD_REQUEST);
    }
    private void emailConfSender(User user){


        UserVerificationToken confirmationToken = new UserVerificationToken(user);

        if (!user.isEnabled() || !user.isConfirmedEmail()){

            confirmationToken = verificationToken.save(confirmationToken);

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(user.getEmail());
            mailMessage.setSubject("Complete Registration!");
            mailMessage.setFrom("aurel.marishta@atis.com");
            mailMessage.setText("This is your token to confirm your account: "
                    + confirmationToken.getConfirmationToken());

            System.out.println(mailMessage);

            mailSender.sendEmail(mailMessage);}
        else {
            throw new AppException("Email alredy confiremed!");
        }

    }
}
