package com.Wonderlab.BankingApp.model;


import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;



@NoArgsConstructor
@AllArgsConstructor
@Data
@Getter
@Setter
@Entity
public class Account implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;
    @Column(nullable = false, updatable = false)
    private int accountNumber; //denotes the account number
    private String  acc_name;
    private String accountType;
    private BigDecimal balance;
    @Column(name = "created_at", columnDefinition = "timestamp default current_timestamp not null")
    private Timestamp created_at = new Timestamp(System.currentTimeMillis());

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;




}
