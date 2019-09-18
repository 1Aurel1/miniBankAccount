package com.example.minibankaccount.controller;

import com.example.minibankaccount.service.ManagerService;
import com.example.minibankaccount.util.AppConstants;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Api("Manager")
@RestController
@RequestMapping("/api/manager")
@PreAuthorize("hasRole('MANAGER')")
public class ManagerController {

    private final ManagerService managerService;

    @Autowired
    public ManagerController(ManagerService managerService) {
        this.managerService = managerService;
    }


    @GetMapping("/undecided/transactions")
    public ResponseEntity<?> getUnConfirmedTransactions(
            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size
            ){
        return managerService.getUnDecidedTransactions(page, size);
    }

    @GetMapping("/rejected/transactions")
    public ResponseEntity<?> getRejectedTransactions(
            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size
    ){
        return managerService.getRejectedTransactions(page, size);
    }

    @GetMapping("/approved/transactions")
    public ResponseEntity<?> getApprovedTransactions(
            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size
            ){
        return managerService.getApprovedTransactions(page, size);
    }

    @PutMapping("/approve/transaction/{transactionId}")
    public ResponseEntity<?> confirmTransaction(
            @PathVariable("transactionId") Long transactionId
            ){
        return managerService.approveTransaction(transactionId);
    }

    @PutMapping("/reject/transaction/{transactionId}")
    public ResponseEntity<?> rejectTransaction(
            @PathVariable("transactionId") Long transactionId
    ){
        return managerService.rejectTransaction(transactionId);
    }



    @GetMapping("/undecided/accounts")
    public ResponseEntity<?> getUnApprovedAccounts(
            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size
            ) {
        return managerService.getUnAuthorizedAccounts(page, size);
    }

    @PutMapping("/approve/account/{accountId}")
    public ResponseEntity<?> approveAccount(
            @PathVariable("accountId") Long accountId){
        return managerService.approveAccount(accountId);
    }

    @PutMapping("disable/account/{accountId}")
    public ResponseEntity<?> disableAccount(
            @PathVariable("accountId") Long accountId
            ){
        return managerService.disableAccount(accountId);
    }

    @PutMapping("enable/account/{accountId}")
    public ResponseEntity<?> enableAccount(
            @PathVariable("accountId") Long accountId
            ){
        return managerService.enableAccount(accountId);
    }

    @DeleteMapping("delete/account/{accountId}")
    public ResponseEntity<?> deleteAccount(@PathVariable("accountId") Long accountId){
        return managerService.deleteAccount(accountId);
    }
}
