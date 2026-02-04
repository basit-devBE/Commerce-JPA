# GraphQL Error Handling Implementation

## Overview
This document explains the comprehensive error handling implementation for GraphQL operations in the Commerce application.

## Backend Implementation

### 1. GraphQL Exception Handler
**File:** `src/main/java/com/example/commerce/errorhandlers/GraphQLExceptionHandler.java`

The `GraphQLExceptionHandler` class extends `DataFetcherExceptionResolverAdapter` to intercept exceptions thrown during GraphQL operations and convert them into proper GraphQL errors.

**Supported Error Types:**
- `ResourceNotFoundException` → `NOT_FOUND`
- `ResourceAlreadyExists` → `BAD_REQUEST`
- `ConstraintViolationException` → `BAD_REQUEST` (e.g., foreign key violations)
- `UnauthorizedException` → `UNAUTHORIZED`
- `IllegalArgumentException` → `BAD_REQUEST`
- All other exceptions → `INTERNAL_ERROR`

**Error Response Format:**
```json
{
  "errors": [
    {
      "message": "Cannot delete category. It is being used by one or more products.",
      "locations": [{"line": 3, "column": 5}],
      "path": ["deleteCategory"],
      "extensions": {
        "classification": "BAD_REQUEST"
      }
    }
  ],
  "data": null
}
```

### 2. Example Usage
When a user tries to delete a category that has associated products:

**Before (Generic Error):**
```json
{
  "errors": [{
    "message": "INTERNAL_ERROR for b50c5c1c-6605-5a15-a990-2e2935942ee1",
    "extensions": {
      "classification": "INTERNAL_ERROR"
    }
  }]
}
```

**After (Meaningful Error):**
```json
{
  "errors": [{
    "message": "Cannot delete category. It is being used by one or more products. Please remove or reassign the products first.",
    "path": ["deleteCategory"],
    "extensions": {
      "classification": "BAD_REQUEST"
    }
  }]
}
```

## Frontend Implementation

### 1. GraphQL API Service
**File:** `frontend/src/services/graphqlApi.js`

Enhanced response interceptor that:
- Detects GraphQL errors in the response
- Extracts meaningful error messages
- Removes UUID from generic INTERNAL_ERROR messages
- Attaches GraphQL error metadata to the error object

### 2. Error Handler Utility
**File:** `frontend/src/utils/errorHandler.js`

The `getErrorMessage()` function now supports both REST and GraphQL errors:
- Detects GraphQL error structure
- Maps error classifications to user-friendly messages
- Cleans up generic INTERNAL_ERROR messages
- Provides consistent error handling across the application

**Example Usage:**
```javascript
try {
  await graphqlAPI.deleteCategory(categoryId);
} catch (err) {
  const message = getErrorMessage(err);
  setError(message);
  // User sees: "Cannot delete category. It is being used by one or more products."
}
```

### 3. Admin Dashboard
**File:** `frontend/src/pages/AdminDashboardGraphQL.js`

Updated `executeGraphQL()` function to:
- Parse GraphQL error responses
- Extract error metadata (path, extensions)
- Create enhanced error objects with context
- Display errors using the ErrorAlert component

## Error Classifications

| Classification | HTTP Equivalent | Use Case |
|---------------|-----------------|----------|
| `NOT_FOUND` | 404 | Resource doesn't exist |
| `BAD_REQUEST` | 400 | Invalid input or constraint violation |
| `UNAUTHORIZED` | 401 | Authentication/authorization failure |
| `INTERNAL_ERROR` | 500 | Unexpected server error |

## Testing the Implementation

### Test Case 1: Delete Category with Products
1. Create a category (e.g., "Electronics")
2. Create a product associated with that category
3. Try to delete the category
4. **Expected Result:** Error message "Cannot delete category. It is being used by one or more products. Please remove or reassign the products first."

### Test Case 2: Delete Non-Existent Category
1. Try to delete a category with ID 99999
2. **Expected Result:** Error message "Category not found with ID: 99999"

### Test Case 3: Create Duplicate Category
1. Create a category with name "Books"
2. Try to create another category with name "Books" (case-insensitive)
3. **Expected Result:** Error message "Category with name 'Books' already exists"

## Benefits

1. **User-Friendly Errors:** Users see meaningful messages instead of generic error codes
2. **Debugging:** Error paths and extensions help developers identify issues quickly
3. **Consistent Handling:** Both REST and GraphQL errors are handled uniformly
4. **Security:** Internal error details are sanitized before being sent to the client
5. **Maintainability:** Centralized error handling makes it easy to add new error types

## Configuration

No additional configuration is required. The GraphQL exception handler is automatically discovered by Spring Boot's component scanning.

To customize error messages, modify the exception classes in:
- `src/main/java/com/example/commerce/errorhandlers/`
- `src/main/java/com/example/commerce/services/` (service layer exceptions)

## Future Enhancements

1. **Localization:** Support for multiple languages in error messages
2. **Error Codes:** Add structured error codes for programmatic error handling
3. **Logging:** Integrate with logging frameworks to track error patterns
4. **Retry Logic:** Implement automatic retry for transient errors
5. **Error Analytics:** Track and analyze common error scenarios
