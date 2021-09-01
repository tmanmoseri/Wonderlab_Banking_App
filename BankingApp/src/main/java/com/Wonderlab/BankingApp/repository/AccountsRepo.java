package com.Wonderlab.BankingApp.repository;

import com.Wonderlab.BankingApp.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface AccountsRepo extends JpaRepository<Account, Long> {
    void deleteAccountById(Long id);
    Optional<Account> findByAccountNumber(int accountNumber);
    List<Account> findByAccountType(String accountType);
    List<Account> findByAccountTypeAndUserId(String accountType, Long id);
    Optional<Account> findByAccountNumberAndUserId(int accountNumber, Long id);
    List<Account> findByUserIdAndAccountNumber(Long id,int accountNumber);
    Optional<Account> findByAccountNumberAndAccountType(int accountNumber, String savings);
}
