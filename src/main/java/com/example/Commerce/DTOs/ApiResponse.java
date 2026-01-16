package com.example.Commerce.DTOs;

public record ApiResponse<T>(int status, String message, T data) {
}
