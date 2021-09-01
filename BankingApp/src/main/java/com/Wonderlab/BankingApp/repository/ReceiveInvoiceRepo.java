package com.Wonderlab.BankingApp.repository;


import com.Wonderlab.BankingApp.model.ReceiverInvoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReceiveInvoiceRepo extends JpaRepository<ReceiverInvoice, Long > {
    Optional<ReceiverInvoice> findByRef(int random);
    List<ReceiverInvoice> findByTransactionTypeAndUserId(String receiverTransactionType, Long userId);
    Optional<ReceiverInvoice> findByRefAndTransactionType(int ref, String deposit);
    List<ReceiverInvoice> findByUserId(Long currentUserId);
}
