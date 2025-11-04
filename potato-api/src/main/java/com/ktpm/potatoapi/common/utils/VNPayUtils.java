package com.ktpm.potatoapi.common.utils;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Random;

@Slf4j
public class VNPayUtils {
    public static String hmacSHA512(final String key, final String data) {
        try {
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);

            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);

            StringBuilder str = new StringBuilder(2 * result.length);
            for (byte b : result)
                str.append(String.format("%02x", b & 0xff));

            return str.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public static String getRandomNumber(int len) {
        Random rd = new Random();
        String chars = "0123456789";

        StringBuilder str = new StringBuilder();
        for (int i = 0; i < len; i++)
            str.append(chars.charAt(rd.nextInt(chars.length())));

        return str.toString();
    }
}
