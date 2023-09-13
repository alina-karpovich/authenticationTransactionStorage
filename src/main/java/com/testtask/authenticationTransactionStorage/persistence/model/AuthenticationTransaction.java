package com.testtask.authenticationTransactionStorage.persistence.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Entity
@Data
public class AuthenticationTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private Date timestamp;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String actor;

    @ElementCollection
    @MapKeyColumn(name="name")
    @Column(name="value")
    @CollectionTable(name="transaction_data", joinColumns=@JoinColumn(name="id"))
    private Map<String, String> transactionData;
}
