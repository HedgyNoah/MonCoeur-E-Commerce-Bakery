package fishdicg.moncoeur.favourite_service.service;

import event.dto.DeleteItemEvent;
import fishdicg.moncoeur.favourite_service.dto.request.FavouriteRequest;
import fishdicg.moncoeur.favourite_service.dto.response.FavouriteResponse;
import fishdicg.moncoeur.favourite_service.entity.Favourite;
import fishdicg.moncoeur.favourite_service.exception.AppException;
import fishdicg.moncoeur.favourite_service.exception.ErrorCode;
import fishdicg.moncoeur.favourite_service.mapper.FavouriteMapper;
import fishdicg.moncoeur.favourite_service.repository.FavouriteRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FavouriteService {
    FavouriteRepository favouriteRepository;
    FavouriteMapper favouriteMapper;

    public FavouriteResponse create(FavouriteRequest request) {
        Favourite favourite = favouriteMapper.toFavourite(request);
        return favouriteMapper.toFavouriteResponse(
                favouriteRepository.save(favourite));
    }

    public FavouriteResponse get(String id) {
        Favourite favourite = favouriteRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.FAVOURITE_NOT_EXISTED));
        return favouriteMapper.toFavouriteResponse(favourite);
    }

    public List<FavouriteResponse> getAll() {
        var id = SecurityContextHolder.getContext().getAuthentication().getName();
        return favouriteRepository.findAllByUserId(id).stream()
                .map(favouriteMapper::toFavouriteResponse).toList();
    }

    public void delete(String id) {
        favouriteRepository.deleteById(id);
    }

    @KafkaListener(topics = "delete-item-topic", groupId = "favourite-service-group")
    public void deleteItem(DeleteItemEvent event) {
        Favourite favourite = favouriteRepository.findByProductId(event.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.FAVOURITE_NOT_EXISTED));
        favouriteRepository.delete(favourite);
    }
}
