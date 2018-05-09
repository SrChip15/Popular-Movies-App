package com.example.android.flixtrove.ui.detail;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.flixtrove.PrivateApiKey;
import com.example.android.flixtrove.R;
import com.example.android.flixtrove.service.model.MovieDetailResponse;
import com.example.android.flixtrove.service.repository.MovieRepository;
import com.example.android.flixtrove.service.repository.MovieService;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressWarnings("ConstantConditions")
public class MovieDetailFragment extends Fragment {
	private static final String TAG = MovieDetailFragment.class.getSimpleName();

	public static final String INTENT_MOVIE_ID = "movie_id";

	private static final String POSTER_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";
	private static final String POSTER_IMAGE_SIZE = "w780";

	private MovieService apiConnection;

	private int movieId;

	@BindView(R.id.detail_movie_poster_iv)
	ImageView moviePosterImageView;

	@BindView(R.id.movie_vote_average_rb)
	RatingBar voteAverageRatingBar;

	@BindView(R.id.movie_vote_count_tv)
	TextView voteCountTextView;

	@BindView(R.id.movie_release_date_tv)
	TextView releaseDateTextView;

	@BindView(R.id.movie_plot_synopsis_tv)
	TextView plotSynopsisTextView;


	public MovieDetailFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(false);
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate detailed pane fragment layout
		View rootView = inflater.inflate(R.layout.fragment_detail_view, container, false);

		// Get reference to relevant views
		ButterKnife.bind(this, rootView);

		// Get movie id from the movie details intent
		Intent intent = getActivity().getIntent();
		if (intent != null && intent.hasExtra(INTENT_MOVIE_ID)) {
			movieId = intent.getIntExtra(INTENT_MOVIE_ID, 0);
		}

		// Connect to the api
		apiConnection = MovieRepository.getClient().create(MovieService.class);

		// Load view
		loadMovieDetails();

		// Return view
		return rootView;
	}

	private void loadMovieDetails() {
		callMoviesApi().enqueue(new Callback<MovieDetailResponse>() {
			@Override
			public void onResponse(@NonNull Call<MovieDetailResponse> call,
			                       @NonNull Response<MovieDetailResponse> response) {
				// Get the movie
				MovieDetailResponse movie = fetchMovie(response);

				// Set movie poster
				Picasso.get()
						.load(POSTER_IMAGE_BASE_URL + POSTER_IMAGE_SIZE + movie.getBackdropPath())
						.into(moviePosterImageView);

				// Set vote average ratings
				BigDecimal getVoteAverage =  new BigDecimal(movie.getVoteAverage());
				// Get the vote average as a float value
				float tenPointRating = getVoteAverage.floatValue();
				// Conversion of 10 point to 5 point rating system
				float convertedRating = 5 * (tenPointRating / 10);
				voteAverageRatingBar.setRating(convertedRating);

				// Set vote count
				voteCountTextView.setText(getString(R.string.rating_count, movie.getVoteCount()));

				// Set release date
				releaseDateTextView.setText(movie.getReleaseDate());

				// Set plot synopsis
				plotSynopsisTextView.setText(movie.getOverview());
				plotSynopsisTextView.setMovementMethod(new ScrollingMovementMethod());

				// Set title for action bar
				((MovieDetailActivity) getActivity()).setActionBarTitle(movie.getTitle());
			}

			@Override
			public void onFailure(@NonNull Call<MovieDetailResponse> call, @NonNull Throwable t) {

			}
		});
	}

	private Call<MovieDetailResponse> callMoviesApi() {
		if (movieId != 0) {
			return apiConnection.getMovie(movieId, PrivateApiKey.YOUR_API_KEY);
		}

		// Error with movie id
		Log.e(TAG, "Movie ID: " + movieId);

		// Return nothing when incorrect movie id
		return null;
	}

	private MovieDetailResponse fetchMovie(Response<MovieDetailResponse> response) {
		// Parse the raw response from the API
		return response.body();
	}
}
