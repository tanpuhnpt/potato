package com.ktpm.potatoapi.mail;

import jakarta.mail.MessagingException;

public interface MailService {
    void sendMerchantActivationEmail(String to, String fullName, String password) throws MessagingException;
    void sendRegistrationApprovalEmail(String mailTo, String fullName, String merchantName) throws MessagingException;
}
