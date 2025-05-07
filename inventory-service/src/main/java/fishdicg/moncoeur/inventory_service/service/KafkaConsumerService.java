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

    @KafkaListener(topics = "create-product-topic")
    public void createProduct(CreateProductEvent event) {
        try {
            inventoryService.create(InventoryRequest.builder()
                    .productId(event.getProductId())
                    .quantity(event.getQuantity())
                    .lowStockThreshold(event.getLowStockThreshold()).build());
        } catch (Exception e) {
            log.error("Error create product", e);
        }
    }

    @KafkaListener(topics = "update-product-topic")
    public void updateProduct(UpdateProductEvent event) {
        try {
            inventoryService.update(event.getProductId(), InventoryRequest.builder()
                    .productId(event.getProductId())
                    .quantity(event.getQuantity())
                    .lowStockThreshold(event.getLowStockThreshold()).build());
        } catch (Exception e) {
            log.error("Error update product", e);
        }
    }

    @KafkaListener(topics = "delete-item-topic")
    public void deleteItem(DeleteItemEvent event) {
        try {
            inventoryService.delete(event.getProductId());
        } catch (Exception e) {
            log.error("Error delete product", e);
        }
    }

    @KafkaListener(topics = "decrease-stock-topic")
    public void decreaseStock(DecreaseStockEvent event) {
        try {
            Inventory inventory = inventoryRepository.findByProductId(event.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_EXISTED));
            inventory.setQuantity(inventory.getQuantity() - event.getQuantity());
            inventory.setSold(inventory.getSold() + event.getQuantity());
            inventoryRepository.save(inventory);
            log.info("Quantity after get decreased: {}", inventory.getQuantity() - event.getQuantity());
        } catch (Exception e) {
            log.error("Error decrease stock", e);
        }
    }
}
