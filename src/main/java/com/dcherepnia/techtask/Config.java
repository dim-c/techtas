package com.dcherepnia.techtask;

import com.dcherepnia.techtask.controller.filter.blacklist.BlackListFiler;
import com.dcherepnia.techtask.controller.filter.countrycode.CountryCodeFilter;
import com.dcherepnia.techtask.controller.filter.requestslimiter.RateLimitingInterceptor;
import com.dcherepnia.techtask.service.CustomerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;

import static com.dcherepnia.techtask.controller.LoanController.LOANS_URL;

@Configuration
public class Config implements WebMvcConfigurer {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private RateLimitingInterceptor rateLimitingInterceptor;



    @Value("#{new Long('${ip.server.timeout.seconds}')}")
    private Long timeOutDuration;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.setConnectTimeout(Duration.ofSeconds(timeOutDuration)).build();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitingInterceptor).addPathPatterns(LOANS_URL);
    }


    @Bean
    public FilterRegistrationBean<BlackListFiler> loggingFilter() {
        FilterRegistrationBean<BlackListFiler> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new BlackListFiler(customerService));
        registrationBean.addUrlPatterns(LOANS_URL);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<CountryCodeFilter> countryCodeFilter() {
        FilterRegistrationBean<CountryCodeFilter> registrationBean = new FilterRegistrationBean<>();
        CountryCodeFilter countryCodeFilter = new CountryCodeFilter(restTemplate(new RestTemplateBuilder()));
        registrationBean.setFilter(countryCodeFilter);
        registrationBean.addUrlPatterns(LOANS_URL);
        return registrationBean;
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
