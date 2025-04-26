package fishdicg.moncoeur.order_service.service;

import event.dto.DecreaseStockEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KafkaProducerService {
    KafkaTemplate<String, DecreaseStockEvent> kafkaTemplate;

    public void decreaseInventoryStock(String productId, Integer quantity) {
        DecreaseStockEvent decreaseStockEvent = new DecreaseStockEvent(productId, quantity);
        kafkaTemplate.send("decrease-stock-topic", decreaseStockEvent);
    }
}
