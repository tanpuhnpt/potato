package com.ktpm.potatoapi.user.service;

import com.ktpm.potatoapi.user.dto.AuthResponse;
import com.ktpm.potatoapi.user.dto.LogInRequest;
import com.ktpm.potatoapi.user.dto.SignUpRequest;
import com.ktpm.potatoapi.user.entity.Role;
import com.ktpm.potatoapi.user.entity.User;
import com.ktpm.potatoapi.user.mapper.UserMapper;
import com.ktpm.potatoapi.user.repo.UserRepository;
import com.ktpm.potatoapi.common.exception.AppException;
import com.ktpm.potatoapi.common.exception.ErrorCode;
import com.ktpm.potatoapi.common.utils.JwtUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthServiceImpl implements AuthService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    UserMapper userMapper;

    @Override
    public AuthResponse signUp(SignUpRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail()))
            throw new AppException(ErrorCode.USER_EXISTED);

        // Create new user
        User user = userMapper.toEntity(signUpRequest);
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setRole(Role.CUSTOMER);
        userRepository.save(user);

        // Generate Jwt token
        log.info("{} sign up success", user.getEmail());
        return new AuthResponse(JwtUtils.createToken(user));
    }

    @Override
    public AuthResponse logIn(LogInRequest logInRequest) {
        User user = userRepository.findByEmail(logInRequest.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        boolean isAuthenticated = passwordEncoder.matches(logInRequest.getPassword(),user.getPassword());
        if(!isAuthenticated)
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        // Generate Jwt token
        log.info("{} log in success", user.getEmail());
        return new AuthResponse(JwtUtils.createToken(user));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRole().name())
                .build();
    }
}
