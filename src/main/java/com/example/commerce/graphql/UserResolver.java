package com.example.commerce.graphql;

import com.example.commerce.dtos.responses.GraphQLPageInfo;
import com.example.commerce.dtos.responses.GraphQLPagedResponse;
import com.example.commerce.dtos.responses.userSummaryDTO;
import com.example.commerce.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class UserResolver {
    private final UserService userService;

    public UserResolver(UserService userService) {
        this.userService = userService;
    }

    // ==================== QUERIES ====================

    @QueryMapping
    public List<userSummaryDTO> getAllUsers() {
        return userService.getAllUsersList();
    }

    @QueryMapping
    public userSummaryDTO getUserById(@Argument Long id) {
        return userService.findUserById(id);
    }

    @QueryMapping
    public GraphQLPagedResponse<userSummaryDTO> usersPaginated(@Argument PaginationInput pagination, @Argument String search) {
        int page = pagination != null && pagination.page() != null ? pagination.page() : 0;
        int size = pagination != null && pagination.size() != null ? pagination.size() : 10;
        String sortBy = pagination != null && pagination.sortBy() != null ? pagination.sortBy() : "id";
        String sortDir = pagination != null && pagination.sortDirection() != null ? pagination.sortDirection() : "ASC";
        
        Sort sort = sortDir.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<userSummaryDTO> usersPage;
        if (search != null && !search.isBlank()) {
            usersPage = userService.searchUsers(search, pageable);
        } else {
            usersPage = userService.getAllUsers(pageable);
        }
        return toGraphQLPagedResponse(usersPage);
    }

    // ==================== HELPER METHODS ====================

    private <T> GraphQLPagedResponse<T> toGraphQLPagedResponse(Page<T> page) {
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

    // ==================== INPUT RECORDS ====================

    public record PaginationInput(Integer page, Integer size, String sortBy, String sortDirection) {}
}
