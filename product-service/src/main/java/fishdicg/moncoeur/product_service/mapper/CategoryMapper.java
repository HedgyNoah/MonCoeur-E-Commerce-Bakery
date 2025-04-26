package fishdicg.moncoeur.product_service.mapper;

import fishdicg.moncoeur.product_service.dto.request.CategoryRequest;
import fishdicg.moncoeur.product_service.dto.response.CategoryResponse;
import fishdicg.moncoeur.product_service.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "products", ignore = true)
    Category toCategory(CategoryRequest request);

    CategoryResponse toCategoryResponse(Category category);

    @Mapping(target = "products", ignore = true)
    void updateCategory(@MappingTarget Category category, CategoryRequest request);


}
