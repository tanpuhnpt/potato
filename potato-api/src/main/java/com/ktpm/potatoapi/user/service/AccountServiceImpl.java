package com.ktpm.potatoapi.user.service;

import com.ktpm.potatoapi.common.exception.AppException;
import com.ktpm.potatoapi.common.exception.ErrorCode;
import com.ktpm.potatoapi.security.AuthContextProvider;
import com.ktpm.potatoapi.user.dto.ChangePasswordRequest;
import com.ktpm.potatoapi.user.entity.User;
import com.ktpm.potatoapi.user.repo.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AccountServiceImpl implements AccountService {
    UserRepository userRepository;
    AuthContextProvider authContextProvider;
    PasswordEncoder passwordEncoder;

    @Override
    public void changePassword(ChangePasswordRequest request) {
        User user = userRepository.findByEmail(authContextProvider.getCurrentUserEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Check current password and new password are different
        if(request.getCurrentPassword().equals(request.getNewPassword())){
            log.error("New password is same as current password");
            throw new AppException(ErrorCode.SAME_PASSWORD);
        }

        // Check password again
        if(!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            log.error("Current password doesn't match");
            throw new AppException(ErrorCode.INVALID_CREDENTIALS); // Wrong password
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
        log.info("{} change password success", user.getEmail());
    }

}
