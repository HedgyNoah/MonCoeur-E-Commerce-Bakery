package fishdicg.moncoeur.favourite_service.repository;

import fishdicg.moncoeur.favourite_service.entity.Favourite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavouriteRepository extends JpaRepository<Favourite, String> {
    List<Favourite> findAllByUserId(String userId);

    Optional<Favourite> findByProductId(String productId);
}
