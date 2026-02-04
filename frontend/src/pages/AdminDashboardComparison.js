import React from 'react';
import { Link } from 'react-router-dom';
import { ChartBarIcon, BoltIcon } from '@heroicons/react/24/outline';

const AdminDashboardComparison = () => {
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 py-12 px-4">
      <div className="max-w-6xl mx-auto">
        <div className="text-center mb-12">
          <h1 className="text-4xl font-bold text-gray-900 mb-4">
            Choose Your Admin Dashboard
          </h1>
          <p className="text-lg text-gray-600">
            Select between REST API or GraphQL powered interfaces
          </p>
        </div>

        <div className="grid md:grid-cols-2 gap-8">
          {/* REST API Dashboard */}
          <div className="bg-white rounded-2xl shadow-xl overflow-hidden transform hover:scale-105 transition-transform duration-300">
            <div className="bg-gradient-to-r from-blue-500 to-blue-600 p-6">
              <div className="flex items-center justify-center mb-4">
                <ChartBarIcon className="w-16 h-16 text-white" />
              </div>
              <h2 className="text-2xl font-bold text-white text-center">
                REST API Dashboard
              </h2>
            </div>
            
            <div className="p-6">
              <div className="mb-6">
                <h3 className="text-lg font-semibold text-gray-900 mb-3">Features:</h3>
                <ul className="space-y-2">
                  <li className="flex items-start">
                    <span className="text-green-500 mr-2">✓</span>
                    <span className="text-gray-700">Traditional REST endpoints</span>
                  </li>
                  <li className="flex items-start">
                    <span className="text-green-500 mr-2">✓</span>
                    <span className="text-gray-700">Built-in pagination support</span>
                  </li>
                  <li className="flex items-start">
                    <span className="text-green-500 mr-2">✓</span>
                    <span className="text-gray-700">Filtering and search</span>
                  </li>
                  <li className="flex items-start">
                    <span className="text-green-500 mr-2">✓</span>
                    <span className="text-gray-700">Performance metrics</span>
                  </li>
                  <li className="flex items-start">
                    <span className="text-green-500 mr-2">✓</span>
                    <span className="text-gray-700">Cache monitoring</span>
                  </li>
                </ul>
              </div>

              <div className="mb-6">
                <h3 className="text-lg font-semibold text-gray-900 mb-3">Best For:</h3>
                <ul className="space-y-2 text-sm text-gray-600">
                  <li>• Standard CRUD operations</li>
                  <li>• Full-featured data management</li>
                  <li>• Advanced pagination needs</li>
                  <li>• Performance monitoring</li>
                </ul>
              </div>

              <Link
                to="/admin"
                className="block w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-6 rounded-lg text-center transition-colors"
              >
                Open REST Dashboard
              </Link>
            </div>
          </div>

          {/* GraphQL Dashboard */}
          <div className="bg-white rounded-2xl shadow-xl overflow-hidden transform hover:scale-105 transition-transform duration-300 border-2 border-purple-400">
            <div className="bg-gradient-to-r from-purple-500 to-pink-500 p-6">
              <div className="flex items-center justify-center mb-4">
                <BoltIcon className="w-16 h-16 text-white" />
              </div>
              <h2 className="text-2xl font-bold text-white text-center">
                GraphQL Dashboard
              </h2>
              <div className="text-center mt-2">
                <span className="bg-yellow-400 text-yellow-900 text-xs font-bold px-3 py-1 rounded-full">
                  ⚡ NEW
                </span>
              </div>
            </div>
            
            <div className="p-6">
              <div className="mb-6">
                <h3 className="text-lg font-semibold text-gray-900 mb-3">Features:</h3>
                <ul className="space-y-2">
                  <li className="flex items-start">
                    <span className="text-green-500 mr-2">✓</span>
                    <span className="text-gray-700">Single endpoint for all data</span>
                  </li>
                  <li className="flex items-start">
                    <span className="text-green-500 mr-2">✓</span>
                    <span className="text-gray-700">Fetch exactly what you need</span>
                  </li>
                  <li className="flex items-start">
                    <span className="text-green-500 mr-2">✓</span>
                    <span className="text-gray-700">Strongly typed schema</span>
                  </li>
                  <li className="flex items-start">
                    <span className="text-green-500 mr-2">✓</span>
                    <span className="text-gray-700">Reduced over-fetching</span>
                  </li>
                  <li className="flex items-start">
                    <span className="text-green-500 mr-2">✓</span>
                    <span className="text-gray-700">Self-documenting API</span>
                  </li>
                </ul>
              </div>

              <div className="mb-6">
                <h3 className="text-lg font-semibold text-gray-900 mb-3">Best For:</h3>
                <ul className="space-y-2 text-sm text-gray-600">
                  <li>• Flexible data queries</li>
                  <li>• Mobile or low-bandwidth</li>
                  <li>• Modern API architecture</li>
                  <li>• Real-time capabilities</li>
                </ul>
              </div>

              <Link
                to="/admin/graphql"
                className="block w-full bg-gradient-to-r from-purple-600 to-pink-600 hover:from-purple-700 hover:to-pink-700 text-white font-bold py-3 px-6 rounded-lg text-center transition-colors"
              >
                Open GraphQL Dashboard ⚡
              </Link>
            </div>
          </div>
        </div>

        {/* Comparison Table */}
        <div className="mt-12 bg-white rounded-2xl shadow-xl overflow-hidden">
          <div className="bg-gray-800 p-6">
            <h2 className="text-2xl font-bold text-white text-center">
              Quick Comparison
            </h2>
          </div>
          
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-4 text-left text-sm font-semibold text-gray-900">Feature</th>
                  <th className="px-6 py-4 text-center text-sm font-semibold text-gray-900">REST API</th>
                  <th className="px-6 py-4 text-center text-sm font-semibold text-gray-900">GraphQL</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                <tr>
                  <td className="px-6 py-4 text-sm text-gray-900">Endpoints</td>
                  <td className="px-6 py-4 text-center text-sm text-gray-600">Multiple</td>
                  <td className="px-6 py-4 text-center text-sm text-gray-600">Single</td>
                </tr>
                <tr className="bg-gray-50">
                  <td className="px-6 py-4 text-sm text-gray-900">Data Fetching</td>
                  <td className="px-6 py-4 text-center text-sm text-gray-600">Fixed structure</td>
                  <td className="px-6 py-4 text-center text-sm text-gray-600">Flexible</td>
                </tr>
                <tr>
                  <td className="px-6 py-4 text-sm text-gray-900">Over-fetching</td>
                  <td className="px-6 py-4 text-center text-sm text-gray-600">Common</td>
                  <td className="px-6 py-4 text-center text-sm text-gray-600">Eliminated</td>
                </tr>
                <tr className="bg-gray-50">
                  <td className="px-6 py-4 text-sm text-gray-900">Type Safety</td>
                  <td className="px-6 py-4 text-center text-sm text-gray-600">OpenAPI/Swagger</td>
                  <td className="px-6 py-4 text-center text-sm text-gray-600">Built-in Schema</td>
                </tr>
                <tr>
                  <td className="px-6 py-4 text-sm text-gray-900">Documentation</td>
                  <td className="px-6 py-4 text-center text-sm text-gray-600">External</td>
                  <td className="px-6 py-4 text-center text-sm text-gray-600">Self-documenting</td>
                </tr>
                <tr className="bg-gray-50">
                  <td className="px-6 py-4 text-sm text-gray-900">Real-time Updates</td>
                  <td className="px-6 py-4 text-center text-sm text-gray-600">Polling/WebSocket</td>
                  <td className="px-6 py-4 text-center text-sm text-gray-600">Subscriptions</td>
                </tr>
                <tr>
                  <td className="px-6 py-4 text-sm text-gray-900">Caching</td>
                  <td className="px-6 py-4 text-center text-sm text-gray-600">HTTP-based</td>
                  <td className="px-6 py-4 text-center text-sm text-gray-600">Query-based</td>
                </tr>
                <tr className="bg-gray-50">
                  <td className="px-6 py-4 text-sm text-gray-900">Learning Curve</td>
                  <td className="px-6 py-4 text-center text-sm text-gray-600">Easy</td>
                  <td className="px-6 py-4 text-center text-sm text-gray-600">Moderate</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        {/* Documentation Links */}
        <div className="mt-8 text-center">
          <p className="text-gray-600 mb-4">
            Need help deciding? Check out our documentation:
          </p>
          <div className="flex justify-center gap-4">
            <a
              href="/GRAPHQL_QUICKSTART.md"
              className="text-blue-600 hover:text-blue-800 font-medium"
              target="_blank"
              rel="noopener noreferrer"
            >
              GraphQL Quick Start →
            </a>
            <span className="text-gray-400">|</span>
            <a
              href="/GRAPHQL_ADMIN_README.md"
              className="text-blue-600 hover:text-blue-800 font-medium"
              target="_blank"
              rel="noopener noreferrer"
            >
              Full Documentation →
            </a>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminDashboardComparison;
