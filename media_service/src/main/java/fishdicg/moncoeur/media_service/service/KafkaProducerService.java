package fishdicg.moncoeur.media_service.service;

import event.dto.UploadImageEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KafkaProducerService {
    KafkaTemplate<String, UploadImageEvent> kafkaTemplate;

    public void uploadImage(String productId, String imageName) {
        UploadImageEvent uploadImageEvent = new UploadImageEvent(productId, imageName);
        kafkaTemplate.send("upload-image-topic", uploadImageEvent);
    }
}
