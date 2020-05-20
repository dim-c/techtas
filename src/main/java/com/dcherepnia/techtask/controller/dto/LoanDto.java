package com.dcherepnia.techtask.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class LoanDto {

    public LoanDto(@Positive BigDecimal amount, LocalDateTime term) {
        this.amount = amount;
        this.term = term;
    }

    @Positive
    private BigDecimal amount;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime term;

    private CustomerDto customer;

    private String country;
}
