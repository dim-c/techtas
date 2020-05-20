package com.dcherepnia.techtask.controller.filter.countrycode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.http.HttpMethod.POST;

@Slf4j
public class CountryCodeFilter implements Filter {


    private static final String LOCAL_HOST_ADRESS = "127.0.0.1";
    private final String LOCAL_NAT_ADDRESS = "0:0:0:0:0:0:0:1";
    public static final String DEFAULT_COUNTRY_CODE = "LV";
    private static final String IP_SERVER_ADDRESS = "http://ip-api.com/json/";
    private final RestTemplate restTemplate;

    private static final ConcurrentHashMap<String, String> countryCache = new ConcurrentHashMap<>();

    public CountryCodeFilter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        if (!req.getMethod().equals(POST.name())) {
            chain.doFilter(req, response);
            return;
        }
        HeaderMapRequestWrapper requestWrapper = new HeaderMapRequestWrapper(req);
        String ip = extractIpAddress(requestWrapper);
        String countryCode = countryCache.get(ip) != null ? countryCache.get(ip) : getCountryCode(ip);
        requestWrapper.addHeader("Country", countryCode);
        chain.doFilter(requestWrapper, response);
    }

    private String getCountryCode(String ip) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(IP_SERVER_ADDRESS + ip + "?fields=2", String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = new ObjectMapper().readTree(Optional.ofNullable(response.getBody()).orElse("{}"));
                Optional<JsonNode> customerId = Optional.ofNullable(jsonNode.get("countryCode"));
                if (customerId.isPresent()) {
                    countryCache.put(ip, customerId.get().asText());
                    return customerId.get().asText();
                }
            }
        } catch (ResourceAccessException | JsonProcessingException e) {
            log.warn(e.getMessage());
        }
        return DEFAULT_COUNTRY_CODE;
    }

    private String extractIpAddress(HeaderMapRequestWrapper requestWrapper) throws UnknownHostException {
        String ip = requestWrapper.getRemoteAddr();
        if (ip.equalsIgnoreCase(LOCAL_NAT_ADDRESS) || ip.equalsIgnoreCase(LOCAL_HOST_ADRESS)) {
            InetAddress inetAddress = InetAddress.getLocalHost();
            ip = inetAddress.getHostAddress();
        }
        return ip;
    }

    private void clearCache() {
        countryCache.clear();
    }

    @Override
    public void destroy() {
        clearCache();
    }

    static class HeaderMapRequestWrapper extends HttpServletRequestWrapper {
        /**
         * construct a wrapper for this request
         *
         * @param request
         */
        HeaderMapRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        private final Map<String, String> headerMap = new HashMap<>();

        /**
         * add a header with given name and value
         *
         * @param name
         * @param value
         */
        void addHeader(String name, String value) {
            headerMap.put(name, value);
        }

        @Override
        public String getHeader(String name) {
            String headerValue = super.getHeader(name);
            if (headerMap.containsKey(name)) {
                headerValue = headerMap.get(name);
            }
            return headerValue;
        }

        /**
         * get the Header names
         */
        @Override
        public Enumeration<String> getHeaderNames() {
            List<String> names = Collections.list(super.getHeaderNames());
            names.addAll(headerMap.keySet());
            return Collections.enumeration(names);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            List<String> values = Collections.list(super.getHeaders(name));
            if (headerMap.containsKey(name)) {
                values.add(headerMap.get(name));
            }
            return Collections.enumeration(values);
        }
    }
}
