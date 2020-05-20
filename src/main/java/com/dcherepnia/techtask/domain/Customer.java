package com.dcherepnia.techtask.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Customer {
    public Customer(long id, String name, String surname, boolean blacklisted) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.blacklisted = blacklisted;
    }

    @Id
    private long id;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Loan> loans = new HashSet<>();

    private String name;

    private String surname;

    private boolean blacklisted = false;
}

