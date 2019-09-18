package com.example.minibankaccount.controller;

import io.swagger.annotations.Api;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api("User")
@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasAnyRole()")
public class UserController {

}
