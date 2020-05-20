package com.dcherepnia.techtask.controller;

import com.dcherepnia.techtask.controller.dto.CustomerDto;
import com.dcherepnia.techtask.controller.dto.LoanDto;
import com.dcherepnia.techtask.controller.dto.LoanForm;
import com.dcherepnia.techtask.domain.Customer;
import com.dcherepnia.techtask.domain.Loan;
import com.dcherepnia.techtask.service.CustomerService;
import com.dcherepnia.techtask.service.LoanService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class LoanControllerImpl implements LoanController {

    private final LoanService loanService;
    private final CustomerService customerService;
    private final ModelMapper modelMapper;

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int DEFAULT_STARTING_PAGE = 0;


    public LoanControllerImpl(LoanService loanService, CustomerService customerService, ModelMapper modelMapper) {
        this.loanService = loanService;
        this.customerService = customerService;
        this.modelMapper = modelMapper;
    }

    @GetMapping(LOANS_URL)
    public List<LoanDto> getAllLoans(@RequestParam(required = false) Optional<Integer> page,
                                     @RequestParam(required = false) Optional<Integer> size) {
        return loanService.getAll(page.orElse(DEFAULT_STARTING_PAGE), size.orElse(DEFAULT_PAGE_SIZE)).stream()
                .map(this::convertLoanEntityToDto).collect(Collectors.toList());
    }

    @PostMapping(value = LOANS_URL, consumes = "application/json")
    public ResponseEntity createLoan(@Valid @RequestBody LoanForm loanForm, @RequestHeader Map<String, String> headers) {
        loanForm.getLoanDto().setCountry(headers.get("Country"));
        loanService.applyForLoan(convertLoanDtoToEntity(loanForm.getLoanDto()), convertCustomerDtoToEntity(loanForm.getCustomerDto()));
        return new ResponseEntity<>("Loan form was applied", HttpStatus.CREATED);
    }

    @GetMapping("/customer/{id}/loans")
    public List<LoanDto> getAllLoans(@PathVariable long id) {
        return customerService.getAllLoansByCustomer(id).stream().map(this::convertLoanEntityToDto).collect(Collectors.toList());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    private Customer convertCustomerDtoToEntity(CustomerDto customerDto) {
        return modelMapper.map(customerDto, Customer.class);
    }

    private Loan convertLoanDtoToEntity(LoanDto loanDto) {
        return modelMapper.map(loanDto, Loan.class);
    }

    private LoanDto convertLoanEntityToDto(Loan loan) {
        return modelMapper.map(loan, LoanDto.class);
    }
}
