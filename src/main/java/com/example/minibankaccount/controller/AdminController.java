package com.example.minibankaccount.controller;

import com.example.minibankaccount.service.AdminService;
import com.example.minibankaccount.util.AppConstants;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Api("Admin")
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers(
            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size
        ){
        return adminService.getUsers(page, size);
    }

    @PutMapping("/users/{userId}/give/admin")
    public ResponseEntity<?> giveAdmin(@PathVariable("userId") Long userId){
        return adminService.giveAdmin(userId);
    }

    @PutMapping("/users/{userId}/take/admin")
    public ResponseEntity<?> takeAdmin(@PathVariable("userId") Long userId ){
        return adminService.takeAdmin(userId);
    }

    @PutMapping("/users/{userId}/give/manager")
    public ResponseEntity<?> giveManager(@PathVariable("userId") Long userId){
        return adminService.giveManager(userId);
    }

    @PutMapping("/users/{userId}/take/manager")
    public ResponseEntity<?> takeManager(@PathVariable("userId") Long userId){
        return adminService.takeManager(userId);
    }

}
