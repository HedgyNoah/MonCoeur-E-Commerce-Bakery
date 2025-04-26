package fishdicg.moncoeur.product_service.mapper;

import fishdicg.moncoeur.product_service.dto.request.ProductRequest;
import fishdicg.moncoeur.product_service.dto.response.ProductResponse;
import fishdicg.moncoeur.product_service.entity.Category;
import fishdicg.moncoeur.product_service.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "category", ignore = true)
    Product toProduct(ProductRequest request);

    @Mapping(source = "category", target = "category", qualifiedByName = "toString")
    ProductResponse toProductResponse(Product product);

    @Named("toString")
    public static String toString(Category category) {
        return category.getCategoryTitle();
    }

    @Mapping(target = "category", ignore = true)
    void updateProduct(@MappingTarget Product product, ProductRequest request);
}
