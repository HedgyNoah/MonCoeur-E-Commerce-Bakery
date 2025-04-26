package fishdicg.moncoeur.favourite_service.controller;

import fishdicg.moncoeur.favourite_service.dto.request.FavouriteRequest;
import fishdicg.moncoeur.favourite_service.dto.response.FavouriteResponse;
import fishdicg.moncoeur.favourite_service.service.FavouriteService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manage")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FavouriteController {
    FavouriteService favouriteService;

    @PostMapping
    ResponseEntity<FavouriteResponse> createFavourite(@RequestBody FavouriteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(favouriteService.create(request));
    }

    @GetMapping
    ResponseEntity<List<FavouriteResponse>> getAllFavourites() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(favouriteService.getAll());
    }

    @GetMapping("/{id}")
    ResponseEntity<FavouriteResponse> getFavourite(@PathVariable String id) {
        return new ResponseEntity<>(favouriteService.get(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<String> deleteFavourite(@PathVariable String id) {
        favouriteService.delete(id);
        return new ResponseEntity<>("Favourite item has been removed.", HttpStatus.OK);
    }
}
