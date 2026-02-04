import React, { useState, useEffect } from 'react';
import { StarIcon } from '@heroicons/react/24/solid';
import { StarIcon as StarIconOutline } from '@heroicons/react/24/outline';
import * as graphqlAPI from '../services/graphqlApi';
import ErrorAlert from './ErrorAlert';

const ProductReviews = ({ productId }) => {
  const [reviews, setReviews] = useState([]);
  const [stats, setStats] = useState({ averageRating: 0, totalReviews: 0 });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showAddReview, setShowAddReview] = useState(false);
  const [newReview, setNewReview] = useState({ rating: 5, comment: '' });
  const [submitting, setSubmitting] = useState(false);

  const currentUser = JSON.parse(localStorage.getItem('user') || '{}');

  useEffect(() => {
    fetchReviews();
    fetchStats();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [productId]);

  const fetchReviews = async () => {
    try {
      setLoading(true);
      const data = await graphqlAPI.getReviewsByProductId(productId);
      setReviews(data.reviewsByProductId || []);
    } catch (err) {
      setError(err);
    } finally {
      setLoading(false);
    }
  };

  const fetchStats = async () => {
    try {
      const data = await graphqlAPI.getProductReviewStats(productId);
      setStats(data.productReviewStats);
    } catch (err) {
      console.error('Error fetching review stats:', err);
    }
  };

  const handleSubmitReview = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setError(null);

    try {
      await graphqlAPI.addReview({
        productId: parseInt(productId),
        userId: parseInt(currentUser.id),
        rating: newReview.rating,
        comment: newReview.comment
      });

      setNewReview({ rating: 5, comment: '' });
      setShowAddReview(false);
      await fetchReviews();
      await fetchStats();
    } catch (err) {
      setError(err);
    } finally {
      setSubmitting(false);
    }
  };

  const renderStars = (rating, interactive = false, onRate = null) => {
    return (
      <div className="flex gap-1">
        {[1, 2, 3, 4, 5].map((star) => (
          <button
            key={star}
            type={interactive ? 'button' : undefined}
            onClick={interactive ? () => onRate(star) : undefined}
            disabled={!interactive}
            className={interactive ? 'cursor-pointer hover:scale-110 transition-transform' : ''}
          >
            {star <= rating ? (
              <StarIcon className="w-5 h-5 text-yellow-400" />
            ) : (
              <StarIconOutline className="w-5 h-5 text-gray-300" />
            )}
          </button>
        ))}
      </div>
    );
  };

  const userHasReviewed = reviews.some(review => review.userId === currentUser.id);

  return (
    <div className="mt-8 border-t pt-8">
      <h2 className="text-2xl font-bold mb-6">Customer Reviews</h2>

      {error && <ErrorAlert error={error} onClose={() => setError(null)} />}

      {/* Stats Summary */}
      <div className="bg-gray-50 rounded-lg p-6 mb-6">
        <div className="flex items-center gap-4">
          <div className="text-center">
            <div className="text-4xl font-bold text-gray-900">
              {stats.averageRating ? stats.averageRating.toFixed(1) : '0.0'}
            </div>
            <div className="flex items-center justify-center mt-2">
              {renderStars(Math.round(stats.averageRating || 0))}
            </div>
            <div className="text-sm text-gray-600 mt-1">
              {stats.totalReviews} {stats.totalReviews === 1 ? 'review' : 'reviews'}
            </div>
          </div>
        </div>
      </div>

      {/* Add Review Button */}
      {currentUser.id && !userHasReviewed && (
        <div className="mb-6">
          {!showAddReview ? (
            <button
              onClick={() => setShowAddReview(true)}
              className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700"
            >
              Write a Review
            </button>
          ) : (
            <form onSubmit={handleSubmitReview} className="bg-white border rounded-lg p-6">
              <h3 className="text-lg font-semibold mb-4">Write Your Review</h3>
              
              <div className="mb-4">
                <label className="block text-sm font-medium mb-2">Rating</label>
                {renderStars(newReview.rating, true, (rating) => setNewReview({ ...newReview, rating }))}
              </div>

              <div className="mb-4">
                <label className="block text-sm font-medium mb-2">Comment</label>
                <textarea
                  value={newReview.comment}
                  onChange={(e) => setNewReview({ ...newReview, comment: e.target.value })}
                  rows="4"
                  className="w-full border rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500 focus:outline-none"
                  placeholder="Share your thoughts about this product..."
                />
              </div>

              <div className="flex gap-2">
                <button
                  type="submit"
                  disabled={submitting}
                  className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 disabled:bg-gray-400"
                >
                  {submitting ? 'Submitting...' : 'Submit Review'}
                </button>
                <button
                  type="button"
                  onClick={() => {
                    setShowAddReview(false);
                    setNewReview({ rating: 5, comment: '' });
                  }}
                  className="bg-gray-200 text-gray-700 px-6 py-2 rounded-lg hover:bg-gray-300"
                >
                  Cancel
                </button>
              </div>
            </form>
          )}
        </div>
      )}

      {userHasReviewed && (
        <div className="mb-6 bg-blue-50 border border-blue-200 rounded-lg p-4 text-blue-800">
          You've already reviewed this product
        </div>
      )}

      {/* Reviews List */}
      <div className="space-y-4">
        {loading ? (
          <div className="text-center py-8 text-gray-500">Loading reviews...</div>
        ) : reviews.length === 0 ? (
          <div className="text-center py-8 text-gray-500">
            No reviews yet. Be the first to review this product!
          </div>
        ) : (
          reviews.map((review) => (
            <div key={review.id} className="bg-white border rounded-lg p-6">
              <div className="flex items-start justify-between mb-3">
                <div>
                  <div className="font-semibold text-gray-900">{review.userName}</div>
                  <div className="flex items-center gap-2 mt-1">
                    {renderStars(review.rating)}
                    <span className="text-sm text-gray-500">
                      {new Date(review.createdAt).toLocaleDateString()}
                    </span>
                  </div>
                </div>
              </div>
              {review.comment && (
                <p className="text-gray-700 mt-2">{review.comment}</p>
              )}
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default ProductReviews;
