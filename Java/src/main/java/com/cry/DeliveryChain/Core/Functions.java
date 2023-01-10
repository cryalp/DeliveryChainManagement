package com.cry.DeliveryChain.Core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class Functions {
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    public Functions() {}

    public void Logger(Object message) {
        try {
            System.out.println("--**--" + Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName());
            System.out.println(message.toString());
            System.out.println("--**--" + Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName());
        }
        catch (Exception e) {
            System.out.println("--**--" + Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName());
            System.err.println(e.getMessage());
            System.out.println("--**--" + Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName());
        }
    }

    public Boolean SendMail(String recipient, String subject, String msgBody) {
        try {
            var mailMessage = new SimpleMailMessage();

            mailMessage.setFrom(sender);
            mailMessage.setTo(recipient);
            mailMessage.setText(msgBody);
            mailMessage.setSubject(subject);

            javaMailSender.send(mailMessage);
            return true;
        }
        catch (Exception e) {
            Logger(e.getMessage());
            return false;
        }
    }
}
