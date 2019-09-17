package com.example.minibankaccount.repository;

import com.example.minibankaccount.model.account.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Page<Account> findAllByUserId(Long userId, Pageable pageable);
    List<Account> findAllByUserId(Long userId);
    Page<Account> findAllByApprovedIsFalse(Pageable pageable);

}
