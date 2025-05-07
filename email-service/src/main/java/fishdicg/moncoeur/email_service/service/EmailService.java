package fishdicg.moncoeur.email_service.service;

import event.dto.CreateUserEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmailService {
    @NonFinal
    @Value("${spring.mail.username}")
    protected String email;

    @Autowired
    JavaMailSender javaMailSender;

    @KafkaListener(topics = "create-user-topic", groupId = "email-service-group")
    public void createUser(CreateUserEvent event) {
        log.info("Email sent to: {} with code {}", event.getEmail(), event.getCode());
        sendMail(event);
    }

    public void sendMail(CreateUserEvent event) {
        String content = "Your verification code is: " + event.getCode();
        String subject = "MonCoeur Verification";
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(email);
        message.setTo(event.getEmail());
        message.setSubject(subject);
        message.setText(content);

        javaMailSender.send(message);
        log.info("Email sent successfully.");
    }
}
