package com.dcherepnia.techtask.controller;

import com.dcherepnia.techtask.controller.dto.LoanDto;
import com.dcherepnia.techtask.controller.dto.LoanForm;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LoanController {

    String LOANS_URL = "/loans";

    List<LoanDto> getAllLoans(Optional<Integer> page, Optional<Integer> size);

    ResponseEntity createLoan(LoanForm loanForm, Map<String, String> headers);
}

