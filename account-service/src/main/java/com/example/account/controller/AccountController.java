package com.example.account.controller;

import com.example.account.dto.AccountRequestDto;
import com.example.account.dto.AccountResponseDto;
import com.example.account.service.AccountService;
import org.springframework.web.bind.annotation.*;

@RestController
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{accountId}")
    public AccountResponseDto getAccount(@PathVariable Long accountId) {
        return new AccountResponseDto(accountService.getAccountById(accountId));
    }

    @PostMapping("/")
    public Long createAccount(@RequestBody AccountRequestDto accountRequestDto) {
        return accountService.createAccount(accountRequestDto.getName(), accountRequestDto.getEmail(),
                accountRequestDto.getPhone(), accountRequestDto.getBills());
    }

    @PutMapping("/{accountId}")
    public AccountResponseDto updateAccount(@PathVariable Long accountId,
                                            @RequestBody AccountRequestDto accountRequestDto) {
        return new AccountResponseDto(
                accountService.updateAccount(
                        accountId,
                        accountRequestDto.getName(),
                        accountRequestDto.getEmail(),
                        accountRequestDto.getPhone(),
                        accountRequestDto.getBills()));
    }

    @DeleteMapping("/{accountId}")
    public AccountResponseDto deleteAccount(@PathVariable Long accountId) {
        return new AccountResponseDto(accountService.deleteAccount(accountId));
    }
}
