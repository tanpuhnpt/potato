package com.ktpm.potatoapi.user.service;

import com.ktpm.potatoapi.user.dto.ChangePasswordRequest;

public interface AccountService {
    void changePassword(ChangePasswordRequest request);
}
