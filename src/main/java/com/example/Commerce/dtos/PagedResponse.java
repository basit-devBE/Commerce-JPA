package com.example.Commerce.dtos;

import java.util.List;

public record PagedResponse<T>(
    List<T> content,
    int currentPage,
    int totalItems,
    int totalPages,
    boolean isLast
    ){}


