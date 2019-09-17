package com.example.minibankaccount.service;

import com.example.minibankaccount.exeption.BadRequestException;
import com.example.minibankaccount.exeption.ResourceNotFoundException;
import com.example.minibankaccount.model.account.Account;
import com.example.minibankaccount.model.transaction.Transaction;
import com.example.minibankaccount.model.transaction.TransactionType;
import com.example.minibankaccount.model.transaction.Transfer;
import com.example.minibankaccount.payload.ApiResponse;
import com.example.minibankaccount.payload.PagedResponse;
import com.example.minibankaccount.payload.transaction.NewTransactionRequest;
import com.example.minibankaccount.repository.AccountRepository;
import com.example.minibankaccount.repository.TransactionRepository;
import com.example.minibankaccount.repository.UserRepository;
import com.example.minibankaccount.security.UserPrincipal;
import com.example.minibankaccount.util.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    public ResponseEntity<?> addTransaction(NewTransactionRequest newTransactionRequest, UserPrincipal currentUser){
        Account account = accountRepository.findById(newTransactionRequest.getAccountId()).orElseThrow(() -> new ResourceNotFoundException("Account", "id", newTransactionRequest.getAccountId()));
        if (account.getUser().getId() == currentUser.getId())
        {
            switch (newTransactionRequest.getTransactionType()){
                case 0:
                    Transaction transaction = new Transaction();
                    transaction.setAccount(account);
                    transaction.setAmount(newTransactionRequest.getAmount());
                    transaction.setDescription(newTransactionRequest.getDescription());
                    transaction.setTransactionType(TransactionType.DEPOSIT);
                    transaction = transactionRepository.saveAndFlush(transaction);

                    int tempBalance = account.getCurrentBalance();
                    account.setCurrentBalance(tempBalance + transaction.getAmount());
                    accountRepository.save(account);
                    return new ResponseEntity<>(transaction, HttpStatus.OK);
                case 1:
                    if (newTransactionRequest.getAmount() < account.getCurrentBalance()){
                        Transaction transaction1 = new Transaction();
                        transaction1.setAccount(account);
                        transaction1.setAmount(newTransactionRequest.getAmount());
                        transaction1.setDescription(newTransactionRequest.getDescription());
                        transaction1.setTransactionType(TransactionType.WITHDRAWAL);
                        transaction1 = transactionRepository.save(transaction1);

                        int tempBalance1 = account.getCurrentBalance();
                        account.setCurrentBalance(tempBalance1 - newTransactionRequest.getAmount());
                        accountRepository.save(account);
                        return new ResponseEntity<>(transaction1, HttpStatus.OK);
                    }else
                        return new ResponseEntity<>(new ApiResponse("error", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), "Unsuficesnt founds!"), HttpStatus.BAD_REQUEST);
                case 2:
                    if (newTransactionRequest.getAmount() < account.getCurrentBalance()){
                        Account receiver = accountRepository.findById(newTransactionRequest.getReceiverId()).orElseThrow(() -> new ResourceNotFoundException("Account", "id", newTransactionRequest.getReceiverId()));
                        Transfer transfer = new Transfer();
                        transfer.setAccount(account);
                        transfer.setAmount(newTransactionRequest.getAmount());
                        transfer.setDescription(newTransactionRequest.getDescription());
                        transfer.setTransactionType(TransactionType.TRANSFER);
                        transfer.setReceiver(receiver);
                        transfer = transactionRepository.save(transfer);

                        int tempBalance2 = account.getCurrentBalance();
                        account.setCurrentBalance(tempBalance2 - newTransactionRequest.getAmount());
                        accountRepository.save(account);

                        int tempBalance3 = receiver.getCurrentBalance();
                        receiver.setCurrentBalance(tempBalance3 + newTransactionRequest.getAmount());
                        accountRepository.save(receiver);
                        return new ResponseEntity<>(transfer, HttpStatus.OK);
                    }else
                        return new ResponseEntity<>(new ApiResponse("error", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), "Unsuficesnt founds!"), HttpStatus.BAD_REQUEST);
                default:
                    return new ResponseEntity<>(new ApiResponse("error", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), "Trasaction type not found!"), HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(new ApiResponse("unauthorized", HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), "You are not authorized to take this action!"),HttpStatus.UNAUTHORIZED);
    }

}
