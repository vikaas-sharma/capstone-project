package com.example.transaction_service.controller;

import com.example.transaction_service.model.Transaction;
import com.example.transaction_service.service.TransactionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public Transaction addTransaction(@RequestBody Transaction transaction, @RequestHeader("Authorization") String token) {
        return transactionService.addTransaction(transaction, token);
    }

//    @GetMapping("/user/{userId}")
//    public List<Transaction> getTransactionsByUser(@PathVariable Integer userId) {
//        return transactionService.getTransactionsByUser(userId);
//    }

    @GetMapping("/user/{userId}/account/{accountId}")
    public List<Transaction> getTransactionsByUserAndAccount(@PathVariable Integer userId,
                                                             @PathVariable Integer accountId,
                                                             @RequestHeader("Authorization") String token) {
        return transactionService.getTransactionsByUserAndAccount(userId, accountId, token);
    }

//    @PutMapping("/{transactionId}")
//    public Transaction updateTransaction(@PathVariable Integer transactionId,
//                                         @RequestBody Transaction transaction) {
//        return transactionService.updateTransaction(transactionId, transaction);
//    }
}
