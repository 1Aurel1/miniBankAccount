package com.example.minibankaccount.payload.transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewTransactionRequest {
    private String description;
    @Min(value = 5, message = "Minimal amount is 5")
    private int amount;
    private byte transactionType;
    private Long accountId;
    private Long receiverId;
}
