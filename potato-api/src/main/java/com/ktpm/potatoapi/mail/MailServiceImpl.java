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

import java.util.Map;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MailServiceImpl implements MailService {
    JavaMailSender mailSender;
    TemplateEngine templateEngine;

    @NonFinal
    @Value("${spring.mail.username}")
    String mailFrom;

    // generic  mail
    private void sendEmail(String mailTo, String subject, String templateName, Map<String, Object> variables)
            throws MessagingException {

        Context context = new Context();
        context.setVariables(variables);

        String htmlContent = templateEngine.process(templateName, context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(mailTo);
        helper.setFrom(mailFrom);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    @Override
    public void sendRegistrationApprovalEmail(String mailTo, String fullName, String merchantName) throws MessagingException {
        sendEmail(
                mailTo,
                "Merchant Registration Review Result",
                "email/registration_approved",
                Map.of(
                        "fullName", fullName,
                        "merchantName", merchantName
                )
        );
    }

    @Override
    public void sendRegistrationRejectionEmail(String mailTo, String fullName) throws MessagingException {
        sendEmail(
                mailTo,
                "Merchant Registration Review Result",
                "email/registration_rejected",
                Map.of("fullName", fullName)
        );
    }


    @Override
    public void sendMerchantActivationEmail(String mailTo, String fullName, String password) throws MessagingException {
        sendEmail(
                mailTo,
                "Merchant Activated",
                "email/merchant_activated",
                Map.of(
                        "fullName", fullName,
                        "email", mailTo,
                        "password", password
                )
        );
    }
}
