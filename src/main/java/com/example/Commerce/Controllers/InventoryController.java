package com.example.Commerce.Controllers;

import com.example.Commerce.Config.RequiresRole;
import com.example.Commerce.DTOs.AddInventoryDTO;
import com.example.Commerce.DTOs.ApiResponse;
import com.example.Commerce.DTOs.InventoryResponseDTO;
import com.example.Commerce.DTOs.PagedResponse;
import com.example.Commerce.DTOs.UpdateInventoryDTO;
import com.example.Commerce.Enums.UserRole;
import com.example.Commerce.Services.InventoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @RequiresRole(UserRole.ADMIN)
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<InventoryResponseDTO>> addInventory(@Valid @RequestBody AddInventoryDTO request) {
        InventoryResponseDTO inventory = inventoryService.addInventory(request);
        ApiResponse<InventoryResponseDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "Inventory added successfully", inventory);
        return ResponseEntity.ok(apiResponse);
    }

    @RequiresRole(UserRole.ADMIN)
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<PagedResponse<InventoryResponseDTO>>> getAllInventories(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<InventoryResponseDTO> inventories = inventoryService.getAllInventories(pageable);
        PagedResponse<InventoryResponseDTO> pagedResponse = new PagedResponse<>(
                inventories.getContent(),
                inventories.getNumber(),
                (int) inventories.getTotalElements(),
                inventories.getTotalPages(),
                inventories.isLast()
        );
        ApiResponse<PagedResponse<InventoryResponseDTO>> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "Inventories fetched successfully", pagedResponse);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InventoryResponseDTO>> getInventoryById(@PathVariable Long id) {
        InventoryResponseDTO inventory = inventoryService.getInventoryById(id);
        ApiResponse<InventoryResponseDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "Inventory fetched successfully", inventory);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<InventoryResponseDTO>> getInventoryByProductId(@PathVariable Long productId) {
        InventoryResponseDTO inventory = inventoryService.getInventoryByProductId(productId);
        ApiResponse<InventoryResponseDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "Inventory fetched successfully", inventory);
        return ResponseEntity.ok(apiResponse);
    }

    @RequiresRole(UserRole.ADMIN)
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<InventoryResponseDTO>> updateInventory(
            @PathVariable Long id,
            @Valid @RequestBody UpdateInventoryDTO request) {
        InventoryResponseDTO updatedInventory = inventoryService.updateInventory(id, request);
        ApiResponse<InventoryResponseDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "Inventory updated successfully", updatedInventory);
        return ResponseEntity.ok(apiResponse);
    }

    @RequiresRole(UserRole.ADMIN)
    @PatchMapping("/adjust/{id}")
    public ResponseEntity<ApiResponse<InventoryResponseDTO>> adjustInventoryQuantity(
            @PathVariable Long id,
            @RequestParam Integer quantityChange) {
        InventoryResponseDTO adjustedInventory = inventoryService.adjustInventoryQuantity(id, quantityChange);
        ApiResponse<InventoryResponseDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "Inventory quantity adjusted successfully", adjustedInventory);
        return ResponseEntity.ok(apiResponse);
    }

    @RequiresRole(UserRole.ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
        ApiResponse<Void> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "Inventory deleted successfully", null);
        return ResponseEntity.ok(apiResponse);
    }
}
