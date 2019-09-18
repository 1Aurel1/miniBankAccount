package com.example.minibankaccount.repository;

import com.example.minibankaccount.model.transaction.Transaction;
import com.example.minibankaccount.model.transaction.TransactionState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> getAllByAccount_AccountId(Long accountId, Pageable pageable);
    Page<Transaction> getAllByTransactionState(TransactionState transactionState, Pageable pageable);
}
