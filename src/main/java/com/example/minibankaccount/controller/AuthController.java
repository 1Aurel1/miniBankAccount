package com.example.minibankaccount.controller;


import com.example.minibankaccount.payload.auth.LoginRequest;
import com.example.minibankaccount.payload.auth.SignUpRequest;
import com.example.minibankaccount.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
        return authService.authenticateUser(loginRequest);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest){
        return authService.registerUser(signUpRequest);
    }

    @RequestMapping(value = "/confirm-email", method = {RequestMethod.POST})
    public ResponseEntity<?> confirmUserAccount(@RequestParam("token") String confirmationToken) {
        return authService.confirmAccount(confirmationToken);
    }

    @RequestMapping(value = "/request-confirmation-token", method = {RequestMethod.POST})
    public ResponseEntity<?> requestConfToken(@RequestParam("email") String email){
        return authService.resendConfToken(email);
    }

}
