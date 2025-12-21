package com.ktpm.potatoapi.common.utils;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderCodeUtils {
    public static String generateOrderCode() {
        String prefix = "POTATO";
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmm"));
        int random = new Random().nextInt(900) + 100;
        return prefix + dateTime + random;
    }
}
