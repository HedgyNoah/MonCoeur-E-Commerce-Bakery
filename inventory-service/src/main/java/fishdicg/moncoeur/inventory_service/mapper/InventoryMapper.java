package fishdicg.moncoeur.inventory_service.mapper;

import fishdicg.moncoeur.inventory_service.dto.request.InventoryRequest;
import fishdicg.moncoeur.inventory_service.dto.response.InventoryResponse;
import fishdicg.moncoeur.inventory_service.entity.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface InventoryMapper {
    Inventory toInventory(InventoryRequest request);

    InventoryResponse toInventoryResponse(Inventory inventory);

    void updateInventory(@MappingTarget Inventory inventory, InventoryRequest request);
}
