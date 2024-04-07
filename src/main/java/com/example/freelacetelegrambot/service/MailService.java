package com.example.freelacetelegrambot.service;


import com.example.freelacetelegrambot.enums.State;
import com.example.freelacetelegrambot.model.User;
import com.example.freelacetelegrambot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;

    @Value("${spring.mail.username}")
    private String emailFrom;

    @Value("${service.activation.url}")
    private String activationUri;

    public MailService(JavaMailSender javaMailSender, UserRepository userRepository) {
        this.javaMailSender = javaMailSender;
        this.userRepository = userRepository;
    }

    public void sendEmail(long id) {
        String subject = "Активация учетной записи";
        String messageBody = getActivationMailBody(id);
        String emailTo = userRepository.findById(id).orElseThrow().getEmail();

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(emailFrom);
        simpleMailMessage.setTo(emailTo);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(messageBody);

        javaMailSender.send(simpleMailMessage);
    }

    public void activation (long id) {
        User user = userRepository.findById(id).orElseThrow();
        user.setState(State.BASIK);
        userRepository.save(user);
    }

    private String getActivationMailBody(long id) {
        String msg = String.format("Для завершения регистрации кликните по ссылке: \n%s", activationUri);
        return msg.replace("{id}", String.valueOf(id));
    }
}
