package fishdicg.moncoeur.identity_service.service;

import event.dto.CreateUserEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KafkaProducerService {
    KafkaTemplate<String, CreateUserEvent> kafkaTemplate;

    public void sendEmailEvent(String toEmail, String code) {
        CreateUserEvent emailEvent1 = new CreateUserEvent(code, toEmail);
        kafkaTemplate.send("create-user-topic", emailEvent1);
    }
}