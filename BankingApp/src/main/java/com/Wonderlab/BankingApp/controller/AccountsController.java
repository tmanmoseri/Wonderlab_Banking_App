package com.Wonderlab.BankingApp.controller;


import com.Wonderlab.BankingApp.model.Account;
import com.Wonderlab.BankingApp.service.AccountsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller

public class AccountsController {
    private final AccountsService accountsService;

    public AccountsController(AccountsService accountsService) {
        this.accountsService = accountsService;

    }

    @PostMapping("/saveAccount")
    public String saveAccount(@ModelAttribute("account") Account account){
        //save new account to DB
        accountsService.addAccount(account);
        return "redirect:/home";
    }








}
