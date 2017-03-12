package com.example.piero.themoviesproject;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.piero.themoviesproject.utilities.Review;

/**
 * {@link ReviewsAdapter} exposes a list of reviews to a
 * {@link RecyclerView}
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsAdapterViewHolder> {

    private Review[] mReviewsData;

    /**
     * Cache of the children views for a review list item.
     */
    public class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder {
        public final TextView mReviewContentTextView;
        public final TextView mReviewAuthorTextView;

        public ReviewsAdapterViewHolder(View view) {
            super(view);
            mReviewContentTextView = (TextView) view.findViewById(R.id.tv_review_content);
            mReviewAuthorTextView = (TextView) view.findViewById(R.id.tv_review_author);
        }
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new ReviewsAdapterViewHolder that holds the View for each list item
     */
    @Override
    public ReviewsAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.review_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ReviewsAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the weather
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param reviewsAdapterViewHolder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ReviewsAdapterViewHolder reviewsAdapterViewHolder, int position) {
        reviewsAdapterViewHolder.mReviewAuthorTextView.setText(mReviewsData[position].author);
        reviewsAdapterViewHolder.mReviewContentTextView.setText(mReviewsData[position].content);
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our reviews list
     */
    @Override
    public int getItemCount() {
        if (null == mReviewsData) return 0;
        return mReviewsData.length;
    }

    /**
     * This method is used to set the reviews on a ReviewsAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new ReviewsAdapter to display it.
     *
     * @param reviewsData The new reviews data to be displayed.
     */
    public void setData(Review[] reviewsData) {
        mReviewsData = reviewsData;
        notifyDataSetChanged();
    }
}