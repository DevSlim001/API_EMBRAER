package com.slim.manual.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class SenderMailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Envia um email
     * @param email
     * @param assunto
     * @param texto
     */
    public void enviar(String email,String assunto,String texto) {
        SimpleMailMessage emailMessage = new SimpleMailMessage();
        emailMessage.setTo(email);
        emailMessage.setSubject(assunto);
        emailMessage.setText(texto);
        mailSender.send(emailMessage);
    }
}