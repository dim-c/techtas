package com.dcherepnia.techtask.repo;

import com.dcherepnia.techtask.domain.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface LoanRepo extends JpaRepository<Loan, Long> {

    Set<Loan> findAllByCustomerId(long id);


}
