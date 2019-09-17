package com.example.minibankaccount.controller;

import com.example.minibankaccount.payload.transaction.NewTransactionRequest;
import com.example.minibankaccount.security.CurrentUser;
import com.example.minibankaccount.security.UserPrincipal;
import com.example.minibankaccount.service.TransactionService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api("Transactions")
@RestController
@RequestMapping("/api/transactions")
@PreAuthorize("hasRole('CLIENT')")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<?> makeTransaction(
            @Valid @RequestBody NewTransactionRequest newTransactionRequest,
            @CurrentUser UserPrincipal currentUser
            ){
        return transactionService.addTransaction(newTransactionRequest, currentUser);
    }

}
