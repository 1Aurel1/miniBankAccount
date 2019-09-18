package com.example.minibankaccount.controller;

import com.example.minibankaccount.security.CurrentUser;
import com.example.minibankaccount.security.UserPrincipal;
import com.example.minibankaccount.service.ClientService;
import com.example.minibankaccount.util.AppConstants;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

@Api("Client")
@RestController
@RequestMapping("/api/client")
@PreAuthorize("hasRole('CLIENT')")
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentClientProfile(
            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size,
            @CurrentUser UserPrincipal currentUser
    ){
        return clientService.getCurrentClientProfile(page, size, currentUser);
    }

    @GetMapping("/accounts/{}/transactions")
    public ResponseEntity<?> getCurrentClientAccountTransactions(
            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size,
            @PathVariable("accountId") Long accountId,
            @CurrentUser UserPrincipal currentUser
            ){
        return clientService.getTransactions(page, size, accountId, currentUser);
    }


    @GetMapping("/search/clients")
    public ResponseEntity<?> getClientProfile(
            @RequestParam(name = "clientEmail") String email
            ){
        return clientService.getClientProfile(email);
    }

}
