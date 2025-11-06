package com.ktpm.potatoapi.mail;

import jakarta.mail.MessagingException;

public interface MailService {
    void sendApprovalEmail(String to, String fullName, String password) throws MessagingException;
    void sendEmail(String mailTo, String fullName) throws MessagingException;
}
