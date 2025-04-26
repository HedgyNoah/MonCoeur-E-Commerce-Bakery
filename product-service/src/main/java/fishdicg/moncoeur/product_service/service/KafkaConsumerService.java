package fishdicg.moncoeur.product_service.service;

import event.dto.UploadImageEvent;
import fishdicg.moncoeur.product_service.entity.Product;
import fishdicg.moncoeur.product_service.exception.AppException;
import fishdicg.moncoeur.product_service.exception.ErrorCode;
import fishdicg.moncoeur.product_service.repository.ProductRepository;
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
    ProductRepository productRepository;

    @KafkaListener(topics = "upload-image-topic", groupId = "product-service-group")
    public void uploadImage(UploadImageEvent event) {
        Product product = productRepository.findById(event.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
        product.setImageName(event.getImageName());
        productRepository.save(product);
        log.info("image name has been saved");
    }
}
