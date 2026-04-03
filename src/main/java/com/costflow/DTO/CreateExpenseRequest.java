package com.costflow.DTO;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateExpenseRequest {
    String itemName;
    Double amount;
}
