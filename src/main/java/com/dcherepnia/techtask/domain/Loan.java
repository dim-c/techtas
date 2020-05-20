package com.dcherepnia.techtask.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
public class Loan {

    public Loan(Customer customer, BigDecimal amount, LocalDateTime term, String country) {
        this.customer = customer;
        this.amount = amount;
        this.term = term;
        this.country = country;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    private BigDecimal amount;

    private LocalDateTime term;

    private String country;
}
