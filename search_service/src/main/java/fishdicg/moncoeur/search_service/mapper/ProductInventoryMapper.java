package fishdicg.moncoeur.search_service.mapper;

import event.dto.CreateProductEvent;
import event.dto.UpdateProductEvent;
import fishdicg.moncoeur.search_service.dto.ProductInventoryResponse;
import fishdicg.moncoeur.search_service.entity.ProductInventoryDocument;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductInventoryMapper {
    ProductInventoryDocument toProductInventoryDocument(CreateProductEvent createProductEvent);

    ProductInventoryResponse toProductInventoryResponse(ProductInventoryDocument productInventoryDocument);

    void updateProductInventoryDocument(
            @MappingTarget ProductInventoryDocument productInventoryDocument,
            UpdateProductEvent updateProductEvent);
}
