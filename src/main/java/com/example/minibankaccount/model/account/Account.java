package com.example.minibankaccount.model.account;

import com.example.minibankaccount.model.audit.DateAudit;
import com.example.minibankaccount.model.transaction.Transaction;
import com.example.minibankaccount.model.transaction.Transfer;
import com.example.minibankaccount.model.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Account extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    private String accountName;

    private int currentBalance;

    private boolean approved;

    private boolean enabled;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "user_id")
    @Getter(AccessLevel.NONE)
    private User user;



    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    @Getter(AccessLevel.NONE)
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "receiver")
    @Getter(AccessLevel.NONE)
    private List<Transfer> received;

    @JsonIgnore
    public User getUser() {
        return user;
    }

    @JsonIgnore
    public List<Transaction> getTransactions() {
        return transactions;
    }

    @JsonIgnore
    public List<Transfer> getReceived() {
        return received;
    }

}
