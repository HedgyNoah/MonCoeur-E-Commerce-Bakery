package fishdicg.moncoeur.inventory_service.repository;

import fishdicg.moncoeur.inventory_service.entity.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, String> {
    Page<Inventory> findAllByProductIdContaining(String productId, Pageable pageable);

    Optional<Inventory> findByProductId(String productId);
}
