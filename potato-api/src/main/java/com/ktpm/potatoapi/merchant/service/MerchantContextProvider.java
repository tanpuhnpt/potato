package com.ktpm.potatoapi.merchant.service;

import com.ktpm.potatoapi.common.exception.AppException;
import com.ktpm.potatoapi.common.exception.ErrorCode;
import com.ktpm.potatoapi.merchant.entity.Merchant;
import com.ktpm.potatoapi.merchant.repo.MerchantRepository;
import com.ktpm.potatoapi.redis.RedisService;
import com.ktpm.potatoapi.security.AuthContextProvider;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class MerchantContextProvider {
    AuthContextProvider authContextProvider;
    MerchantRepository merchantRepository;
    RedisService redisService;

    public Merchant getCurrentMerchant() {
        String email = authContextProvider.getCurrentUserEmail();
        String key = "merchant:" + email;

        Merchant merchant = redisService.get(key, Merchant.class);

        if (merchant == null) {
            log.info("query current merchant");
            merchant = merchantRepository.findByMerchantAdmin_Email(email)
                    .orElseThrow(() -> new AppException(ErrorCode.MERCHANT_NOT_FOUND));

            redisService.save(key, merchant);
        }

        return merchant;
    }
}
