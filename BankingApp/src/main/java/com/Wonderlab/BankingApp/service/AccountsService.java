package com.Wonderlab.BankingApp.service;

import com.Wonderlab.BankingApp.model.Account;
import com.Wonderlab.BankingApp.model.User;
import com.Wonderlab.BankingApp.repository.AccountsRepo;
import com.Wonderlab.BankingApp.repository.UserRepo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class AccountsService {
    private final AccountsRepo accountsRepo;
    private final UserRepo userRepo;

    public AccountsService(AccountsRepo accountsRepo, UserRepo userRepo) {
        this.accountsRepo = accountsRepo;
        this.userRepo = userRepo;
    }
    /* *****************Accounts model services***********/
    public List<Account> getAllAccounts(){
        return accountsRepo.findAll();
    }
    public void addAccount(Account account) {

        int min = 100100;
        int max = 900000000;
        int random = (int) (Math.random() * (max - min + 1) + min);
        Optional<Account> optional = accountsRepo.findByAccountNumber(random);

        while (optional.isPresent()) {
            random = (int) (Math.random() * (max - min + 1) + min);
            optional = accountsRepo.findByAccountNumber(random);
        }
        BigDecimal balance = BigDecimal.valueOf(0);

        if (account.getBalance() != null){
             balance = balance.add(account.getBalance());
             account.setBalance(balance);
        }else{
            account.setBalance(balance);
        }
        account.setAccountNumber(random);
        account.setUser(getCurrentUser());
        accountsRepo.save(account);
    }
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepo.findByUsername(auth.getName());
    }
    public void deleteAccount(Long id){
        accountsRepo.deleteAccountById(id);
    }
    public void findByAccountTypeAndUserId(Model model){
            User currentUser = currentUser();
            String accountType1 = "savings";
            String accountType2 = "current";
            model.addAttribute("listSavingsAccounts", accountsRepo.findByAccountTypeAndUserId(accountType1,currentUser.getId()));
            model.addAttribute("listCurrentAccounts", accountsRepo.findByAccountTypeAndUserId(accountType2,currentUser.getId()));
        }
    public User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepo.findByUsername(auth.getName());
    }


}
