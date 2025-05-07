package fishdicg.moncoeur.search_service.repository;

import fishdicg.moncoeur.search_service.entity.ProductInventoryDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductInventoryRepository extends ElasticsearchRepository<ProductInventoryDocument, String> {
    Optional<ProductInventoryDocument> findBySlug(String slug);

    Page<ProductInventoryDocument> findByProductNameContainingIgnoreCase (String productName, Pageable pageable);

    Page<ProductInventoryDocument> findByCategory(String category, Pageable pageable);

    Page<ProductInventoryDocument> findByCategoryAndProductNameContainingIgnoreCase(
            String category, String search, Pageable pageable);
}
