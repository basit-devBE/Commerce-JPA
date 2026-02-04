package com.example.commerce.graphql.input;

/**
 * Common pagination input for GraphQL queries
 */
public record PaginationInput(
    Integer page,
    Integer size,
    String sortBy,
    String sortDirection
) {
    public PaginationInput {
        // Default values
        if (page == null || page < 0) {
            page = 0;
        }
        if (size == null || size < 1) {
            size = 10;
        }
        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "id";
        }
        if (sortDirection == null || sortDirection.isBlank()) {
            sortDirection = "ASC";
        }
    }
    
    public int getPage() {
        return page != null ? page : 0;
    }
    
    public int getSize() {
        return size != null ? size : 10;
    }
    
    public String getSortBy() {
        return sortBy != null && !sortBy.isBlank() ? sortBy : "id";
    }
    
    public String getSortDirection() {
        return sortDirection != null && !sortDirection.isBlank() ? sortDirection : "ASC";
    }
}
