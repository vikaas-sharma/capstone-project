package com.example.transaction_service.service;

import com.example.transaction_service.model.Transaction;
import com.example.transaction_service.repository.TransactionRepository;
import com.example.transaction_service.dto.AccountDTO;
import com.example.transaction_service.exception.AccountNotFoundException;
import com.example.transaction_service.exception.TransactionNotFoundException;
import com.example.transaction_service.exception.UserNotFoundException;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepo;
    private final RestTemplate restTemplate;

 // Base URLs for other services
    private final String USER_SERVICE_URL = "http://localhost:1111/api/users/";  
    private final String ACCOUNT_SERVICE_URL = "http://localhost:2222/api/accounts/";  

    public TransactionServiceImpl(TransactionRepository transactionRepo, RestTemplate restTemplate) {
        this.transactionRepo = transactionRepo;
        this.restTemplate = restTemplate;
    }

    // ✅ Helper: Build headers with JWT
    private HttpEntity<Void> buildAuthEntity(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token); // Bearer <token>
        return new HttpEntity<>(headers);
    }
    
    @Override
    public Transaction addTransaction(Transaction transaction, String token) {
        // ✅ Validate User
        ResponseEntity<Boolean> userResponse = restTemplate.exchange(
                USER_SERVICE_URL + transaction.getUserId() + "/exists",
                org.springframework.http.HttpMethod.GET,
                buildAuthEntity(token),
                Boolean.class
        );

        if (userResponse.getBody() == null || !userResponse.getBody()) {
            throw new UserNotFoundException("User with id " + transaction.getUserId() + " does not exist!");
        }

        // ✅ Validate From Account
        ResponseEntity<AccountDTO> fromResponse = restTemplate.exchange(
                ACCOUNT_SERVICE_URL + transaction.getFrmAccountId(),
                org.springframework.http.HttpMethod.GET,
                buildAuthEntity(token),
                AccountDTO.class
        );

        if (fromResponse.getBody() == null) {
            throw new AccountNotFoundException("From Account with id " + transaction.getFrmAccountId() + " does not exist!");
        }

        // ✅ Validate To Account (TRANSFER case)
        if ("TRANSFER".equalsIgnoreCase(transaction.getTransactionType())
                && transaction.getToAccountId() != null) {

            ResponseEntity<AccountDTO> toResponse = restTemplate.exchange(
                    ACCOUNT_SERVICE_URL + transaction.getToAccountId(),
                    org.springframework.http.HttpMethod.GET,
                    buildAuthEntity(token),
                    AccountDTO.class
            );

            if (toResponse.getBody() == null) {
                throw new AccountNotFoundException("To Account with id " + transaction.getToAccountId() + " does not exist!");
            }

            // ✅ Call AccountService to transfer money
            String transferUrl = ACCOUNT_SERVICE_URL + "transfer/"
                    + transaction.getFrmAccountId() + "/"
                    + transaction.getToAccountId() + "/"
                    + transaction.getAmount();

            ResponseEntity<String> transferResponse = restTemplate.exchange(
                    transferUrl,
                    org.springframework.http.HttpMethod.POST,
                    buildAuthEntity(token),
                    String.class
            );

            if (transferResponse.getBody() == null || !transferResponse.getBody().contains("successful")) {
                throw new RuntimeException("AccountService transfer failed!");
            }
        }

        // ✅ Save transaction
        return transactionRepo.save(transaction);
    }



 

    @Override
    public List<Transaction> getTransactionsByUserAndAccount(Integer userId, Integer accountId, String token) {

    	 // 1. Validate User
        ResponseEntity<Boolean> userResponse = restTemplate.exchange(
                USER_SERVICE_URL + userId + "/exists",
                org.springframework.http.HttpMethod.GET,
                buildAuthEntity(token),
                Boolean.class
        );
        if (userResponse.getBody() == null || !userResponse.getBody()) {
            throw new UserNotFoundException("User with id " + userId + " does not exist!");
        }

        // 2. Validate Account
        ResponseEntity<AccountDTO> accountResponse = restTemplate.exchange(
                ACCOUNT_SERVICE_URL + accountId,
                org.springframework.http.HttpMethod.GET,
                buildAuthEntity(token),
                AccountDTO.class
        );
        if (accountResponse.getBody() == null) {
            throw new AccountNotFoundException("Account with id " + accountId + " does not exist!");
        }

        // 3. Fetch transactions
        List<Transaction> transactions = transactionRepo.findByUserIdAndFrmAccountId(userId, accountId);
        if (transactions.isEmpty()) {
            throw new TransactionNotFoundException(
                    "No transactions found for userId " + userId + " and accountId " + accountId);
        }

        return transactions;
    }

//    @Override
//    public Transaction updateTransaction(Integer transactionId, Transaction transaction) {
//        Transaction existing = transactionRepo.findById(transactionId)
//                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + transactionId));
//
//        existing.setTransactionType(transaction.getTransactionType());
//        existing.setAmount(transaction.getAmount());
//        existing.setToAccountId(transaction.getToAccountId());
//        existing.setFrmAccountId(transaction.getFrmAccountId());
//        existing.setUserId(transaction.getUserId());
//
//        return transactionRepo.save(existing);
//    }

//	@Override
//	public List<Transaction> getTransactionsByUser(Integer userId) {
//		// TODO Auto-generated method stub
//		return null;
//	}
}

