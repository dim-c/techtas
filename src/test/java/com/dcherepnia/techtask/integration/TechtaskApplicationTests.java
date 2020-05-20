package com.dcherepnia.techtask.integration;

import com.dcherepnia.techtask.controller.dto.CustomerDto;
import com.dcherepnia.techtask.controller.dto.LoanDto;
import com.dcherepnia.techtask.controller.dto.LoanForm;
import com.dcherepnia.techtask.controller.filter.requestslimiter.RateLimitingInterceptor;
import com.dcherepnia.techtask.domain.Customer;
import com.dcherepnia.techtask.repo.CustomerRepo;
import com.dcherepnia.techtask.repo.LoanRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.dcherepnia.techtask.controller.LoanController.LOANS_URL;
import static com.dcherepnia.techtask.controller.filter.countrycode.CountryCodeFilter.DEFAULT_COUNTRY_CODE;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class TechtaskApplicationTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    LoanRepo loanRepo;
    @Autowired
    CustomerRepo customerRepo;
    @Autowired
    FilterRegistrationBean countryCodeFilter;

    @Mock
    private RestTemplate restTemplate;
    @Autowired
    private RateLimitingInterceptor rateLimitingInterceptor;

    @AfterEach
    void destroyFilter() {
        rateLimitingInterceptor.destroy();
        countryCodeFilter.getFilter().destroy();
    }

    @Test
    @SneakyThrows
    void checkThatGetRequestReturnsJsonWithRightSize() {
        int expectedSize = loanRepo.findAll().size();
        this.mockMvc.perform(MockMvcRequestBuilders.get("/loans"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(expectedSize)));
    }

    @Test
    @SneakyThrows
    void checkThatGetRequestWithPaginationReturnsJsonWithExactSize() {
        int size = 3;
        this.mockMvc.perform(MockMvcRequestBuilders.get("/loans?page=0&size=" + size))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(size)));
    }

    @Test
    @SneakyThrows
    void checkThatPostRequestAddsOneLoanAndOneCustomer() {
        int expectedSizeLoans = loanRepo.findAll().size() + 1;
        int expectedSizeUsers = customerRepo.findAll().size() + 1;
        long id = customerRepo.findAll().stream().map(Customer::getId).max(Long::compareTo).get() + 1;

        makeLoanRequestWithDtoAndCheckStatus(id, status().isCreated());

        int actualSizeUsers = customerRepo.findAll().size();
        int actualSizeLoans = loanRepo.findAll().size();
        assertEquals("User wasn't created", expectedSizeUsers, actualSizeUsers);
        assertEquals("Loan wasn't created", expectedSizeLoans, actualSizeLoans);
    }

    @Test
    @SneakyThrows
    void checkDefaultCountryIfThirdPartyWebServiceIsFailed() {
        ReflectionTestUtils.setField(countryCodeFilter.getFilter(), "restTemplate", restTemplate);
        Mockito.when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(new ResponseEntity(HttpStatus.NOT_FOUND));
        long customerId = customerRepo.findAll().stream().map(Customer::getId).max(Long::compareTo).get() + 1000;
        makeLoanRequestWithDtoAndCheckStatus(customerId, status().isCreated());
        String actualCountry = loanRepo.findAllByCustomerId(customerId).iterator().next().getCountry();
        assertEquals("Country code is not default", DEFAULT_COUNTRY_CODE, actualCountry);
    }

    @Test
    @SneakyThrows
    void checkCountryCoedIfThirdPartyWebServiceIsOK() {
        String countryCode = "UA";
        mockThirdPartyResponse(countryCode);
        long customerId = customerRepo.findAll().stream().map(Customer::getId).max(Long::compareTo).get() + 1000;
        makeLoanRequestWithDtoAndCheckStatus(customerId, status().isCreated());
        String actualCountry = loanRepo.findAllByCustomerId(customerId).iterator().next().getCountry();
        assertEquals("Country code is not as expected", countryCode, actualCountry);
    }

    private void mockThirdPartyResponse(String countryCode) {
        ReflectionTestUtils.setField(countryCodeFilter.getFilter(), "restTemplate", restTemplate);
        Mockito.when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(new ResponseEntity("{ \"countryCode\" : \"" + countryCode + "\" }", HttpStatus.OK));
    }

    @Test
    @SneakyThrows
    void checkLimitAccessFromCountry() {
        String countryCode = "UA";
        ReflectionTestUtils.setField(countryCodeFilter.getFilter(), "restTemplate", restTemplate);
        Mockito.when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(new ResponseEntity("{ \"countryCode\" : \"" + countryCode + "\" }", HttpStatus.OK));
        long customerId = customerRepo.findAll().stream().map(Customer::getId).max(Long::compareTo).get() + 1000;
        for (int i = 0; i < 10; i++) {
            makeLoanRequestWithDtoAndCheckStatus(customerId, status().isCreated());
            customerId++;
        }
        this.mockMvc.perform(MockMvcRequestBuilders.post("/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(new LoanForm(new CustomerDto("UserName", "UserSurname", customerId),
                        new LoanDto(BigDecimal.ONE, LocalDateTime.now())))))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    @SneakyThrows
    void checkThatBlacklistedCustomerCantCreateLoan() {
        long customerId = customerRepo.findAll().stream().map(Customer::getId).max(Long::compareTo).get() + 1000;
        customerRepo.save(new Customer(customerId, "UserName", "UserSurname", true));
        makeLoanRequestWithDtoAndCheckStatus(customerId, status().isForbidden());
    }

    @Test
    @SneakyThrows
    void getLoansByCustomerId() {
        long customerId = customerRepo.findAll().stream().filter(c -> !c.isBlacklisted()).findAny().get().getId();
        int loansCount = loanRepo.findAllByCustomerId(customerId).size();

        this.mockMvc.perform(MockMvcRequestBuilders.get("/customer/" + customerId + "/loans"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(loansCount)));
    }

    private void makeLoanRequestWithDtoAndCheckStatus(long customerId, ResultMatcher matcher) throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post(LOANS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(new LoanForm(new CustomerDto("UserName", "UserSurname", customerId),
                        new LoanDto(BigDecimal.ONE, LocalDateTime.now())))))
                .andExpect(matcher);
    }

    private static String asJsonString(final Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
