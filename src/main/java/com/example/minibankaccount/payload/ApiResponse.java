package com.example.minibankaccount.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApiResponse {

    private String type;
    private int status;
    private String title;
    private String message;

}
