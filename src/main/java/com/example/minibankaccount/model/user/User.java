package com.example.minibankaccount.model.user;

import com.example.minibankaccount.model.account.Account;
import com.example.minibankaccount.model.audit.DateAudit;
import com.example.minibankaccount.model.role.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class User extends DateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank
    @Pattern(regexp = "^([a-zA-Z])*$", message = "Letters only")
    private String firstName;
    @NotBlank
    @Pattern(regexp = "^([a-zA-Z])*$", message = "Letters only")
    private String lastName;
    @NotBlank
    private String username;
    @Email
    @NotBlank
    private String email;
    private boolean confirmedEmail;
    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(max = 100)
    @Column(name = "password")
//    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[#$^+=!*()@%&]).{8,10}$", message = "Password must contain a capital letter, a small letter, a number and a special caracter!")
    private String password;

    private boolean enabled;
    private boolean accountNonExpired;
    private boolean credentialsNonExpired;
    private boolean accountNonLocked;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    @Getter(AccessLevel.NONE)
    private List<Role> roles;

    @OneToOne(mappedBy = "user",cascade = CascadeType.ALL)
    @JsonIgnore
    private UserVerificationToken userVerificationToken;

    @OneToMany(mappedBy = "user",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @Getter(AccessLevel.NONE)
    private List<Account> accounts;

    public User(String firstName, String lastName, String username, String email, String password, boolean enabled, boolean accountNonExpired, boolean accountNonLocked, boolean credentialsNonExpired) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
    }

    @JsonIgnore
    public List<Account> getAccounts() {
        return accounts;
    }

    @JsonIgnore
    public List<Role> getRoles() {
        return roles;
    }
}
