package com.app.application.service;

import com.app.infrastructure.exception.MailException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Objects;

@Service
@RequiredArgsConstructor
//@SessionScope
@Slf4j
@EnableAsync
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendPlainEmail(String to, String mailText, String from) {

        SimpleMailMessage mailMessage = createMail(to, mailText, from);
        mailSender.send(mailMessage);
    }

    @Async
    public void sendAsHtml(String from, String to, String htmlContent, String title) {

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = null;
            messageHelper = new MimeMessageHelper(mimeMessage, false);
            messageHelper.setText(htmlContent, true);
            messageHelper.setFrom(Objects.requireNonNullElse(from, "noreply@domain.com"));
            messageHelper.setTo(to);
            messageHelper.setSubject(title);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.info("Exception during mail sending");
            log.error(e.getMessage());
            throw new MailException(e.getMessage());
        }
    }

    private SimpleMailMessage createMail(String to, String mailText, String from) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(Objects.requireNonNullElse(from, "noreply@domain.com"));
        mailMessage.setTo(to);
        mailMessage.setText(mailText);

        return mailMessage;
    }
}
