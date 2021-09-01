package com.Wonderlab.BankingApp.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Payment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;
    private Long userId;

    private int accountNumber; //denotes the account number
    private String beneficiaryName;
    private  int beneficiaryAccount;
    private BigDecimal amount;
    public String transactionType;
    private  int ref;
    private String status;

    @Column(name = "created_at", columnDefinition = "timestamp default current_timestamp not null")
    private Timestamp created_at = new Timestamp(System.currentTimeMillis());

}
