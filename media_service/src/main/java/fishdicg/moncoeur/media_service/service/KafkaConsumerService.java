package fishdicg.moncoeur.media_service.service;

import event.dto.DeleteItemEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class KafkaConsumerService {
    MinioService minioService;

    @KafkaListener(topics = "delete-item-topic", groupId = "media-service-group")
    public void deleteItem(DeleteItemEvent event) {
        minioService.deleteFile(event.getFileName());
        log.info("File has been deleted by Kafka");
    }
}
