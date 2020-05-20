package com.dcherepnia.techtask.service;

import com.dcherepnia.techtask.domain.Loan;

import java.util.Set;

public interface CustomerService {

    boolean isBlackListed(Long id);

    Set<Loan> getAllLoansByCustomer(Long id);

}
