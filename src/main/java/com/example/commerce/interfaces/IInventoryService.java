package com.example.commerce.interfaces;

import com.example.commerce.dtos.requests.AddInventoryDTO;
import com.example.commerce.dtos.requests.UpdateInventoryDTO;
import com.example.commerce.dtos.responses.InventoryResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IInventoryService {
    InventoryResponseDTO addInventory(AddInventoryDTO addInventoryDTO);

    Page<InventoryResponseDTO> getAllInventories(Pageable pageable);

    InventoryResponseDTO getInventoryById(Long id);

    InventoryResponseDTO getInventoryByProductId(Long productId);

    InventoryResponseDTO updateInventory(Long id, UpdateInventoryDTO updateInventoryDTO);

    InventoryResponseDTO adjustInventoryQuantity(Long id, Integer quantityChange);

    void deleteInventory(Long id);
}
