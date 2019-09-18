package com.example.minibankaccount.payload.client;

import com.example.minibankaccount.payload.account.AccountSummary;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientSummary {
    private Long id;
    private String name;
    private String username;
    private String email;
    private List<AccountSummary> accountSummaries;
}
