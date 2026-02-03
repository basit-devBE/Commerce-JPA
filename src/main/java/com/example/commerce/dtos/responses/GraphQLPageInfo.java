package com.example.commerce.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GraphQLPageInfo {
    private int currentPage;
    private int totalItems;
    private int totalPages;
    private boolean isLast;
    private boolean hasNext;
    private boolean hasPrevious;

    public static GraphQLPageInfo from(org.springframework.data.domain.Page<?> page) {
        return new GraphQLPageInfo(
            page.getNumber(),
            (int) page.getTotalElements(),
            page.getTotalPages(),
            page.isLast(),
            page.hasNext(),
            page.hasPrevious()
        );
    }
}
