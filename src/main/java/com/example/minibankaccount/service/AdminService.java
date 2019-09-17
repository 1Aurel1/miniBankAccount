package com.example.minibankaccount.service;

import com.example.minibankaccount.exeption.AppException;
import com.example.minibankaccount.exeption.BadRequestException;
import com.example.minibankaccount.exeption.ResourceNotFoundException;
import com.example.minibankaccount.model.account.Account;
import com.example.minibankaccount.model.role.Role;
import com.example.minibankaccount.model.role.RoleName;
import com.example.minibankaccount.model.user.User;
import com.example.minibankaccount.payload.ApiResponse;
import com.example.minibankaccount.payload.PagedResponse;
import com.example.minibankaccount.repository.RoleRepository;
import com.example.minibankaccount.repository.UserRepository;
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
import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public AdminService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public ResponseEntity<?> getUsers(int page, int size){
        validatePageNumberAndSize(page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<User> users = userRepository.findAll(pageable);

        return getResponseEntity(users);
    }

    public ResponseEntity<?> giveAdmin(Long userId){
        User user = userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User", "id", userId));
        List<Role> roles = user.getRoles();
        if (roles.contains(roleRepository.findByName(RoleName.ROLE_ADMIN).orElseThrow(() -> new AppException("User role not set"))))
            return new ResponseEntity<>(new ApiResponse("error", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), "User already has role ADMIN"), HttpStatus.BAD_REQUEST);
        roles.add(roleRepository.findByName(RoleName.ROLE_ADMIN).orElseThrow(() -> new AppException("User role not set")));
        user.setRoles(roles);
        user = userRepository.save(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
    public ResponseEntity<?> takeAdmin(Long userId){
        User user = userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User", "id", userId));
        List<Role> roles = user.getRoles();
        if (!roles.contains(roleRepository.findByName(RoleName.ROLE_ADMIN).orElseThrow(() -> new AppException("User role not set"))))
            return new ResponseEntity<>(new ApiResponse("error", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), "User doesnt has role ADMIN"), HttpStatus.BAD_REQUEST);
        roles.remove(roleRepository.findByName(RoleName.ROLE_ADMIN).orElseThrow(() -> new AppException("User role not set")));
        user.setRoles(roles);
        user = userRepository.save(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
    public ResponseEntity<?> giveManager(Long userId){
        User user = userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User", "id", userId));
        List<Role> roles = user.getRoles();
        if (roles.contains(roleRepository.findByName(RoleName.ROLE_MANAGER).orElseThrow(() -> new AppException("User role not set"))))
            return new ResponseEntity<>(new ApiResponse("error", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), "User already has role MANAGER"), HttpStatus.BAD_REQUEST);
        roles.add(roleRepository.findByName(RoleName.ROLE_MANAGER).orElseThrow(() -> new AppException("User role not set")));
        user.setRoles(roles);
        user = userRepository.save(user);
        return new ResponseEntity<>(user, HttpStatus.OK);

    }
    public ResponseEntity<?> takeManager(Long userId){
        User user = userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User", "id", userId));
        List<Role> roles = user.getRoles();
        if (!roles.contains(roleRepository.findByName(RoleName.ROLE_MANAGER).orElseThrow(() -> new AppException("User role not set"))))
            return new ResponseEntity<>(new ApiResponse("error", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), "User doesnt has role MANAGER"), HttpStatus.BAD_REQUEST);
        roles.remove(roleRepository.findByName(RoleName.ROLE_MANAGER).orElseThrow(() -> new AppException("User role not set")));
        user.setRoles(roles);
        user = userRepository.save(user);
        return new ResponseEntity<>(user, HttpStatus.OK);

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
