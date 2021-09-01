package com.Wonderlab.BankingApp.controller;

import com.Wonderlab.BankingApp.model.Account;
import com.Wonderlab.BankingApp.model.Payment;
import com.Wonderlab.BankingApp.model.ReceiverInvoice;
import com.Wonderlab.BankingApp.model.User;
import com.Wonderlab.BankingApp.repository.*;
import com.Wonderlab.BankingApp.service.AccountsService;
import com.Wonderlab.BankingApp.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class PaymentsController {


    @Autowired
    private PaymentRepo paymentRepo;
    @Autowired
    private AccountsRepo accountsRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PaymentsController paymentsController;
    @Autowired
    private ReceiveInvoiceRepo receiveInvoiceRepo;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private AccountsService accountsService;

    @PostMapping("/makeDeposit")
    public String deposit(@ModelAttribute("payment")Payment payment,@ModelAttribute("account")Account account, ReceiverInvoice receiverInvoice){

        //save new account to DB
        paymentService.Deposit(payment, receiverInvoice);
        return "redirect:/home";
    }
    @PostMapping("/makeWithdrawal")
    public String withdrawal(@ModelAttribute("payment")Payment payment,@ModelAttribute("account")Account account){

        //save new account to DB
        paymentService.Withdrawal(payment);
        return "redirect:/home";
    }
    @PostMapping("/makeTranfer")
    public String tranfer(@ModelAttribute("payment")Payment payment, @ModelAttribute("account")Account account, ReceiverInvoice receiverInvoice){

        //save new account to DB
        paymentService.transfer(payment,receiverInvoice);
        return "redirect:/home";
    }
    @GetMapping("/results")
    public String showResults(Model model){
        model.addAttribute("listPayments", paymentService.getAllPayments());
        String transactionType1 ="deposit";
        String transactionType2 ="withdrawal";
        String transactionType3 ="transfer";

        User currentUser = currentUser();
        Long currentUserId = currentUser.getId();

        model.addAttribute("listPayments1", receiveInvoiceRepo.findByTransactionTypeAndUserId(transactionType1,currentUserId));
        model.addAttribute("listPayments2", paymentRepo.findByUserIdAndTransactionType(currentUserId,transactionType2));
        model.addAttribute("listPayments3", paymentRepo.findByUserIdAndTransactionType(currentUserId,transactionType3));
        return "results";
    }
    @GetMapping("/home")
    public String home(Model model){

        accountsService.findByAccountTypeAndUserId(model);
        return "home";
    }

    public User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepo.findByUsername(auth.getName());
    }
}
