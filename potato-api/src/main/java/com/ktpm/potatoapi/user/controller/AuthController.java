package com.ktpm.potatoapi.user.controller;

import com.ktpm.potatoapi.user.dto.LogInRequest;
import com.ktpm.potatoapi.user.dto.SignUpRequest;
import com.ktpm.potatoapi.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller that provides user authentication APIs.
 * <p>
 * This controller exposes endpoints under {@code /auth} for:
 * <ul>
 *     <li>User registration (Sign up)</li>
 *     <li>User authentication (Log in)</li>
 * </ul>
 *
 * It delegates user-related business logic to the {@link AuthService}.
 * @author Hieu, Thanh
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication APIs", description = "APIs for signup and login")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CrossOrigin(origins = "*")
public class AuthController {
    AuthService authService;

    @PostMapping("/sign-up")
    @Operation(summary = "Sign up", description = "API for users to register an account")
    public ResponseEntity<?> signup(@RequestBody @Valid SignUpRequest signUpRequest) {
        return ResponseEntity.ok(authService.signUp(signUpRequest));
    }

    @PostMapping("/log-in")
    @Operation(summary = "Log in", description = "API for users to log in")
    public ResponseEntity<?> login(@RequestBody @Valid LogInRequest logInRequest) {
        return ResponseEntity.ok(authService.logIn(logInRequest));
    }
}
