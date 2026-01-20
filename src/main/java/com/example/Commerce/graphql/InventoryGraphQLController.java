package com.example.Commerce.graphql;

import com.example.Commerce.DTOs.InventoryResponseDTO;
import com.example.Commerce.Services.InventoryService;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class InventoryGraphQLController {
    private final InventoryService inventoryService;

    public InventoryGraphQLController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

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
}
