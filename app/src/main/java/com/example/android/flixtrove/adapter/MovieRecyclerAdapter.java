package com.example.android.flixtrove.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.example.android.flixtrove.R;
import com.example.android.flixtrove.service.model.Movie;
import com.example.android.flixtrove.ui.PosterImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieRecyclerAdapter
        extends RecyclerView.Adapter<MovieRecyclerAdapter.PosterViewHolder> {

    /** List of movieList */
    private List<Movie> movieList;

    /** Application context */
    private final Context context;

    /** ItemClickListener */
    private final ListItemClickListener clickListener;

    /** Base URL for movie poster */
    private static final String POSTER_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";

    /** Movie poster size */
    private static final String POSTER_IMAGE_SIZE = "w780";

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemMovieId);
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
        View itemView = inflater.inflate(R.layout.list_item, parent, false);

        // Return the inflated item view
        return new PosterViewHolder(itemView);
    }

    /** Replace the contents of a view (invoked by the layout manager) */
    @Override
    public void onBindViewHolder(@NonNull PosterViewHolder holder, int position) {
        // Get the movie at position
        Movie movieAtScrollPosition = this.movieList.get(position);

        // Get the path of the current movie's poster
        String posterPath = movieAtScrollPosition.getPosterPath();

        // Deal with null poster path values
        if (posterPath == null) {
            // Set default poster when movie poster unavailable
            holder.displayMoviePoster.setImageResource(R.drawable.ic_movie);

            // Bail early
            return;
        }

        // If valid poster path exists for movie, append the same to the url
        String moviePosterUrl = POSTER_IMAGE_BASE_URL + POSTER_IMAGE_SIZE + posterPath;

        // Construct movie poster final url
        Picasso.get()
                .load(moviePosterUrl)
                .placeholder(android.R.drawable.sym_def_app_icon)
                .error(android.R.drawable.sym_def_app_icon)
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

    /** Provide a reference to the views for each data item via a view holder */
    class PosterViewHolder
            extends RecyclerView.ViewHolder
            implements OnClickListener {
        /** Each data item is a movie represented as a movie poster image */
        @BindView(R.id.grid_movie_poster_iv)
        PosterImageView displayMoviePoster;

        /** Initialize view and cache reference hooks */
        PosterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            int movieId = movieList.get(clickedPosition).getId();
            clickListener.onListItemClick(movieId);
        }
    }
}
