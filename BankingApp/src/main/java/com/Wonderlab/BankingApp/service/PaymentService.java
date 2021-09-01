package com.Wonderlab.BankingApp.service;

import com.Wonderlab.BankingApp.controller.PaymentsController;
import com.Wonderlab.BankingApp.model.*;
import com.Wonderlab.BankingApp.repository.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PaymentService {
    @Autowired
    private final PaymentRepo paymentRepo;
    @Autowired
    private final AccountsRepo accountsRepo;
    @Autowired
    private final UserRepo userRepo;
    @Autowired
    private final ReceiveInvoiceRepo receiveInvoiceRepo;

    public PaymentService(PaymentRepo paymentRepo, AccountsRepo accountsRepo, UserRepo userRepo, ReceiveInvoiceRepo receiveInvoiceRepo) {
        this.paymentRepo = paymentRepo;
        this.accountsRepo = accountsRepo;
        this.userRepo = userRepo;
        this.receiveInvoiceRepo = receiveInvoiceRepo;
    }
    public void Deposit(Payment payment, ReceiverInvoice receiverInvoice) {
        User currentUser = currentUser();
        Long user_id = currentUser.getId();

        String depositTransaction = "deposit";
        String successTransactionStatus = "Successful";

        //create ref no
        int ref;
        int min = 10000;
        int max = 999999999;
        int random = (int) (Math.random() * (max - min + 1) + min);
        Optional<Payment> paymentRef = paymentRepo.findByRef(random);
        while (paymentRef.isPresent()) {
            random = (int) (Math.random() * (max - min + 1) + min);
            paymentRef = paymentRepo.findByRef(random);
        }
        ref = random;

        int AccountNumber = payment.getAccountNumber();
        BigDecimal amountToPay =  payment.getAmount();
        Optional<Account> optional = accountsRepo.findByAccountNumber(AccountNumber);

        optional.ifPresent(account -> {
            BigDecimal ReceiverBalance = account.getBalance();
            ReceiverBalance = ReceiverBalance.add(amountToPay);
            account.setBalance(ReceiverBalance);
            accountsRepo.save(account);

            receiverInvoice.setUserId(user_id);
            receiverInvoice.setAccountNumber(AccountNumber);
            receiverInvoice.setBeneficiaryName("Online");
            receiverInvoice.setAmount(amountToPay);
            receiverInvoice.setTransactionType(depositTransaction);
            receiverInvoice.setRef(ref);
            receiverInvoice.setStatus(successTransactionStatus);

            receiveInvoiceRepo.save(receiverInvoice);
        });
    }
    public void Withdrawal(Payment payment) {
        User currentUser = currentUser();
        Long user_id = currentUser.getId();
        int AccountNumber = payment.getAccountNumber();
        BigDecimal amountToWithdraw =  payment.getAmount();

        //create ref no
        int ref;
        int min = 10000;
        int max = 999999999;
        int random = (int) (Math.random() * (max - min + 1) + min);
        Optional<Payment> paymentRef = paymentRepo.findByRef(random);
        while (paymentRef.isPresent()) {
            random = (int) (Math.random() * (max - min + 1) + min);
            paymentRef = paymentRepo.findByRef(random);
        }
        ref = random;

        Optional<Account> optional = accountsRepo.findByAccountNumber(AccountNumber);
        optional.ifPresent(account -> {

            BigDecimal WithdrawerBalance = account.getBalance();
             
                Optional<Account> savings = accountsRepo.findByAccountNumberAndAccountType(AccountNumber,"savings");
                if (savings.isPresent()){ //perfom withdrawal for savings account

                    BigDecimal newsenderBalance = BigDecimal.valueOf(0);
                    newsenderBalance = WithdrawerBalance.subtract(amountToWithdraw);
                    BigDecimal withdrawerActiveBalance =  WithdrawerBalance.subtract(BigDecimal.valueOf(1000));


                    WithdrawerBalance = WithdrawerBalance.subtract(amountToWithdraw);
                    int savingsAccountLimit =  BigDecimal.valueOf(1000).compareTo(WithdrawerBalance);
                    if (savingsAccountLimit <= 0){
                        account.setBalance(WithdrawerBalance);
                        accountsRepo.save(account);

                        String statusmessage="Your withdrawal transaction is Successful";
                        payment.setStatus(statusmessage);
                        payment.setUserId(user_id);
                        payment.setAccountNumber(AccountNumber);
                        payment.setAmount(amountToWithdraw);

                        paymentRepo.save(payment);
                    } else throw new RuntimeException("Failed, Reason: Insufficient Funds - your balance should not exceed -R1000.00.\n" +
                            " Your active Withdrawal amount is R"+withdrawerActiveBalance);
             }
                else {
                    Optional<Account> current = accountsRepo.findByAccountNumberAndAccountType(AccountNumber, "current");
                    if (current.isPresent()) { //perfom withdrawal for savings account

                        BigDecimal newsenderBalance = BigDecimal.valueOf(0);
                        newsenderBalance = WithdrawerBalance.subtract(amountToWithdraw);
                        BigDecimal withdrawerActiveBalance =  WithdrawerBalance.subtract(BigDecimal.valueOf(-100000));


                        WithdrawerBalance = WithdrawerBalance.subtract(amountToWithdraw);
                        int savingsAccountLimit = BigDecimal.valueOf(-100000).compareTo(WithdrawerBalance);
                        if (savingsAccountLimit <= 0) {
                            account.setBalance(WithdrawerBalance);
                            accountsRepo.save(account);
                            String statusmessage = "Your withdrawal transaction is Successful";
                            payment.setStatus(statusmessage);
                            payment.setUserId(user_id);
                            payment.setAccountNumber(AccountNumber);
                            payment.setAmount(amountToWithdraw);

                            paymentRepo.save(payment);

                        } else
                            throw new RuntimeException("Failed, Reason: Insufficient Funds - your balance should not exceed -R100 000.00.\n" +
                                    " Your active Withdrawal amount is R"+withdrawerActiveBalance);

                    }
                }
        });
    }
    public void transfer(Payment payment, ReceiverInvoice receiverInvoice) {
        User currentUser = currentUser();
        Long senderUserId = currentUser.getId();
        int SenderAccountNumber = payment.getAccountNumber();
        int ReceiverAccountNumber = payment.getBeneficiaryAccount();
        BigDecimal amountToTransfer = payment.getAmount();
        BigDecimal amountToReceive = payment.getAmount();
        String depositTransaction = "deposit";
        String transferTransaction = "transfer";
        String successTransactionStatus = "Your transaction is successful!!";
        String failedTransactionStatusDueToAccount ="Failed. Beneficiary account number does not exist.!! ";

        //create ref no
        int ref;
        int min = 10000;
        int max = 999999999;
        int random = (int) (Math.random() * (max - min + 1) + min);
        Optional<Payment> paymentRef = paymentRepo.findByRef(random);
        //find payment by ref
        while (paymentRef.isPresent()) {
            random = (int) (Math.random() * (max - min + 1) + min);
            paymentRef = paymentRepo.findByRef(random);
        }
        ref = random;
        Optional<Account> receiver = accountsRepo.findByAccountNumber(ReceiverAccountNumber);
        Optional<Account> Sender = accountsRepo.findByAccountNumber(SenderAccountNumber);

        //unallowed self transaction
        if (SenderAccountNumber == ReceiverAccountNumber){
            throw new RuntimeException("Failed, Reason: Account Number error - And account can not transfer funds to it's self");

        }
        else{
            //Receiver account not found
            if (receiver.isPresent() == false){
                    payment.setAccountNumber(SenderAccountNumber);
                    payment.setAmount(BigDecimal.valueOf(0));
                    payment.setRef(ref);
                    payment.setTransactionType(transferTransaction);
                    payment.setStatus(failedTransactionStatusDueToAccount);
                    payment.setUserId(senderUserId);
                    /*
                     * generate sender invoice
                     * */
                    paymentRepo.save(payment);
                    int hn = 1;if (hn <1) {}else throw  new RuntimeException(failedTransactionStatusDueToAccount);
                }
                //Receiver is present
                else{
                //get sender account details by account number
                Sender.ifPresent(account -> {
                    BigDecimal senderBalance = account.getBalance();
                    //if receiver Account number exist, do transactions
                    receiver.ifPresent(account1 -> {
                        Long receiverUserId = account1.getUser().getId();

                        List<Account> theSender = accountsRepo.findByUserIdAndAccountNumber(currentUser.getId(), SenderAccountNumber);

                        //Two User transaction
                        if (!Objects.equals(senderUserId, receiverUserId)) {System.out.println("\nThis is not a self transfer, both users should receiver an invoice");
                            Optional<Account> savings = accountsRepo.findByAccountNumberAndAccountType(SenderAccountNumber, "savings");
                            //savings account
                            if (savings.isPresent()) {
                                //perfom withdrawal for savings account
                                BigDecimal newsenderBalance = BigDecimal.valueOf(0);
                                newsenderBalance = senderBalance.subtract(amountToTransfer);
                                BigDecimal SenderActiveBalance =  senderBalance.subtract(BigDecimal.valueOf(1000));
                                int savingsAccountLimit = BigDecimal.valueOf(1000).compareTo(newsenderBalance);


                                if (savingsAccountLimit <= 0) {

                                    //send payment to beneficiary account
                                    account.setBalance(newsenderBalance);
                                    accountsRepo.save(account);


                                    BigDecimal receiverBalance = account1.getBalance();
                                    receiverBalance = receiverBalance.add(amountToTransfer);
                                    account1.setBalance(receiverBalance);
                                    //Beneficiary account receives payment
                                    accountsRepo.save(account1);

                                    payment.setAccountNumber(SenderAccountNumber);
                                    payment.setBeneficiaryName(String.valueOf(ReceiverAccountNumber));
                                    payment.setAmount(amountToTransfer);
                                    payment.setRef(ref);
                                    payment.setTransactionType(transferTransaction);
                                    payment.setStatus(successTransactionStatus);
                                    payment.setUserId(senderUserId);

                                    //generate sender invoice
                                    paymentRepo.save(payment);

                                    receiverInvoice.setUserId(receiverUserId);
                                    receiverInvoice.setAccountNumber(ReceiverAccountNumber);
                                    receiverInvoice.setSenderName(String.valueOf(SenderAccountNumber));
                                    receiverInvoice.setAmount(amountToReceive);
                                    receiverInvoice.setTransactionType(depositTransaction);
                                    receiverInvoice.setRef(ref);
                                    receiverInvoice.setStatus(successTransactionStatus);

                                    receiveInvoiceRepo.save(receiverInvoice);
                                }
                                else { int k = 1;if (k > 1) {} else throw new RuntimeException("Failed, Reason: Insufficient Funds - your balance should not remain below R1000.00. your Active balance is "+SenderActiveBalance);}
                            }//end of savings transactions

                            //current account
                            else {
                                Optional<Account> current = accountsRepo.findByAccountNumberAndAccountType(SenderAccountNumber, "current");
                                if (current.isPresent()) { //perfom withdrawal for current account

                                    BigDecimal newsenderBalance = BigDecimal.valueOf(0);
                                    newsenderBalance = senderBalance.subtract(amountToTransfer);
                                    BigDecimal SenderActiveBalance =  senderBalance.subtract(BigDecimal.valueOf(-100000));
                                    int currentAccountLimit = BigDecimal.valueOf(-100000).compareTo(newsenderBalance);

                                    if (currentAccountLimit <= 0) {//pass sender attributes to generate Sender invoice


                                        //send payment to beneficiary account
                                        account.setBalance(newsenderBalance);
                                        accountsRepo.save(account);


                                        BigDecimal receiverBalance = account1.getBalance();
                                        receiverBalance = receiverBalance.add(amountToTransfer);
                                        account1.setBalance(receiverBalance);
                                        //Beneficiary account receives payment
                                        accountsRepo.save(account1);

                                        payment.setAccountNumber(SenderAccountNumber);
                                        payment.setBeneficiaryName(String.valueOf(ReceiverAccountNumber));
                                        payment.setAmount(amountToTransfer);
                                        payment.setRef(ref);
                                        payment.setTransactionType(transferTransaction);
                                        payment.setStatus(successTransactionStatus);
                                        payment.setUserId(senderUserId);

                                        //generate sender invoice
                                        paymentRepo.save(payment);

                                        receiverInvoice.setUserId(receiverUserId);
                                        receiverInvoice.setAccountNumber(ReceiverAccountNumber);
                                        receiverInvoice.setSenderName(String.valueOf(SenderAccountNumber));
                                        receiverInvoice.setAmount(amountToReceive);
                                        receiverInvoice.setTransactionType(depositTransaction);
                                        receiverInvoice.setRef(ref);
                                        receiverInvoice.setStatus(successTransactionStatus);

                                        receiveInvoiceRepo.save(receiverInvoice);}
                                    else {int k = 1;if (k > 1){} else throw new RuntimeException("Failed, Reason: Insufficient Funds - your balance should not exceed -R100 000.00. your available balance is "+SenderActiveBalance);}
                                }
                            }//end of current transactions

                        }// end of two user transactions

                        //self transaction
                        else{
                            Optional<Account> savings = accountsRepo.findByAccountNumberAndAccountType(SenderAccountNumber, "savings");
                            //: savings transactions
                            if (savings.isPresent()) {System.out.println("\nThis is a self transfer, only sender users should receiver an invoice");

                                //perfom withdrawal for savings account
                                BigDecimal newsenderBalance = BigDecimal.valueOf(0);
                                newsenderBalance = senderBalance.subtract(amountToTransfer);
                                BigDecimal SenderActiveBalance =  senderBalance.subtract(BigDecimal.valueOf(1000));

                                int savingsAccountLimit = BigDecimal.valueOf(1000).compareTo(newsenderBalance);
                                if (savingsAccountLimit <= 0) {//sender haenought to transfer


                                    //send payment to beneficiary account
                                    account.setBalance(newsenderBalance);
                                    accountsRepo.save(account);


                                    BigDecimal receiverBalance = account1.getBalance();
                                    receiverBalance = receiverBalance.add(amountToTransfer);
                                    account1.setBalance(receiverBalance);
                                    //Beneficiary account receives payment
                                    accountsRepo.save(account1);

                                    payment.setAccountNumber(SenderAccountNumber);
                                    payment.setBeneficiaryName(String.valueOf(ReceiverAccountNumber));
                                    payment.setAmount(amountToTransfer);
                                    payment.setRef(ref);
                                    payment.setTransactionType(transferTransaction);
                                    payment.setStatus(successTransactionStatus);
                                    payment.setUserId(senderUserId);

                                    //generate sender invoice
                                    paymentRepo.save(payment);
                                }
                                else {int k = 1;if (k > 1) {} else throw new RuntimeException("Failed, Reason: Insufficient Funds - your balance should not exceed -R1000.00. Your active balance is R"+SenderActiveBalance);}
                            }//end of savings transactions
                            //: current transactions
                            else {
                                Optional<Account> current = accountsRepo.findByAccountNumberAndAccountType(SenderAccountNumber, "current");
                                if (current.isPresent()) { //perfom withdrawal for current account

                                    BigDecimal newsenderBalance = BigDecimal.valueOf(0);
                                    newsenderBalance = newsenderBalance.subtract(amountToTransfer);
                                    BigDecimal SenderActiveBalance =  senderBalance.subtract(BigDecimal.valueOf(-100000));
                                    int savingsAccountLimit = BigDecimal.valueOf(-100000).compareTo(newsenderBalance);
                                    if (savingsAccountLimit <= 0) {//pass sender attributes to generate Sender invoice

                                        //send payment to beneficiary account
                                        account.setBalance(newsenderBalance);
                                        accountsRepo.save(account);


                                        BigDecimal receiverBalance = account1.getBalance();
                                        receiverBalance = receiverBalance.add(amountToTransfer);
                                        account1.setBalance(receiverBalance);
                                        //Beneficiary account receives payment
                                        accountsRepo.save(account1);

                                        payment.setAccountNumber(SenderAccountNumber);
                                        payment.setBeneficiaryName(String.valueOf(ReceiverAccountNumber));
                                        payment.setAmount(amountToTransfer);
                                        payment.setRef(ref);
                                        payment.setTransactionType(transferTransaction);
                                        payment.setStatus(successTransactionStatus);
                                        payment.setUserId(senderUserId);

                                        //generate sender invoice
                                        paymentRepo.save(payment);
                                    }
                                    else {int k = 1;if (k > 1){} else throw new RuntimeException("Failed, Reason: Insufficient Funds - your balance should not exceed -R100 000.00. your active balance is R"+SenderActiveBalance);}
                                }
                            }//end of current transactions
                        }// end of self transactions
                    });//Receiver transaction end

                });//sender transaction end
            }
        }
    }
    public Object getAllPayments() {
        return paymentRepo.findAll();
    }
    public User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepo.findByUsername(auth.getName());
    }

}
