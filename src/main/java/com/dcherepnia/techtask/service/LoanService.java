package com.dcherepnia.techtask.service;

import com.dcherepnia.techtask.domain.Customer;
import com.dcherepnia.techtask.domain.Loan;

import java.util.List;

public interface LoanService {

    List<Loan> getAll(int page, int size);

    void applyForLoan(Loan loan, Customer customer);
}
