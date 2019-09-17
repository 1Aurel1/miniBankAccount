package com.example.minibankaccount.controller;

import com.example.minibankaccount.model.user.User;
import com.example.minibankaccount.payload.account.NewAccountRequest;
import com.example.minibankaccount.payload.account.UpdateAccountRequest;
import com.example.minibankaccount.security.CurrentUser;
import com.example.minibankaccount.security.UserPrincipal;
import com.example.minibankaccount.service.AccountService;
import com.example.minibankaccount.util.AppConstants;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Api("User Accunts")
@RestController
@RequestMapping("/api/accounts")
@PreAuthorize("hasRole('CLIENT')")
public class AccountController {
    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<?> getUserAccounts(
            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size,
            @CurrentUser UserPrincipal currentUser
    ){
        return accountService.getUserAccounts(page, size, currentUser);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<?> getAccount(@PathVariable("accountId") Long accountId, @CurrentUser UserPrincipal currentUser){
        System.out.println("Hereeee");
        return accountService.getAccount(accountId, currentUser);
    }

    @PostMapping
    public ResponseEntity<?> requestAccount(
            @Valid @RequestBody NewAccountRequest newAccountRequest,
            @CurrentUser UserPrincipal currentUser
    ){
        return accountService.addAccount(newAccountRequest, currentUser);
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<?> updateAccount(
            @Valid @RequestBody UpdateAccountRequest updateAccountRequest,
            @CurrentUser UserPrincipal currentUser
    ){
        return accountService.updateAccount(updateAccountRequest, currentUser);
    }

    @PutMapping("/:accountId/close")
    public ResponseEntity<?> closeAccount(
            @PathVariable("accountId") Long accountId,
            @CurrentUser UserPrincipal currentUser
    ){
        return accountService.disableAccount(accountId, currentUser);
    }
}
