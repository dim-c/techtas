package com.dcherepnia.techtask.service;

import com.dcherepnia.techtask.domain.Customer;
import com.dcherepnia.techtask.domain.Loan;
import com.dcherepnia.techtask.repo.CustomerRepo;
import com.dcherepnia.techtask.repo.LoanRepo;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class LoanServiceImpl implements LoanService {

    private final LoanRepo loanRepo;

    private final CustomerRepo customerRepo;

    public LoanServiceImpl(LoanRepo loanRepo, CustomerRepo customerRepo) {
        this.loanRepo = loanRepo;
        this.customerRepo = customerRepo;
    }

    @Override
    public List<Loan> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return loanRepo.findAll(pageable).getContent();
    }

    @Override
    @Transactional
    public void applyForLoan(Loan loan, Customer customer) {
        customer = customerRepo.findById(customer.getId()).orElse(customer);
        loan.setCustomer(customer);
        loanRepo.save(loan);
    }
}
