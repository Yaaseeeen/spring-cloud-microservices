package com.example.notification.service;

import com.example.notification.config.RabbitMQConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class DepositMessageHandler {

    private final JavaMailSender javaMailSender;

    public DepositMessageHandler(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_DEPOSIT)
    public void receive(Message message) throws JsonProcessingException {
        System.out.println(message);
        byte[] body = message.getBody();
        String jsonBody = new String(body);
        ObjectMapper mapper = new ObjectMapper();
        DepositResponseDTO depositResponseDTO = mapper.readValue(jsonBody, DepositResponseDTO.class);
        System.out.println(depositResponseDTO);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(depositResponseDTO.getMail());
        mailMessage.setFrom("lori1111");
        mailMessage.setSubject("Deposit");
        mailMessage.setText("Make deposit, sum: " + depositResponseDTO.getAmount());
        try {
            javaMailSender.send(mailMessage);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
