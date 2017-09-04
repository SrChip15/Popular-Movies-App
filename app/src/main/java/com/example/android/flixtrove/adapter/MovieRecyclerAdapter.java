package com.example.android.flixtrove.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.flixtrove.R;
import com.example.android.flixtrove.service.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieRecyclerAdapter
		extends RecyclerView.Adapter<MovieRecyclerAdapter.posterViewHolder> {
	/**
	 * Tag for log messages
	 */
	private static final String TAG = MovieRecyclerAdapter.class.getSimpleName();

	/**
	 * List of movieList
	 */
	private List<Movie> movieList;

	private final Context context;

	/** Base URL for movie poster */
	private final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w342//";


	/**
	 * Create new Movie RecyclerView adapter
	 */
	public MovieRecyclerAdapter(Context context) {
		this.context = context;
		this.movieList = new ArrayList<>();
	}

	/**
	 * Create new views (invoked by the layout manager)
	 */
	@Override
	public posterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		// Get context of the parent ViewGroup
		Context context = parent.getContext();

		// Let the adapter view handle attaching to parent
		boolean shouldAttachToParentImmediately = false;

		// Create and inflate view for each data item in the list
		View itemView = LayoutInflater.from(context).inflate
				(
						R.layout.list_item,
						parent,
						shouldAttachToParentImmediately);

		// Return the inflated item view
		return new posterViewHolder(itemView);
	}

	/**
	 * Replace the contents of a view (invoked by the layout manager)
	 */
	@Override
	public void onBindViewHolder(posterViewHolder holder, int position) {
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
		String moviePosterUrl = POSTER_BASE_URL + posterPath;

		// Construct movie poster final url
		Picasso.with(context)
				.load(moviePosterUrl)
				.placeholder(android.R.drawable.sym_def_app_icon)
				.error(android.R.drawable.sym_def_app_icon)
				.into(holder.displayMoviePoster);
	}

	/**
	 * Return the size of your data set (invoked by the layout manager)
	 */
	@Override
	public int getItemCount() {
		return this.movieList.size();
	}

	/**
	 * Helper method to clear adapter's old data
	 */
	public void clear() {
		// Clear our adapter's data set
		this.movieList = new ArrayList<>();
	}

	/**
	 * Helper method to add all movieList from a list into the adapter's data set
	 */
	public void addAll(List<Movie> movies) {
		// Traverse the movie list
		if (movies != null && !movies.isEmpty()) {
			for (Movie movie : movies) {
				// Add each movie
				this.movieList.add(movie);
			}

			// Inform adapter of the updated data set
			// This triggers reloading of the UI view
			notifyDataSetChanged();
		}
	}

	/**
	 * Provide a reference to the views for each data item via a view holder
	 */
	static class posterViewHolder extends RecyclerView.ViewHolder {
		/** Each data item is a movie represented as a movie poster image*/
		@BindView(R.id.iv_movie_poster)
		ImageView displayMoviePoster;

		/** Initialize view and cache reference hooks  */
		public posterViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
