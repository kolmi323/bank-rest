package com.example.bankcards.controller;

import com.example.bankcards.security.CustomUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.SQLException;
import java.util.Collections;

public abstract class AbstractControllerTest {
    @Autowired protected MockMvc mockMvc;
    @Autowired protected ObjectMapper objectMapper;

    protected final int USER_ID = 1;
    protected Authentication authentication;

    protected ObjectWriter objectWriter;
    protected ConstraintViolationException constraintViolationSQLAlreadyExistException;

    @BeforeEach
    public void setUp() throws Exception {
        objectWriter = objectMapper.writer().withDefaultPrettyPrinter();

        CustomUserDetails user = new CustomUserDetails(
                USER_ID,
                "john@mail.ru",
                "password",
                Collections.emptyList()
        );
        authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        SQLException sqlException = new SQLException("repeat recording", "23505");
        constraintViolationSQLAlreadyExistException = new ConstraintViolationException("Such a record already exists", sqlException, "repeat recording");
    }
}
