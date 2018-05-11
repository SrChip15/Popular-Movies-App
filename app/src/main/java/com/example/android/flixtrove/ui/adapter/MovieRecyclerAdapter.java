package com.example.android.flixtrove.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.android.flixtrove.R;
import com.example.android.flixtrove.service.model.Movie;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MovieRecyclerAdapter
        extends RecyclerView.Adapter<MovieRecyclerAdapter.PosterViewHolder> {
    /** Provide a reference to the views for each data item via a view holder */
    class PosterViewHolder
            extends RecyclerView.ViewHolder
            implements OnClickListener {
        /** Each data item is a movie represented as a movie poster image */
        @BindView(R.id.grid_movie_poster_iv)
        ImageView displayMoviePoster;

        /** Initialize view and cache reference hooks */
        PosterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            Timber.d("Clicked position %s", clickedPosition);

            int movieId = movieList.get(clickedPosition).getId();
            Timber.i("Movie ID: %d", movieId);
            clickListener.onListItemClick(clickedPosition, movieId);
        }
    }

    /* Class variables */
    private List<Movie> movieList;
    private final Context context;
    private final ListItemClickListener clickListener;

    /* Class Constants */
    private static final String POSTER_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String POSTER_IMAGE_SIZE = "w342";

    public interface ListItemClickListener {
        void onListItemClick(int position, int clickedItemMovieId); // may use position to smooth scroll upon up navigation
    }

    /** Create new Movie RecyclerView adapter */
    public MovieRecyclerAdapter(Context context, ListItemClickListener listener) {
        this.context = context;
        this.clickListener = listener;
        this.movieList = new ArrayList<>();
    }

    /** Create new views (invoked by the layout manager) */
    @NonNull
    @Override
    public PosterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create and inflate view for each data item in the list
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.list_item_movie_poster, parent, false);

        // Return the inflated item view
        return new PosterViewHolder(itemView);
    }

    /** Replace the contents of a view (invoked by the layout manager) */
    @Override
    public void onBindViewHolder(@NonNull PosterViewHolder holder, int position) {
        Movie movieAtScrollPosition = this.movieList.get(position);
        String posterPath = movieAtScrollPosition.getPosterPath();
        String moviePosterUrl = POSTER_IMAGE_BASE_URL + POSTER_IMAGE_SIZE + posterPath;
        Timber.i("Movie poster path: %s", moviePosterUrl);

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.ic_movie) // This checks for null paths & if null loads placeholder image
                .error(R.drawable.ic_movie)
                .centerCrop();

        Glide.with(context)
                .setDefaultRequestOptions(options)
                .load(moviePosterUrl)
                .into(holder.displayMoviePoster);
    }

    /** Return the size of your data set (invoked by the layout manager) */
    @Override
    public int getItemCount() {
        return this.movieList.size();
    }

    /** Helper method to clear adapter's old data */
    public void clear() {
        // Clear our adapter's data set
        this.movieList = new ArrayList<>();
        notifyDataSetChanged();
    }

    /** Helper method to add all movieList from a list into the adapter's data set */
    public void addAll(List<Movie> movies) {
        if (movies != null && !movies.isEmpty()) {
            // Add all movies
            movieList.addAll(movies);

            notifyDataSetChanged();
        }
    }
}
