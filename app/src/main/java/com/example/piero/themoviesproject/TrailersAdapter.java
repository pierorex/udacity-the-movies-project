package com.example.piero.themoviesproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;

import com.example.piero.themoviesproject.utilities.Trailer;
import com.squareup.picasso.Picasso;

/**
 * {@link TrailersAdapter} exposes a list of trailers to a
 * {@link android.support.v7.widget.RecyclerView}
 */
public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersAdapterViewHolder> {

    private Trailer[] mTrailersData;

    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private final TrailersAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface TrailersAdapterOnClickHandler {
        void onClick(String trailerKey);
    }

    /**
     * Creates a TrailersAdapter.
     *
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public TrailersAdapter(TrailersAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    /**
     * Cache of the children views for a trailer list item.
     */
    public class TrailersAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mTrailerNameTextView;
        public final ImageView mPlayButtonImageView;

        public TrailersAdapterViewHolder(View view) {
            super(view);
            mTrailerNameTextView = (TextView) view.findViewById(R.id.tv_trailer_name);
            mPlayButtonImageView = (ImageView) view.findViewById(R.id.iv_play_button);

            Picasso.with(view.getContext())
                    .load(R.raw.playbutton)
                    .noFade()
                    .resize(200, 200)
                    .error(R.raw.load_error)
                    .into(mPlayButtonImageView);

            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            mClickHandler.onClick(mTrailersData[getAdapterPosition()].key);
        }
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new TrailersAdapterViewHolder that holds the View for each list item
     */
    @Override
    public TrailersAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.trailer_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new TrailersAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the weather
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param trailerAdapterViewHolder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(TrailersAdapterViewHolder trailerAdapterViewHolder, int position) {
        trailerAdapterViewHolder.mTrailerNameTextView.setText(mTrailersData[position].name);
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our trailers list
     */
    @Override
    public int getItemCount() {
        if (null == mTrailersData) return 0;
        return mTrailersData.length;
    }

    /**
     * This method is used to set the trailers on a TrailersAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new TrailersAdapter to display it.
     *
     * @param trailersData The new trailers data to be displayed.
     */
    public void setData(Trailer[] trailersData) {
        mTrailersData = trailersData;
        notifyDataSetChanged();
    }
}