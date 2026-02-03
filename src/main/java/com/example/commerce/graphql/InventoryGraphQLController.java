package com.example.commerce.graphql;

import com.example.commerce.dtos.requests.AddInventoryDTO;
import com.example.commerce.dtos.requests.UpdateInventoryDTO;
import com.example.commerce.dtos.responses.GraphQLPageInfo;
import com.example.commerce.dtos.responses.GraphQLPagedResponse;
import com.example.commerce.dtos.responses.InventoryResponseDTO;
import com.example.commerce.services.InventoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class InventoryGraphQLController {
    private final InventoryService inventoryService;

    public InventoryGraphQLController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // ==================== QUERIES ====================

    @QueryMapping
    public List<InventoryResponseDTO> allInventories() {
        return inventoryService.getAllInventories(Pageable.unpaged()).getContent();
    }

    @QueryMapping
    public InventoryResponseDTO inventoryById(@Argument Long id) {
        return inventoryService.getInventoryById(id);
    }

    @QueryMapping
    public InventoryResponseDTO inventoryByProductId(@Argument Long productId) {
        return inventoryService.getInventoryByProductId(productId);
    }

    @QueryMapping
    public GraphQLPagedResponse<InventoryResponseDTO> inventoriesPaginated(@Argument PaginationInput pagination) {
        int page = pagination != null && pagination.page() != null ? pagination.page() : 0;
        int size = pagination != null && pagination.size() != null ? pagination.size() : 10;
        String sortBy = pagination != null && pagination.sortBy() != null ? pagination.sortBy() : "id";
        String sortDir = pagination != null && pagination.sortDirection() != null ? pagination.sortDirection() : "ASC";
        
        Sort sort = sortDir.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<InventoryResponseDTO> inventoriesPage = inventoryService.getAllInventories(pageable);
        return toGraphQLPagedResponse(inventoriesPage);
    }

    // ==================== MUTATIONS ====================

    @MutationMapping
    public InventoryResponseDTO addInventory(@Argument AddInventoryInput input) {
        AddInventoryDTO dto = new AddInventoryDTO();
        dto.setProductId(input.productId());
        dto.setQuantity(input.quantity());
        dto.setLocation(input.location());
        return inventoryService.addInventory(dto);
    }

    @MutationMapping
    public InventoryResponseDTO updateInventory(@Argument Long id, @Argument UpdateInventoryInput input) {
        UpdateInventoryDTO dto = new UpdateInventoryDTO();
        dto.setQuantity(input.quantity());
        dto.setLocation(input.location());
        return inventoryService.updateInventory(id, dto);
    }

    @MutationMapping
    public boolean deleteInventory(@Argument Long id) {
        inventoryService.deleteInventory(id);
        return true;
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

    public record AddInventoryInput(Long productId, Integer quantity, String location) {}
    public record UpdateInventoryInput(Integer quantity, String location) {}
    public record PaginationInput(Integer page, Integer size, String sortBy, String sortDirection) {}
}
