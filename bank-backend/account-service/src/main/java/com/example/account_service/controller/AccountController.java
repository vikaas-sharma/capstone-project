package com.example.account_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.account_service.exception.AccountNotFoundException;
import com.example.account_service.exception.InsufficientBalanceException;
import com.example.account_service.model.Account;
import com.example.account_service.service.AccountService;
import com.example.user_service.exception.UserNotFoundException;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

//    @PostMapping("/{userId}")
//    public Account createAccount(
//            @RequestHeader("Authorization") String token,
//            @RequestBody Account account,
//            @PathVariable Integer userId) {
//        return accountService.createAccount(account, userId, token);
//    }
    
    @PostMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> createAccount(
            @RequestHeader("Authorization") String token,
            @RequestBody Account account,
            @PathVariable Integer userId) {
        
        try {
            Account createdAccount = accountService.createAccount(account, userId, token);
            
            // Return success response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Account created successfully!");
            response.put("account", createdAccount);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());

            if (e instanceof AccessDeniedException) {
                return ResponseEntity.status(403).body(errorResponse);
            } else if (e instanceof UserNotFoundException) {
                return ResponseEntity.status(404).body(errorResponse);
            } else {
                return ResponseEntity.status(500).body(errorResponse);
            }
        }
    }
    @GetMapping("/{accountId}")
    public Account getAccountById(@PathVariable Integer accountId) {
        return accountService.getAccountById(accountId);
    }


    @GetMapping("/user/{userId}")
    public List<Account> getAccountsByUser(@PathVariable Integer userId, @RequestHeader("Authorization") String token ) {
        return accountService.getAccountsByUser(userId, token);
    }

    @PostMapping("/transfer/{fromId}/{toId}/{amount}")
    public ResponseEntity<Map<String, Object>> transfer(
            @PathVariable Integer fromId,
            @PathVariable Integer toId,
            @PathVariable Double amount,
            @RequestHeader("Authorization") String token) {
        
        try {
            Account updatedAccount = accountService.transferMoney(fromId, toId, amount, token);
            
            // Return JSON response instead of plain string
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Transfer successful!");
            response.put("fromAccountId", fromId);
            response.put("toAccountId", toId);
            response.put("amount", amount);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            if (e instanceof AccessDeniedException) {
                return ResponseEntity.status(403).body(errorResponse);
            } else if (e instanceof AccountNotFoundException || 
                       e instanceof InsufficientBalanceException) {
                return ResponseEntity.badRequest().body(errorResponse);
            } else {
                return ResponseEntity.status(500).body(errorResponse);
            }
        }
    }

}

