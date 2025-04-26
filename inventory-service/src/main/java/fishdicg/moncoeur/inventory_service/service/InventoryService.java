package fishdicg.moncoeur.inventory_service.service;

import fishdicg.moncoeur.inventory_service.dto.PageResponse;
import fishdicg.moncoeur.inventory_service.dto.request.InventoryRequest;
import fishdicg.moncoeur.inventory_service.dto.response.InventoryResponse;
import fishdicg.moncoeur.inventory_service.entity.Inventory;
import fishdicg.moncoeur.inventory_service.exception.AppException;
import fishdicg.moncoeur.inventory_service.exception.ErrorCode;
import fishdicg.moncoeur.inventory_service.mapper.InventoryMapper;
import fishdicg.moncoeur.inventory_service.repository.InventoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class InventoryService {
    InventoryRepository inventoryRepository;
    InventoryMapper inventoryMapper;

    public InventoryResponse create(InventoryRequest request) {
        Inventory inventory = inventoryMapper.toInventory(request);
        inventory.setRestockDate(Instant.now());
        return inventoryMapper.toInventoryResponse(inventory);
    }

    public PageResponse<InventoryResponse> getAll(int page, int size, String sortBy,
                                                  String order, String search) {
        Sort sort = "asc".equalsIgnoreCase(order) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<Inventory> pageData;
        if(search != null && !search.isEmpty())
            pageData = inventoryRepository.findAllByProductIdContaining(search, pageable);
        else
            pageData = inventoryRepository.findAll(pageable);
        List<InventoryResponse> inventoryResponseList = pageData.getContent().stream()
                .map(inventoryMapper::toInventoryResponse).toList();

        return PageResponse.<InventoryResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPage(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .order(order)
                .sortBy(sortBy)
                .search(search)
                .data(inventoryResponseList)
                .build();
    }

    public InventoryResponse getOne(String id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_EXISTED));

        return inventoryMapper.toInventoryResponse(inventory);
    }

    public InventoryResponse update(String productId, InventoryRequest request) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_EXISTED));

        inventoryMapper.updateInventory(inventory, request);
        return inventoryMapper.toInventoryResponse(inventoryRepository.save(inventory));
    }

    public void delete(String productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_EXISTED));
        inventoryRepository.delete(inventory);
    }
}
