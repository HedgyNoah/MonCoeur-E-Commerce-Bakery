package fishdicg.moncoeur.inventory_service.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageResponse<T> {
    int currentPage;
    int pageSize;
    int totalPage;
    long totalElements;
    String sortBy;
    String order;
    String search;

    @Builder.Default
    List<T> data = Collections.emptyList();
}
