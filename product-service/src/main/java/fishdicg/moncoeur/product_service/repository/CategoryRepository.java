package fishdicg.moncoeur.product_service.repository;

import fishdicg.moncoeur.product_service.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    Optional<Category> findByCategoryTitle(String categoryTitle);

    Page<Category> findByCategoryTitleContainingIgnoreCase(String categoryTitle, Pageable pageable);
}
