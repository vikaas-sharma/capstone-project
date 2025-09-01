package com.example.transaction_service.service;

import com.example.transaction_service.model.Transaction;
import java.util.List;

public interface TransactionService {
    Transaction addTransaction(Transaction transaction, String token);
//    List<Transaction> getTransactionsByUser(Integer userId);
    List<Transaction> getTransactionsByUserAndAccount(Integer userId, Integer accountId, String token);
//    Transaction updateTransaction(Integer transactionId, Transaction transaction);
}

