package fishdicg.moncoeur.payment_service.service;

import event.dto.CartPayedEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KafkaProducerService {
    KafkaTemplate<String, CartPayedEvent> kafkaTemplate;

    public void paymentCompleted(String cartId) {
        CartPayedEvent cartPayedEvent = new CartPayedEvent(cartId);
        kafkaTemplate.send("payment-completed-topic", cartPayedEvent);
    }
}
