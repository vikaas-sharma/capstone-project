package com.example.account_service.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.account_service.model.Account;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    List<Account> findByUserId(Integer userId);
}
