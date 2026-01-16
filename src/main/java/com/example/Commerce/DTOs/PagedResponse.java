package com.example.Commerce.DTOs;

import java.util.List;

public record PagedResponse<T>(
    List<T> content,
    int currentPage,
    int totalItems,
    int totalPages,
    boolean isLast
    ){}


