package com.example.commerce.graphql;

import com.example.commerce.dtos.requests.AddInventoryDTO;
import com.example.commerce.dtos.requests.UpdateInventoryDTO;
import com.example.commerce.dtos.responses.GraphQLPagedResponse;
import com.example.commerce.dtos.responses.InventoryResponseDTO;
import com.example.commerce.graphql.input.InventoryInput.AddInventoryInput;
import com.example.commerce.graphql.input.InventoryInput.UpdateInventoryInput;
import com.example.commerce.graphql.input.PaginationInput;
import com.example.commerce.graphql.utils.GraphQLResponseMapper;
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
    private final GraphQLResponseMapper responseMapper;

    public InventoryGraphQLController(InventoryService inventoryService, GraphQLResponseMapper responseMapper) {
        this.inventoryService = inventoryService;
        this.responseMapper = responseMapper;
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
    public GraphQLPagedResponse<InventoryResponseDTO> inventoriesPaginated(@Argument PaginationInput pagination, @Argument String search) {
        // Handle null pagination with defaults
        if (pagination == null) {
            pagination = new PaginationInput(0, 10, "id", "ASC");
        }
        
        int page = pagination.getPage();
        int size = pagination.getSize();
        String sortBy = pagination.getSortBy();
        String sortDir = pagination.getSortDirection();
        
        Sort sort = sortDir.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<InventoryResponseDTO> inventoriesPage;
        if (search != null && !search.isBlank()) {
            inventoriesPage = inventoryService.searchInventory(search, pageable);
        } else {
            inventoriesPage = inventoryService.getAllInventories(pageable);
        }
        return responseMapper.toGraphQLPagedResponse(inventoriesPage);
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
}

