package com.example.Commerce.utils.sorting;

import com.example.Commerce.DTOs.ProductResponseDTO;
import com.example.Commerce.DTOs.OrderResponseDTO;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class SortingService {
    
    public enum SortAlgorithm {
        QUICKSORT, MERGESORT
    }
    
    public enum ProductSortField {
        NAME, PRICE, QUANTITY
    }
    
    public enum OrderSortField {
        TOTAL_AMOUNT, CREATED_AT
    }
    
    public void sortProducts(List<ProductResponseDTO> products, ProductSortField field, 
                            boolean ascending, SortAlgorithm algorithm) {
        Comparator<ProductResponseDTO> comparator = getProductComparator(field, ascending);
        
        if (algorithm == SortAlgorithm.QUICKSORT) {
            QuickSort.sort(products, comparator);
        } else {
            MergeSort.sort(products, comparator);
        }
    }
    
    public void sortOrders(List<OrderResponseDTO> orders, OrderSortField field, 
                          boolean ascending, SortAlgorithm algorithm) {
        Comparator<OrderResponseDTO> comparator = getOrderComparator(field, ascending);
        
        if (algorithm == SortAlgorithm.QUICKSORT) {
            QuickSort.sort(orders, comparator);
        } else {
            MergeSort.sort(orders, comparator);
        }
    }
    
    private Comparator<ProductResponseDTO> getProductComparator(ProductSortField field, boolean ascending) {
        Comparator<ProductResponseDTO> comparator = switch (field) {
            case NAME -> Comparator.comparing(ProductResponseDTO::getName);
            case PRICE -> Comparator.comparing(ProductResponseDTO::getPrice);
            case QUANTITY -> Comparator.comparing(p -> p.getQuantity() != null ? p.getQuantity() : 0);
        };
        return ascending ? comparator : comparator.reversed();
    }
    
    private Comparator<OrderResponseDTO> getOrderComparator(OrderSortField field, boolean ascending) {
        Comparator<OrderResponseDTO> comparator = switch (field) {
            case TOTAL_AMOUNT -> Comparator.comparing(OrderResponseDTO::getTotalAmount);
            case CREATED_AT -> Comparator.comparing(OrderResponseDTO::getCreatedAt);
        };
        return ascending ? comparator : comparator.reversed();
    }
}
