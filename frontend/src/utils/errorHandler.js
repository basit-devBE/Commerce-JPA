/**
 * Utility function to extract error message from API error response
 * Handles both REST API ErrorResponse and GraphQL error structures
 */
export const getErrorMessage = (error, defaultMessage = 'An error occurred') => {
  // Handle GraphQL errors
  if (error.graphQLErrors && error.graphQLErrors.length > 0) {
    const gqlError = error.graphQLErrors[0];
    
    // Check for specific error types based on extensions
    if (gqlError.extensions?.classification) {
      const classification = gqlError.extensions.classification;
      
      // Map GraphQL error types to user-friendly messages
      switch (classification) {
        case 'NOT_FOUND':
          return gqlError.message || 'Resource not found';
        case 'BAD_REQUEST':
          return gqlError.message || 'Invalid request';
        case 'UNAUTHORIZED':
          return gqlError.message || 'You are not authorized to perform this action';
        case 'INTERNAL_ERROR':
          // Avoid showing the UUID in INTERNAL_ERROR messages
          if (gqlError.message.includes('INTERNAL_ERROR for')) {
            return 'An internal error occurred. Please try again or contact support.';
          }
          return gqlError.message || 'An internal server error occurred';
        default:
          return gqlError.message || defaultMessage;
      }
    }
    
    return gqlError.message || defaultMessage;
  }
  
  // Check if it's an axios error with response
  if (error.response?.data) {
    const errorData = error.response.data;
    
    // If it's our ErrorResponse structure, extract the message
    if (errorData.message) {
      return errorData.message;
    }
    
    // Fallback to other possible error formats
    if (typeof errorData === 'string') {
      return errorData;
    }
    
    // If error data has an error field
    if (errorData.error) {
      return errorData.error;
    }
  }
  
  // Network or other errors
  if (error.message) {
    // Clean up INTERNAL_ERROR messages that may have slipped through
    if (error.message.includes('INTERNAL_ERROR for')) {
      return 'An internal error occurred. Please try again or contact support.';
    }
    return error.message;
  }
  
  return defaultMessage;
};

/**
 * Get full error details for debugging or detailed display
 * Supports both REST and GraphQL errors
 */
export const getErrorDetails = (error) => {
  // Handle GraphQL errors
  if (error.graphQLErrors && error.graphQLErrors.length > 0) {
    const gqlError = error.graphQLErrors[0];
    return {
      message: gqlError.message || 'Unknown GraphQL error',
      path: gqlError.path?.join('.'),
      extensions: gqlError.extensions,
      type: 'GraphQL',
    };
  }
  
  if (error.response?.data) {
    const errorData = error.response.data;
    return {
      message: errorData.message || 'Unknown error',
      status: errorData.status || error.response.status,
      timestamp: errorData.timestamp,
      path: errorData.path,
      type: 'REST',
    };
  }
  
  return {
    message: error.message || 'Unknown error',
    type: 'Unknown',
  };
};
    status: null,
    timestamp: new Date().toISOString(),
    path: null,
  };
};

/**
 * Format error for user-friendly display
 */
export const formatErrorForDisplay = (error) => {
  const details = getErrorDetails(error);
  
  let displayMessage = details.message;
  
  // Add status code if available
  if (details.status) {
    displayMessage = `[${details.status}] ${displayMessage}`;
  }
  
  return displayMessage;
};
