package com.example.minibankaccount.service;

import com.example.minibankaccount.exeption.BadRequestException;
import com.example.minibankaccount.exeption.ResourceNotFoundException;
import com.example.minibankaccount.model.account.Account;
import com.example.minibankaccount.model.transaction.Transaction;
import com.example.minibankaccount.model.user.User;
import com.example.minibankaccount.payload.ApiResponse;
import com.example.minibankaccount.payload.PagedResponse;
import com.example.minibankaccount.payload.account.AccountSummary;
import com.example.minibankaccount.payload.client.ClientProfile;
import com.example.minibankaccount.payload.client.ClientSummary;
import com.example.minibankaccount.repository.AccountRepository;
import com.example.minibankaccount.repository.TransactionRepository;
import com.example.minibankaccount.repository.UserRepository;
import com.example.minibankaccount.security.UserPrincipal;
import com.example.minibankaccount.util.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Service
public class ClientService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public ClientService(UserRepository userRepository, AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }


    public ResponseEntity<?> getTransactions(int page, int size, Long accountId, UserPrincipal currentUser){
        Account account = accountRepository.findById(accountId).orElseThrow(()->new ResourceNotFoundException("Account", "id", accountId));
        if (account.getUser().getId() == currentUser.getId()){
            validatePageNumberAndSize(page, size);
            Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "dateOfTransaction");
            Page<Transaction> transactions = transactionRepository.getAllByAccount_AccountId(accountId, pageable);

            return getResponseEntity(transactions);
        }
        return new ResponseEntity<>(new ApiResponse("unauthorized", HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), "You are not authorized to take this action!"),HttpStatus.UNAUTHORIZED);
    }


    public ResponseEntity<?> getCurrentClientProfile(int page, int size, UserPrincipal currentUser) {
        User user = userRepository.findById(currentUser.getId()).orElseThrow(()->new ResourceNotFoundException("User", "id", currentUser.getId()));
        validatePageNumberAndSize(page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Account> accounts = accountRepository.findAllByUserId(currentUser.getId(), pageable);

        if (accounts.getNumberOfElements() == 0 ){
            PagedResponse<Account> accountPagedResponse = new PagedResponse<>(Collections.emptyList(), accounts.getNumber(), accounts.getSize(), accounts.getTotalElements(), accounts.getTotalPages(), accounts.isLast());
            return new ResponseEntity<>(accountPagedResponse, HttpStatus.OK);
        }
        PagedResponse<Account> accountPagedResponse = new PagedResponse<>(accounts.getContent(), accounts.getNumber(), accounts.getSize(), accounts.getTotalElements(), accounts.getTotalPages(), accounts.isLast());

        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setUser(user);
        clientProfile.setAccounts(accountPagedResponse);

        return new ResponseEntity<>(clientProfile, HttpStatus.OK);
    }

    public ResponseEntity<?> getClientProfile(String email){
        User user = userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("User", "email", email));
        List<AccountSummary> accountSummaries = new ArrayList<>();
        for (Account account: user.getAccounts()) {
            AccountSummary accountSummary = new AccountSummary();
            accountSummary.setId(account.getAccountId());
            accountSummary.setAccountName(account.getAccountName());
            accountSummaries.add(accountSummary);
        }
        ClientSummary clientSummary = new ClientSummary();
        clientSummary.setId(user.getId());
        clientSummary.setName(user.getFirstName()+" "+user.getLastName());
        clientSummary.setUsername(user.getUsername());
        clientSummary.setEmail(user.getEmail());
        clientSummary.setAccountSummaries(accountSummaries);
        return new ResponseEntity<>(clientSummary, HttpStatus.OK);
    }

    static ResponseEntity<?> getResponseEntity(Page<?> entities) {
        if (entities.getNumberOfElements() == 0 ){
            PagedResponse<?> accountPagedResponse = new PagedResponse<>(Collections.emptyList(), entities.getNumber(), entities.getSize(), entities.getTotalElements(), entities.getTotalPages(), entities.isLast());
            return new ResponseEntity<>(accountPagedResponse, HttpStatus.OK);
        }
        PagedResponse<?> accountPagedResponse = new PagedResponse<>(entities.getContent(), entities.getNumber(), entities.getSize(), entities.getTotalElements(), entities.getTotalPages(), entities.isLast());
        return new ResponseEntity<>(accountPagedResponse, HttpStatus.OK);
    }

    private void validatePageNumberAndSize(int page, int size) {
        if(page < 0) {
            throw new BadRequestException("Page number cannot be less than zero.");
        }

        if(size < 0) {
            throw new BadRequestException("Size number cannot be less than zero.");
        }

        if(size > AppConstants.MAX_PAGE_SIZE) {
            throw new BadRequestException("Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE);
        }
    }

}
