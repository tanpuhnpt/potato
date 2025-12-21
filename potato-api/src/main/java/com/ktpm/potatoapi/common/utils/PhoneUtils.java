package com.ktpm.potatoapi.common.utils;

public class PhoneUtils {
    public static String formatPhoneNumber(String phone) {
        String digitsOnly = phone.replaceAll("[\\s\\-.]", "");

        if (digitsOnly.startsWith("+84"))
            digitsOnly = "0" + digitsOnly.substring(3);

        return digitsOnly;
    }
}
