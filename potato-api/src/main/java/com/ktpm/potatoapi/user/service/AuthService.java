package com.ktpm.potatoapi.user.service;

import com.ktpm.potatoapi.user.dto.LogInRequest;
import com.ktpm.potatoapi.user.dto.AuthResponse;
import com.ktpm.potatoapi.user.dto.SignUpRequest;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService extends UserDetailsService {
    /**
     * Registers a new user account (sign up).
     *
     * @param signUpRequest the registration information provided by the user (email, password, full name)
     * @return a {@link AuthResponse} containing the username, email, and generated token
     */
    AuthResponse signUp(SignUpRequest signUpRequest);

    /**
     * Log in an existing user.
     *
     * @param logInRequest the login request containing email and password
     * @return a {@link AuthResponse} containing username, email, and authentication token
     */
    AuthResponse logIn(LogInRequest logInRequest);
}
