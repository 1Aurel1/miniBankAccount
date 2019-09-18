package com.example.minibankaccount.service;

import com.example.minibankaccount.exeption.BadRequestException;
import com.example.minibankaccount.exeption.ResourceNotFoundException;
import com.example.minibankaccount.model.account.Account;
import com.example.minibankaccount.model.transaction.Transaction;
import com.example.minibankaccount.model.transaction.TransactionState;
import com.example.minibankaccount.payload.ApiResponse;
import com.example.minibankaccount.payload.PagedResponse;
import com.example.minibankaccount.repository.AccountRepository;
import com.example.minibankaccount.repository.TransactionRepository;
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

@Service
public class ManagerService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public ManagerService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public ResponseEntity<?> approveTransaction(Long transactionId){

        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(()->new ResourceNotFoundException("Transaction", "id", transactionId));

        switch (transaction.getTransactionType()){
            case DEPOSIT:
                transaction.setTransactionState(TransactionState.APPROVED);
                Account account = transaction.getAccount();
                int tempBalance = account.getCurrentBalance();
                account.setCurrentBalance(tempBalance + transaction.getAmount());
                accountRepository.save(account);

                return new ResponseEntity<>(account,HttpStatus.OK);
            default:
                return null;
        }


    }

    public ResponseEntity<?> rejectTransaction(Long transactionId){

        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(()->new ResourceNotFoundException("Transaction", "id", transactionId));

        switch (transaction.getTransactionType()){
            case DEPOSIT:
                transaction.setTransactionState(TransactionState.APPROVED);


                Account account = transaction.getAccount();
                int tempBalance = account.getCurrentBalance();
                account.setCurrentBalance(tempBalance + transaction.getAmount());
                accountRepository.save(account);

                return new ResponseEntity<>(account,HttpStatus.OK);
            default:
                return null;
        }


    }

    public ResponseEntity<?> getUnDecidedTransactions(int page, int size){
        validatePageNumberAndSize(page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "dateOfTransaction");
        Page<Transaction> transactions = transactionRepository.getAllByTransactionState(TransactionState.UN_DECIDED ,pageable);
        return getResponseEntity(transactions);
    }

    public ResponseEntity<?> getRejectedTransactions(int page, int size){
        validatePageNumberAndSize(page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "dateOfTransaction");
        Page<Transaction> transactions = transactionRepository.getAllByTransactionState(TransactionState.REJECTED ,pageable);
        return getResponseEntity(transactions);
    }

    public ResponseEntity<?> getApprovedTransactions(int page, int size){
        validatePageNumberAndSize(page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "dateOfTransaction");
        Page<Transaction> transactions = transactionRepository.getAllByTransactionState(TransactionState.APPROVED ,pageable);
        return getResponseEntity(transactions);
    }

    public ResponseEntity<?> getUnAuthorizedAccounts(int page, int size) {
        validatePageNumberAndSize(page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Account> accounts = accountRepository.findAllByApprovedIsFalse(pageable);

        return getResponseEntity(accounts);
    }

    public ResponseEntity<?> approveAccount(Long accountId){
        Account account = accountRepository.findById(accountId).orElseThrow(()->new ResourceNotFoundException("Account", "id", accountId));
        account.setApproved(true);
        account = accountRepository.save(account);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    public ResponseEntity<?> deleteAccount(Long accountId){
        Account account = accountRepository.findById(accountId).orElseThrow(()-> new ResourceNotFoundException("Account", "id", accountId));
        accountRepository.delete(account);
        return new ResponseEntity<>(new ApiResponse("success", HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), "Deleted sucessfully!"), HttpStatus.OK);
    }

    public ResponseEntity<?> disableAccount(Long accountId){
        Account account = accountRepository.findById(accountId).orElseThrow(()-> new ResourceNotFoundException("Account", "id", accountId));
        account.setEnabled(false);
        accountRepository.save(account);
        return new ResponseEntity<>(new ApiResponse("success", HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), "Disabled sucessfully!"), HttpStatus.OK);
    }

    public ResponseEntity<?> enableAccount(Long accountId){
        Account account = accountRepository.findById(accountId).orElseThrow(()-> new ResourceNotFoundException("Account", "id", accountId));
        account.setEnabled(true);
        accountRepository.save(account);
        return new ResponseEntity<>(new ApiResponse("success", HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), "Enabled sucessfully!"), HttpStatus.OK);
    }

    private static ResponseEntity<?> getResponseEntity(Page<?> entities) {
        if (entities.getNumberOfElements() == 0 ){
            PagedResponse<?> accountPagedResponse = new PagedResponse<>(Collections.emptyList(), entities.getNumber(), entities.getSize(), entities.getTotalElements(), entities.getTotalPages(), entities.isLast());
            return new ResponseEntity<>(accountPagedResponse, HttpStatus.OK);
        }
        PagedResponse<?> pagedResponse = new PagedResponse<>(entities.getContent(), entities.getNumber(), entities.getSize(), entities.getTotalElements(), entities.getTotalPages(), entities.isLast());
        return new ResponseEntity<>(pagedResponse, HttpStatus.OK);
    }

    private static void validatePageNumberAndSize(int page, int size) {
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
