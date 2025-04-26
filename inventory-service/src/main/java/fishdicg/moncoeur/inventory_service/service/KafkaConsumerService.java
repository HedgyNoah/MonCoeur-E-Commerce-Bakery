package fishdicg.moncoeur.inventory_service.service;

import event.dto.CreateProductEvent;
import event.dto.DecreaseStockEvent;
import event.dto.DeleteItemEvent;
import event.dto.UpdateProductEvent;
import fishdicg.moncoeur.inventory_service.dto.request.InventoryRequest;
import fishdicg.moncoeur.inventory_service.entity.Inventory;
import fishdicg.moncoeur.inventory_service.exception.AppException;
import fishdicg.moncoeur.inventory_service.exception.ErrorCode;
import fishdicg.moncoeur.inventory_service.repository.InventoryRepository;
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
    InventoryRepository inventoryRepository;
    InventoryService inventoryService;

    @KafkaListener(topics = "create-product-topic", groupId = "inventory-service-group")
    public void createProduct(CreateProductEvent event) {
        inventoryService.create(InventoryRequest.builder()
                .productId(event.getProductId())
                .quantity(event.getQuantity())
                .lowStockThreshold(event.getLowStockThreshold()).build());
    }

    @KafkaListener(topics = "update-product-topic", groupId = "inventory-service-group")
    public void updateProduct(UpdateProductEvent event) {
        inventoryService.update(event.getProductId(), InventoryRequest.builder()
                .productId(event.getProductId())
                .quantity(event.getQuantity())
                .lowStockThreshold(event.getLowStockThreshold()).build());
    }

    @KafkaListener(topics = "delete-item-topic", groupId = "inventory-service-group")
    public void deleteItem(DeleteItemEvent event) {
        inventoryService.delete(event.getProductId());
    }

    @KafkaListener(topics = "decrease-stock-topic", groupId = "inventory-service-group")
    public void decreaseStock(DecreaseStockEvent event) {
        Inventory inventory = inventoryRepository.findByProductId(event.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_EXISTED));
        inventory.setQuantity(inventory.getQuantity() - event.getQuantity());
        inventoryRepository.save(inventory);
        log.info("Quantity after get decreased: {}", inventory.getQuantity() - event.getQuantity());
    }
}
