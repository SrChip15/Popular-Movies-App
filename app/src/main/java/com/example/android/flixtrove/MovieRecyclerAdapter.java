package com.example.android.flixtrove;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieRecyclerAdapter
		extends RecyclerView.Adapter<MovieRecyclerAdapter.MoviePosterHolder> {
	/**
	 * Tag for log messages
	 */
	private final String LOG_TAG = MovieRecyclerAdapter.class.getSimpleName();

	/**
	 * List of movieList
	 */
	private List<Movie> movieList;

	private String BASE_POSTER_URL = "http://image.tmdb.org/t/p/w342/";


	/**
	 * Create new Movie RecyclerView adapter
	 */
	public MovieRecyclerAdapter(List<Movie> movieList) {
		this.movieList = movieList;
	}

	/**
	 * Create new views (invoked by the layout manager)
	 */
	@Override
	public MoviePosterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		// Get context of the parent ViewGroup
		Context context = parent.getContext();

		// Create a layout inflater to inflate the view for every single item
		// in the adapter's data set
		LayoutInflater inflater = LayoutInflater.from(context);

		// Let the adapter view handle attaching to parent
		boolean shouldAttachToParentImmediately = false;

		// Inflate item view
		View itemView = inflater.inflate
				(
						R.layout.list_item,
						parent,
						shouldAttachToParentImmediately
				);

		// Return the inflated item view
		return new MoviePosterHolder(itemView);
	}

	/**
	 * Replace the contents of a view (invoked by the layout manager)
	 */
	@Override
	public void onBindViewHolder(MoviePosterHolder holder, int position) {
		// Get the context of the view to be bound
		Context context = holder.displayMoviePoster.getContext();

		// Get the movie at position
		Movie movieAtPosition = this.movieList.get(position);

		// Get the path of the current movie's poster
		String moviePosterPath = movieAtPosition.getPosterUrl();

		// Build string url
		String moviePosterFinalUrl = BASE_POSTER_URL+moviePosterPath;

		Log.v(LOG_TAG, "Image URI: " + moviePosterFinalUrl);

		// Construct movie poster final url
		Picasso.with(context)
				.load(moviePosterFinalUrl)
				.fit()
				.centerInside()
				.into(holder.displayMoviePoster);
	}

	/**
	 * Return the size of your dataset (invoked by the layout manager)
	 */
	@Override
	public int getItemCount() {
		return this.movieList.size();
	}

	/**
	 * Helper method to clear adapter's old data
	 */
	void clear() {
		// Clear our adapter's data set
		this.movieList = new ArrayList<Movie>();
	}

	/**
	 * Helper method to add all movieList from a list into the adapter's data set
	 */
	void addAll(List<Movie> movieList) {
		// Traverse the movie list
		for (Movie movie : movieList) {
			// Add each movie
			this.movieList.add(movie);

			// Trigger reload of RecyclerView with fresh data
			notifyDataSetChanged();
		}
	}

	/**
	 * This class caches view to be re-used by the adapter
	 */
	class MoviePosterHolder extends RecyclerView.ViewHolder {

		@BindView(R.id.iv_movie_poster)
		ImageView displayMoviePoster;

		public MoviePosterHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
