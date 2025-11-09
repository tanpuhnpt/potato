package com.ktpm.potatoapi.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MailServiceImpl implements MailService {
    JavaMailSender mailSender;
    TemplateEngine templateEngine;

    @NonFinal
    @Value("${spring.mail.username}")
    String mailFrom;

    @Override
    public void sendMerchantActivationEmail(String mailTo, String fullName, String password) throws MessagingException {
        Context context = new Context();
        context.setVariable("fullName", fullName);
        context.setVariable("email", mailTo);
        context.setVariable("password", password);

        String htmlContent = templateEngine.process("email/merchant_activated", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(mailTo);
        helper.setFrom(mailFrom);
        helper.setSubject("Merchant Activated");
        helper.setText(htmlContent, true); // Gửi dạng HTML

        mailSender.send(message);
    }

    @Override
    public void sendRegistrationApprovalEmail(String mailTo, String fullName, String merchantName) throws MessagingException {
        Context context = new Context();
        context.setVariable("fullName", fullName);
        context.setVariable("merchantName", merchantName);

        String htmlContent = templateEngine.process("email/registration_approved", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(mailTo);
        helper.setFrom(mailFrom);
        helper.setSubject("Merchant Registration Approved");
        helper.setText(htmlContent, true); // Gửi dạng HTML

        mailSender.send(message);
    }
}
