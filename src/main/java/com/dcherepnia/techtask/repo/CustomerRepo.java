package com.dcherepnia.techtask.repo;

import com.dcherepnia.techtask.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerRepo extends JpaRepository<Customer, Long> {

    @Query("select case when count(c) > 0 then true else false end from Customer c where c.id = :id and c.blacklisted=true")
    boolean isBlackListed(@Param("id") Long id);
}
