package com.example.Commerce.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Commerce Smart API",
        version = "1.0.0",
        description = """
            # Commerce Smart E-Commerce API
            
            This API provides comprehensive e-commerce functionality including:
            - User authentication and registration
            - Product catalog management
            - Shopping cart and checkout
            - Order processing and tracking
            - Inventory management
            - Category management
            
            ## Authentication
            Most endpoints require authentication via Bearer token in the Authorization header.
            Format: `Authorization: Bearer <token>`
            
            ## Error Handling
            The API uses standard HTTP status codes and returns consistent error responses:
            
            ### Common Error Responses:
            - **400 Bad Request**: Validation errors or malformed request body
            - **401 Unauthorized**: Missing or invalid authentication token
            - **403 Forbidden**: User lacks required permissions/role
            - **404 Not Found**: Resource not found
            - **409 Conflict**: Resource already exists or constraint violation
            - **500 Internal Server Error**: Unexpected server error
            
            ### Error Response Structure:
            
            **Standard Error** (404, 401, 403, 500):
            ```json
            {
              "timestamp": "2026-01-19T10:30:00.000+00:00",
              "status": 404,
              "message": "Product not found with id: 123",
              "path": "uri=/api/products/123"
            }
            ```
            
            **Validation Error** (400):
            ```json
            {
              "timestamp": "2026-01-19T10:30:00.000+00:00",
              "status": 400,
              "errors": {
                "email": "must be a well-formed email address",
                "name": "must not be blank",
                "price": "must be greater than 0"
              },
              "path": "uri=/api/products/add"
            }
            ```
            
            ## Roles and Permissions
            - **CUSTOMER**: Can browse products, create orders, view own orders
            - **ADMIN**: Full access to all endpoints including management operations
            """,
        contact = @Contact(
            name = "Commerce Smart API Support",
            email = "support@commercesmart.com"
        )
    ),
    servers = {
        @Server(url = "http://localhost:8080", description = "Local Development Server"),
        @Server(url = "https://api.commercesmart.com", description = "Production Server")
    }
)
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Components components = new Components();
        
        components.addResponses("BadRequest", new io.swagger.v3.oas.models.responses.ApiResponse()
                .description("Bad Request - Validation error or malformed request")
                .content(new Content()
                        .addMediaType("application/json", new MediaType()
                                .schema(new Schema<>().$ref("#/components/schemas/ValidationErrorResponse")))));
        
        components.addResponses("Unauthorized", new io.swagger.v3.oas.models.responses.ApiResponse()
                .description("Unauthorized - Invalid or missing authentication token")
                .content(new Content()
                        .addMediaType("application/json", new MediaType()
                                .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse")))));
        
        components.addResponses("Forbidden", new io.swagger.v3.oas.models.responses.ApiResponse()
                .description("Forbidden - User does not have required role")
                .content(new Content()
                        .addMediaType("application/json", new MediaType()
                                .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse")))));
        
        components.addResponses("NotFound", new io.swagger.v3.oas.models.responses.ApiResponse()
                .description("Not Found - Resource not found")
                .content(new Content()
                        .addMediaType("application/json", new MediaType()
                                .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse")))));
        
        components.addResponses("Conflict", new io.swagger.v3.oas.models.responses.ApiResponse()
                .description("Conflict - Resource already exists or constraint violation")
                .content(new Content()
                        .addMediaType("application/json", new MediaType()
                                .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse")))));
        
        components.addResponses("InternalServerError", new io.swagger.v3.oas.models.responses.ApiResponse()
                .description("Internal Server Error - Unexpected error occurred")
                .content(new Content()
                        .addMediaType("application/json", new MediaType()
                                .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse")))));
        
        return new OpenAPI().components(components);
    }
}
