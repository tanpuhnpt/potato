package com.ktpm.potatoapi.user.controller;

import com.ktpm.potatoapi.user.dto.ChangePasswordRequest;
import com.ktpm.potatoapi.user.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
@Tag(name = "Account APIs", description = "APIs for account")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CrossOrigin("*")
public class AccountController {
    AccountService accountService;

    @PostMapping("/change-password")
    @Operation(summary = "Change password",
            description = "API for users to change current password")
    public ResponseEntity<?> login(@RequestBody @Valid ChangePasswordRequest changePasswordRequest) {
        accountService.changePassword(changePasswordRequest);
        return ResponseEntity.ok().build();
    }
}
