package com.dcherepnia.techtask.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class CustomerDto {

    @NotEmpty
    private String name;
    @NotEmpty
    private String surname;
    @NotNull
    private Long id;
}
