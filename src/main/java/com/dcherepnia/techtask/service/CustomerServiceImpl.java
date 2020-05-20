package com.dcherepnia.techtask.service;

import com.dcherepnia.techtask.domain.Customer;
import com.dcherepnia.techtask.domain.Loan;
import com.dcherepnia.techtask.repo.CustomerRepo;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Set;


@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepo customerRepo;

    public CustomerServiceImpl(CustomerRepo customerRepo) {
        this.customerRepo = customerRepo;
    }

    @Override
    public boolean isBlackListed(Long id) {
        return customerRepo.isBlackListed(id);
    }

    @Override
    @Transactional
    public Set<Loan> getAllLoansByCustomer(Long id) {
        Customer customer = customerRepo.findById(id).orElse(new Customer());
        return customer.getLoans();
    }
}
