/**
 * Utility function to extract error message from API error response
 * Handles the ErrorResponse structure from the backend:
 * { timestamp, status, message, path }
 */
export const getErrorMessage = (error, defaultMessage = 'An error occurred') => {
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
    return error.message;
  }
  
  return defaultMessage;
};

/**
 * Get full error details for debugging or detailed display
 */
export const getErrorDetails = (error) => {
  if (error.response?.data) {
    const errorData = error.response.data;
    return {
      message: errorData.message || 'Unknown error',
      status: errorData.status || error.response.status,
      timestamp: errorData.timestamp,
      path: errorData.path,
    };
  }
  
  return {
    message: error.message || 'Unknown error',
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
