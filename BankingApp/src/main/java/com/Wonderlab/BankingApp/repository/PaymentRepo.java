package com.Wonderlab.BankingApp.repository;



import com.Wonderlab.BankingApp.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface PaymentRepo extends JpaRepository<Payment, Long> {

    List<Payment> findByUserIdAndTransactionType(Long id,String transactionType1);
    Optional<Payment> findByRef(int ref);
    Optional<Payment> findByRefAndTransactionType(int ref, String transfer);
}
