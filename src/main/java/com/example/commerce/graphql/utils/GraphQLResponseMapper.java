package com.example.commerce.graphql.utils;

import com.example.commerce.dtos.responses.GraphQLPageInfo;
import com.example.commerce.dtos.responses.GraphQLPagedResponse;
import com.example.commerce.dtos.responses.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

/**
 * Utility class for mapping responses to GraphQL format
 */
@Component
public class GraphQLResponseMapper {
    
    /**
     * Convert Spring Data Page to GraphQL PagedResponse
     */
    public <T> GraphQLPagedResponse<T> toGraphQLPagedResponse(Page<T> page) {
        GraphQLPageInfo pageInfo = new GraphQLPageInfo(
            page.getNumber(),
            (int) page.getTotalElements(),
            page.getTotalPages(),
            page.isLast(),
            page.hasNext(),
            page.hasPrevious()
        );
        return GraphQLPagedResponse.of(page.getContent(), pageInfo);
    }
    
    /**
     * Convert custom PagedResponse to GraphQL PagedResponse
     */
    public <T> GraphQLPagedResponse<T> toGraphQLPagedResponse(PagedResponse<T> pagedResponse) {
        GraphQLPageInfo pageInfo = new GraphQLPageInfo(
            pagedResponse.currentPage(),
            pagedResponse.totalItems(),
            pagedResponse.totalPages(),
            pagedResponse.isLast(),
            !pagedResponse.isLast(),
            pagedResponse.currentPage() > 0
        );
        return GraphQLPagedResponse.of(pagedResponse.content(), pageInfo);
    }
}
