package com.example.account_service.service;

import java.util.List;

import com.example.account_service.model.Account;

public interface AccountService {
    Account createAccount(Account account, Integer userId, String token);
    List<Account> getAccountsByUser(Integer userId, String token);
    Account transferMoney(Integer fromAccountId, Integer toAccountId, Double amount, String token);
	Account getAccountById(Integer accountId);
}