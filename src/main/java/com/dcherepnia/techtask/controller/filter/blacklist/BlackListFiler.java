package com.dcherepnia.techtask.controller.filter.blacklist;


import com.dcherepnia.techtask.service.CustomerService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.springframework.http.HttpMethod.POST;

public class BlackListFiler implements Filter {

    private final CustomerService customerService;

    public BlackListFiler(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        if (request.getMethod().equals(POST.name())) {
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            request = new MultiReadHttpServletRequest(request);
            String requestBody = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
            JsonNode requestJson = new ObjectMapper().readTree(requestBody);
            Optional<JsonNode> customerId = Optional.ofNullable(requestJson.get("customer")).map(node -> node.get("id"));
            boolean isBlackListed = (customerId.isPresent() && customerService.isBlackListed(customerId.get().asLong()));
            if (isBlackListed) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }
        filterChain.doFilter(request, servletResponse);
    }
}
