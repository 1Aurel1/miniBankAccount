package com.example.minibankaccount.payload.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewAccountRequest {
    private String accountName;
    private int initialBalance;
}
