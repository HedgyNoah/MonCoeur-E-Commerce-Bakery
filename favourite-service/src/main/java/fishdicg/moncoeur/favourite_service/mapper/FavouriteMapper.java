package fishdicg.moncoeur.favourite_service.mapper;

import fishdicg.moncoeur.favourite_service.dto.request.FavouriteRequest;
import fishdicg.moncoeur.favourite_service.dto.response.FavouriteResponse;
import fishdicg.moncoeur.favourite_service.entity.Favourite;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FavouriteMapper {
    Favourite toFavourite(FavouriteRequest request);

    FavouriteResponse toFavouriteResponse(Favourite favourite);
}
