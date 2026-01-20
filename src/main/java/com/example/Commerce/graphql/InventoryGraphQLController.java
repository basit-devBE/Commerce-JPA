package com.example.Commerce.graphql;

import com.example.Commerce.DTOs.AddInventoryDTO;
import com.example.Commerce.DTOs.InventoryResponseDTO;
import com.example.Commerce.DTOs.UpdateInventoryDTO;
import com.example.Commerce.Services.InventoryService;
import org.springframework.data.domain.Pageable;
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

    public record AddInventoryInput(Long productId, Integer quantity, String location) {}
    public record UpdateInventoryInput(Integer quantity, String location) {}
}
