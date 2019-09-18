package com.example.minibankaccount.payload.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequest {
    @Pattern(regexp = "^([a-zA-Z])*$", message = "Letters only")
    private String firstName;
    @Pattern(regexp = "^([a-zA-Z])*$", message = "Letters only")
    private String lastName;
    @Email
    private String email;

    private String password;
}
