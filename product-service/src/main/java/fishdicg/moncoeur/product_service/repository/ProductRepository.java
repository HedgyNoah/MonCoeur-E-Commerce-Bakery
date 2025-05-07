package fishdicg.moncoeur.product_service.repository;

import fishdicg.moncoeur.product_service.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    boolean existsBySlug(String slug);

    Page<Product> findByProductNameContaining(String productName, Pageable pageable);

    Page<Product> findByCategory_CategoryTitle(String categoryTitle, Pageable pageable);

    Page<Product> findByCategory_CategoryTitleAndProductNameContainingIgnoreCase(
            String categoryTitle, String search, Pageable pageable);
}
