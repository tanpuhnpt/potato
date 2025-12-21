package com.ktpm.potatoapi.common.config.payment;

import com.ktpm.potatoapi.common.utils.VNPayUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VNPayConfig {
    @Getter
    @Value("${vnpay.url}")
    String paymentUrl;

    @Value("${vnpay.returnUrl}")
    String returnUrl;

    @Value("${vnpay.tmnCode}")
    String terminalCode;

    @Getter
    @Value("${vnpay.secretKey}")
    String secretKey;

    final String transactionReference = VNPayUtils.getRandomNumber(8);

    public Map<String, String> getVNPayConfig(HttpServletRequest request) {
        Map<String, String> vnpParams = new HashMap<>();

        // set up transaction data
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_BankCode", "NCB");
        vnpParams.put("vnp_IpAddr", getClientIp(request));
        vnpParams.put("vnp_TmnCode", this.terminalCode);
        vnpParams.put("vnp_ReturnUrl", this.returnUrl);
        vnpParams.put("vnp_TxnRef", transactionReference);
        vnpParams.put("vnp_OrderInfo", "Thanh toan don hang: " + transactionReference);

        // set up transaction time
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

        String vnpCreateDate = formatter.format(calendar.getTime());
        vnpParams.put("vnp_CreateDate", vnpCreateDate);

        calendar.add(Calendar.MINUTE, 15); // transaction time: 15 mins

        String vnpExpDate = formatter.format(calendar.getTime());
        vnpParams.put("vnp_ExpireDate", vnpExpDate);

        return vnpParams;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // Trường hợp nhiều IP (qua proxy, load balancer)
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        System.out.println(ip);
        return ip;
    }

}
