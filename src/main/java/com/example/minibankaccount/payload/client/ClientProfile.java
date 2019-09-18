package com.example.minibankaccount.payload.client;

import com.example.minibankaccount.model.account.Account;
import com.example.minibankaccount.model.user.User;
import com.example.minibankaccount.payload.PagedResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientProfile {
    private User user;
    private PagedResponse<Account> accounts;
}
