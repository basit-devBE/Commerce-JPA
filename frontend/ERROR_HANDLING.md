# Error Handling in Frontend

This document describes how errors from the backend API are handled and displayed in the frontend application.

## Backend Error Response Structure

The backend returns errors in the following standardized format:

```json
{
  "timestamp": "2026-01-19T10:30:00.000+00:00",
  "status": 404,
  "message": "Product not found with id: 123",
  "path": "uri=/api/products/123"
}
```

## Frontend Error Handling Components

### 1. Error Utility Functions (`src/utils/errorHandler.js`)

**`getErrorMessage(error, defaultMessage)`**
- Extracts the error message from various error formats
- Returns a user-friendly error message
- Handles axios errors, string errors, and custom error formats

**`getErrorDetails(error)`**
- Extracts full error details including status, timestamp, and path
- Useful for debugging or detailed error displays

**`formatErrorForDisplay(error)`**
- Formats error with status code for user-friendly display
- Example: `[404] Product not found with id: 123`

### 2. ErrorAlert Component (`src/components/ErrorAlert.js`)

A reusable React component for displaying errors consistently across the application.

**Features:**
- Clean, accessible error display with icon
- Optional dismiss button
- Optional detailed error information (status, path)
- Consistent styling with Tailwind CSS

**Usage:**

```jsx
import ErrorAlert from '../components/ErrorAlert';

function MyComponent() {
  const [error, setError] = useState(null);
  
  return (
    <div>
      <ErrorAlert 
        error={error} 
        onDismiss={() => setError(null)}
        showDetails={false} // Optional: show status and path
      />
    </div>
  );
}
```

## Implementation Pattern

### Standard Error Handling Pattern

```jsx
import { useState } from 'react';
import ErrorAlert from '../components/ErrorAlert';
import { apiCall } from '../services/api';

function MyComponent() {
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleAction = async () => {
    setError(null);
    setLoading(true);

    try {
      await apiCall();
      // Success handling
    } catch (err) {
      setError(err); // Store the full error object
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <ErrorAlert error={error} onDismiss={() => setError(null)} />
      {/* Rest of component */}
    </div>
  );
}
```

## Updated Pages

The following pages have been updated with improved error handling:

1. **Login** (`src/pages/Login.js`)
   - Uses ErrorAlert component
   - Properly extracts error messages from backend ErrorResponse

2. **Register** (`src/pages/Register.js`)
   - Uses ErrorAlert component
   - Handles validation errors from backend

3. **Profile** (`src/pages/Profile.js`)
   - Separate error and success message handling
   - Uses ErrorAlert for errors
   - Custom success message display

4. **Checkout** (`src/pages/Checkout.js`)
   - Uses ErrorAlert component
   - Clear error messaging for order placement failures

5. **Admin Dashboard** (`src/pages/AdminDashboard.js`)
   - Replaced alert() calls with ErrorAlert component
   - Centralized error state management
   - Better error visibility for admin operations

## API Interceptor

The axios interceptor in `src/services/api.js` has been enhanced to:
- Log detailed error information for debugging
- Extract and log all ErrorResponse fields (message, status, path, timestamp)
- Handle 401 errors by redirecting to login

## Best Practices

1. **Always store the full error object** in state, not just the message
2. **Use ErrorAlert component** for consistent error display
3. **Clear errors** when starting new operations: `setError(null)`
4. **Provide dismiss functionality** to allow users to close error messages
5. **Log errors to console** for debugging while showing user-friendly messages
6. **Consider showing detailed errors** (status, path) in development mode

## Development vs Production

For development environments, you can enable detailed error information:

```jsx
<ErrorAlert 
  error={error} 
  onDismiss={() => setError(null)}
  showDetails={process.env.NODE_ENV === 'development'}
/>
```

This will show the HTTP status code and request path in development mode only.

## Common Error Scenarios

### 404 - Not Found
```
[404] Product not found with id: 123
```

### 400 - Bad Request
```
[400] Invalid input: Email is already registered
```

### 401 - Unauthorized
Automatically redirects to login page (handled by interceptor)

### 500 - Internal Server Error
```
[500] An unexpected error occurred. Please try again later.
```

## Future Improvements

- Add toast notifications for non-critical errors
- Implement error tracking/logging service integration
- Add retry mechanism for network failures
- Implement offline error handling
- Add field-level validation error display for forms
