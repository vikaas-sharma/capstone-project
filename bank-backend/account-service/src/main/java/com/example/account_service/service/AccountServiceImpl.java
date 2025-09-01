package com.example.account_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.account_service.exception.AccountNotFoundException;
import com.example.account_service.exception.InsufficientBalanceException;
import com.example.account_service.exception.UserNotFoundException;
import com.example.account_service.model.Account;
import com.example.account_service.respository.AccountRepository;
import com.example.account_service.util.JwtUtil;

import org.springframework.http.HttpMethod;


import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepo;
    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil; // ✅ Add this

    @Value("${user.service.url}")
    private String userServiceUrl; // http://localhost:1111/api/users/
    
    public AccountServiceImpl(AccountRepository accountRepo, RestTemplate restTemplate, JwtUtil jwtUtil) {
        this.accountRepo = accountRepo;
        this.restTemplate = restTemplate;
        this.jwtUtil =jwtUtil;
    }

//    @Override
//    public Account createAccount(Account account, Integer userId, String token) {
//        // ✅ Add JWT token in headers
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", token);
//
//        HttpEntity<Void> entity = new HttpEntity<>(headers);
//
//        ResponseEntity<Boolean> response = restTemplate.exchange(
//                userServiceUrl + userId + "/exists",
//                HttpMethod.GET,
//                entity,
//                Boolean.class
//        );
//
//        Boolean exists = response.getBody();
//
//        if (exists == null || !exists) {
//            throw new UserNotFoundException("User with id " + userId + " does not exist!");
//        }
//
//        account.setUserId(userId);
//        return accountRepo.save(account);
//    }
    @Override
    public Account createAccount(Account account, Integer userId, String token) {
        System.out.println("=== CREATE ACCOUNT DEBUG ===");
        System.out.println("Token received: " + token);
        System.out.println("UserId from path: " + userId);
        
        try {
            // Extract JWT token and get userId from it
            String jwt = token.replace("Bearer ", "");
            Integer userIdFromToken = jwtUtil.extractUserId(jwt);
            
            System.out.println("UserId from token: " + userIdFromToken);
            System.out.println("UserId from path: " + userId);
            System.out.println("Do they match? " + userIdFromToken.equals(userId));
            
            // ✅ CHECK: User can only create account for themselves
            if (!userIdFromToken.equals(userId)) {
                System.out.println("AUTHORIZATION FAILED: User trying to create account for different user");
                throw new AccessDeniedException("You can only create accounts for yourself!");
            }
            
            // ✅ Add JWT token in headers for User Service call
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Boolean> response = restTemplate.exchange(
                    userServiceUrl + userId + "/exists",
                    HttpMethod.GET,
                    entity,
                    Boolean.class
            );

            Boolean exists = response.getBody();

            if (exists == null || !exists) {
                System.out.println("USER NOT FOUND: " + userId);
                throw new UserNotFoundException("User with id " + userId + " does not exist!");
            }

            account.setUserId(userId);
            Account savedAccount = accountRepo.save(account);
            System.out.println("Account created successfully: " + savedAccount.getAccountId());
            return savedAccount;
            
        } catch (Exception e) {
            System.out.println("Exception in createAccount: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<Account> getAccountsByUser(Integer userId, String token) {
        // Pehle validate kar user exist karta hai ya nahi (User Service call)
    	
//        Boolean userExists = restTemplate.getForObject(userServiceUrl + userId + "/exists", Boolean.class);
//        if (userExists == null || !userExists) {
//            throw new UserNotFoundException("User with id " + userId + " does not exist!");
//        }
    
    	
    	// ✅ Add JWT token in headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Boolean> response = restTemplate.exchange(
                userServiceUrl + userId + "/exists",
                HttpMethod.GET,
                entity,
                Boolean.class
        );

        Boolean exists = response.getBody();

        if (exists == null || !exists) {
            throw new UserNotFoundException("User with id " + userId + " does not exist!");
        }

        List<Account> accounts = accountRepo.findByUserId(userId);
        if (accounts.isEmpty()) {
            throw new AccountNotFoundException("No accounts found for user id: " + userId);
        }
        return accounts;
    }


//    @Override
//    public Account transferMoney(Integer fromAccountId, Integer toAccountId, Double amount, String token) {
//        String jwt = token.replace("Bearer ", "");
//        Integer userIdFromToken = jwtUtil.extractUserId(jwt);
//        Account from = accountRepo.findById(fromAccountId)
//                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + fromAccountId));
//
//        // ✅ check if this account belongs to the logged-in user
//        if (!from.getUserId().equals(userIdFromToken)) {
//            throw new AccessDeniedException("You are not authorized to transfer from this account!");
//        }
//
//        Account to = accountRepo.findById(toAccountId)
//                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + toAccountId));
//
//        if (from.getAccountBalance() < amount) {
//            throw new InsufficientBalanceException("Insufficient balance!");
//        }
//
//        from.setAccountBalance(from.getAccountBalance() - amount);
//        to.setAccountBalance(to.getAccountBalance() + amount);
//
//        accountRepo.save(from);
//        return accountRepo.save(to);
//    }
    
    @Override
    public Account transferMoney(Integer fromAccountId, Integer toAccountId, Double amount, String token) {
        System.out.println("=== TRANSFER DEBUG ===");
        System.out.println("Token received: " + token);
        
        String jwt = token.replace("Bearer ", "");
        System.out.println("JWT after Bearer removal: " + jwt);
        
        try {
            Integer userIdFromToken = jwtUtil.extractUserId(jwt);
            System.out.println("UserId extracted from token: " + userIdFromToken);
            
            Account from = accountRepo.findById(fromAccountId)
                    .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + fromAccountId));
            
            System.out.println("From account userId: " + from.getUserId());
            System.out.println("Token userId: " + userIdFromToken);
            System.out.println("Are they equal? " + from.getUserId().equals(userIdFromToken));
            
            // ✅ check if this account belongs to the logged-in user
            if (!from.getUserId().equals(userIdFromToken)) {
                System.out.println("AUTHORIZATION FAILED!");
                throw new AccessDeniedException("You are not authorized to transfer from this account!");
            }
            
            Account to = accountRepo.findById(toAccountId)
                    .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + toAccountId));

            if (from.getAccountBalance() < amount) {
                throw new InsufficientBalanceException("Insufficient balance!");
            }

            from.setAccountBalance(from.getAccountBalance() - amount);
            to.setAccountBalance(to.getAccountBalance() + amount);

            accountRepo.save(from);
            return accountRepo.save(to);
            
        } catch (Exception e) {
            System.out.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }


    
    @Override
    public Account getAccountById(Integer accountId) {
        return accountRepo.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + accountId));
    }

}

