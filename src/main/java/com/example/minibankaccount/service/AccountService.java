package com.example.minibankaccount.service;

import com.example.minibankaccount.exeption.BadRequestException;
import com.example.minibankaccount.exeption.ResourceNotFoundException;
import com.example.minibankaccount.model.account.Account;
import com.example.minibankaccount.model.transaction.Transaction;
import com.example.minibankaccount.model.user.User;
import com.example.minibankaccount.payload.ApiResponse;
import com.example.minibankaccount.payload.PagedResponse;
import com.example.minibankaccount.payload.account.NewAccountRequest;
import com.example.minibankaccount.payload.account.UpdateAccountRequest;
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

import java.util.Collections;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;


    @Autowired
    public AccountService(AccountRepository accountRepository, UserRepository userRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public ResponseEntity<?> getUserAccounts(int page, int size, UserPrincipal currentUser) {
        validatePageNumberAndSize(page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Account> accounts = accountRepository.findAllByUserId(currentUser.getId(), pageable);

        return getResponseEntity(accounts);
    }


    public ResponseEntity<?> getAccount(Long accountId, UserPrincipal currentUser){
        Account account = accountRepository.findById(accountId).orElseThrow(()-> new ResourceNotFoundException("Account", "id", accountId));
        if (account.getUser().getId()  == currentUser.getId()){
            return new ResponseEntity<>(account, HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponse("unauthorized", HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), "You are not authorized to take this action!"),HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<?> addAccount(NewAccountRequest newAccountRequest, UserPrincipal currentUser){
        User user = userRepository.findById(currentUser.getId()).orElseThrow(()-> new ResourceNotFoundException("User", "id", currentUser.getId()));

        Account account = new Account();

        List<Account> accounts = accountRepository.findAllByUserId(user.getId());
        for (Account account1:accounts) {
            if (account1.getAccountName().equals(newAccountRequest.getAccountName()))
                return new ResponseEntity<>(new ApiResponse("error", HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT.getReasonPhrase(), "Account name already used!"), HttpStatus.CONFLICT);
        }

        account.setAccountName(newAccountRequest.getAccountName());
        account.setCurrentBalance(newAccountRequest.getInitialBalance());
        account.setApproved(false);
        account.setEnabled(true);
        account.setUser(user);
        account = accountRepository.saveAndFlush(account);
        System.out.println(account.toString());

        return new  ResponseEntity<>(account, HttpStatus.CREATED);
    }

    public ResponseEntity<?> updateAccount(UpdateAccountRequest updateAccountRequest, UserPrincipal currentUser){
        Account account = accountRepository.findById(updateAccountRequest.getId()).orElseThrow(()-> new ResourceNotFoundException("User", "id", currentUser.getId()));
        if (account.getUser().getId() == currentUser.getId()){
            if (updateAccountRequest.getAccountName().length() > 5){
                List<Account> accounts = accountRepository.findAllByUserId(currentUser.getId());
                for (Account account1:accounts) {
                    if (account1.getAccountName().equals(updateAccountRequest.getAccountName()))
                        return new ResponseEntity<>(new ApiResponse("error", HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT.getReasonPhrase(), "Account name already used!"), HttpStatus.CONFLICT);
                }
            }else {
                return new ResponseEntity<>( new ApiResponse("error", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), "Account name must contain at least 5 characters!"), HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(new ApiResponse("unauthorized", HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), "You are not authorized to take this action!"),HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<?> disableAccount(Long accountId, UserPrincipal currentUser){
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));
        if (account.getUser().getId() == currentUser.getId()){
            account.setEnabled(false);
            account.setApproved(false);
            accountRepository.save(account);
            return new ResponseEntity<>(new ApiResponse("success", HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), "Account closed successfully!"), HttpStatus.OK);
        }

        return new ResponseEntity<>(new ApiResponse("unauthorized", HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), "You are not authorized to take this action!"),HttpStatus.UNAUTHORIZED);
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
