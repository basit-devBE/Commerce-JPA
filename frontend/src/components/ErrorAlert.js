import React from 'react';
import { ExclamationCircleIcon, XMarkIcon } from '@heroicons/react/24/outline';

const ErrorAlert = ({ error, onDismiss, showDetails = false }) => {
  if (!error) return null;

  const errorData = typeof error === 'string' 
    ? { message: error } 
    : error.response?.data || { message: error.message || 'An error occurred' };

  return (
    <div className="bg-red-50 border-l-4 border-red-500 p-4 rounded-lg mb-4">
      <div className="flex items-start">
        <div className="flex-shrink-0">
          <ExclamationCircleIcon className="h-5 w-5 text-red-400" />
        </div>
        <div className="ml-3 flex-1">
          <h3 className="text-sm font-medium text-red-800">Error</h3>
          <div className="mt-2 text-sm text-red-700">
            <p>{errorData.message}</p>
            {showDetails && errorData.status && (
              <p className="mt-1 text-xs text-red-600">
                Status: {errorData.status}
                {errorData.path && ` | Path: ${errorData.path}`}
              </p>
            )}
          </div>
        </div>
        {onDismiss && (
          <div className="ml-auto pl-3">
            <button
              onClick={onDismiss}
              className="inline-flex rounded-md bg-red-50 p-1.5 text-red-500 hover:bg-red-100 focus:outline-none focus:ring-2 focus:ring-red-600 focus:ring-offset-2 focus:ring-offset-red-50"
            >
              <span className="sr-only">Dismiss</span>
              <XMarkIcon className="h-5 w-5" />
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default ErrorAlert;
