//package com.example.minibankaccount.payload.user;
//
//import com.restblogv2.restblog.model.user.Address;
//import com.restblogv2.restblog.model.user.Company;
//import com.restblogv2.restblog.validation.PhoneNumber;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import javax.persistence.Column;
//import javax.validation.constraints.Digits;
//import javax.validation.constraints.Email;
//import javax.validation.constraints.Pattern;
//import javax.validation.constraints.Size;
//import java.math.BigInteger;
//import java.time.Instant;
//
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//public class UserUpdateRequest {
//
//    @Size(max = 15)
//    @Pattern(regexp = "^[a-z0-9_-]{3,15}$", message = "Username is invalid")
//    private String username;
//    @Size(max = 40)
//    @Pattern(regexp = "^[a-zA-Z]+([',. -])*$",message = "Please enter a valid firsname")
//    private String firstName;
//    @Size(max = 40)
//    @Pattern(regexp = "^[a-zA-Z]+([',. -])*$", message = "Please enter a valid surname")
//    private String lastName;
//    @Size(max = 40)
//    @Email
//    private String email;
//    @Size(max = 100)
//    private String password;
//    private Long profilePicture;
//    @PhoneNumber
//    private String phone;
//    @Pattern(regexp = "^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?$")
//    private String website;
//    private Address address;
//    private Company company;
//
//}
