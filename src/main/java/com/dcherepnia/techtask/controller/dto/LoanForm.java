package com.dcherepnia.techtask.controller.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class LoanForm {

    @JsonProperty("customer")
    private CustomerDto customerDto;
    @JsonProperty("loan")
    private LoanDto loanDto;
}
