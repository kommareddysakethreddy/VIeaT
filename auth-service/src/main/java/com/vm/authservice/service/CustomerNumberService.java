package com.vm.authservice.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class CustomerNumberService {

    private final JdbcTemplate jdbcTemplate;

    public CustomerNumberService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long nextCustomerNumber() {
        return jdbcTemplate.queryForObject("SELECT nextval('auth_customer_number_seq')", Long.class);
    }
}
