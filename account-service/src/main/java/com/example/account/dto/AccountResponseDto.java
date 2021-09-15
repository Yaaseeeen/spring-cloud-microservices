package com.example.account.dto;

import com.example.account.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class AccountResponseDto {

    private Long accountId;

    private String name;

    private String email;

    private List<Long> bills;

    private String phone;

    private OffsetDateTime creationDate;

    public AccountResponseDto(Account account) {
        accountId = account.getAccountId();
        name = account.getName();
        email = account.getEmail();
        phone = account.getPhone();
        bills = account.getBills();
        creationDate = account.getCreationDate();
    }
}
