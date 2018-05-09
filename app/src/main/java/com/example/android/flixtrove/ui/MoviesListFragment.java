package com.example.android.flixtrove.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ProgressBar;

import com.example.android.flixtrove.PrivateApiKey;
import com.example.android.flixtrove.R;
import com.example.android.flixtrove.adapter.MovieRecyclerAdapter;
import com.example.android.flixtrove.adapter.MovieRecyclerAdapter.ListItemClickListener;
import com.example.android.flixtrove.adapter.PaginationScrollListener;
import com.example.android.flixtrove.service.model.MainResponse;
import com.example.android.flixtrove.service.model.Movie;
import com.example.android.flixtrove.service.repository.MovieRepository;
import com.example.android.flixtrove.service.repository.MovieService;
import com.example.android.flixtrove.ui.detail.MovieDetailActivity;
import com.example.android.flixtrove.ui.detail.MovieDetailFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoviesListFragment
		extends Fragment
		implements ListItemClickListener, OnGlobalLayoutListener {
	/** Layout manager for recycler view */
	private GridLayoutManager layoutManager;

	/** Recycler view to display grid of movie posters */
	@BindView(R.id.recycler_view)
	RecyclerView recyclerView;

	/** Movie poster buffer indicator */
	@BindView(R.id.main_progress_bar)
	ProgressBar progressBar;

	/** Adapter for the view */
	private MovieRecyclerAdapter adapter;

	/** Movie API service */
	private MovieService apiConnection;

	/** Tag for popular movies sort option */
	public static final String INTENT_SORT_POPULAR_MOVIES = "PopularMovies";

	/** Tag for top rated movies sort option */
	public static final String INTENT_SORT_TOP_RATED_MOVIES = "TopRatedMovies";

	/** String to store sort option tag */
	private String sortAction;

	/** Flag for paginated scroll listener */
	private boolean isLoading = false;
	private boolean isLastPage = false;

	/** Total number of movie pages from the API */
	private int totalPages;

	/** Default constant value for current page */
	private static final int PAGE_START = 1;

	/** Store current movie page that is being parsed */
	private int currentPage = PAGE_START;

	public MoviesListFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setRetainInstance(true);

	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate grid view fragment layout
		View rootView = inflater.inflate(R.layout.fragment_grid_view, container, false);

		// Get all references to relevant views
		ButterKnife.bind(this, rootView);

		// Configure recycler view
		recyclerView.setHasFixedSize(true);
		// Prepare the layout manager and hand it over to the view
		prepareLayout();
		recyclerView.setLayoutManager(layoutManager);
		// Initialize adapter for recycler view
		adapter = new MovieRecyclerAdapter(getContext(), this);
		recyclerView.setAdapter(adapter);

		// Add scroll listener and enable pagination
		recyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
			@Override
			protected void loadMoreItems() {
				isLoading = true;
				currentPage++;

				loadNextPage();
			}

			@Override
			public int getTotalPageCount() {
				return totalPages;
			}

			@Override
			public boolean isLastPage() {
				return isLastPage;
			}

			@Override
			public boolean isLoading() {
				return isLoading;
			}
		});

		// Connect to API and fetch results
		apiConnection = MovieRepository.getClient().create(MovieService.class);
		loadFirstPage();

		// Return root view
		return rootView;
	}

	private void loadFirstPage() {
		// Clear existing data
		adapter.clear();

		// Make network call
		callMoviesApi().enqueue(new Callback<MainResponse>() {
			@Override
			public void onResponse(
					@NonNull Call<MainResponse> call,
					@NonNull Response<MainResponse> response
			) {
				List<Movie> movies = fetchMovies(response);
				adapter.addAll(movies);

				// Hide progress bar
				progressBar.setVisibility(View.GONE);

				// Check if current page is last page
				if (currentPage == totalPages) {
					isLastPage = true;
				}
			}

			@Override
			public void onFailure(@NonNull Call<MainResponse> call, @NonNull Throwable t) {
				Timber.e(t.toString());
			}
		});
	}

	private Call<MainResponse> callMoviesApi() {
		if (sortAction != null && sortAction.equals(INTENT_SORT_TOP_RATED_MOVIES)) {
			Timber.d("Fetching Top Rated Movies. Current Page: %s", currentPage);
			return apiConnection.getTopRatedMovies(PrivateApiKey.YOUR_API_KEY, currentPage);
		} else if (sortAction != null && sortAction.equals(INTENT_SORT_POPULAR_MOVIES)) {
			Timber.d("Fetching Popular Movies. Current Page: %s", currentPage);
			return apiConnection.getPopularMovies(PrivateApiKey.YOUR_API_KEY, currentPage);
		} else {
			Timber.d("Fetching Discover Movies. Current Page: %s", currentPage);
			return apiConnection.getMovies(PrivateApiKey.YOUR_API_KEY, currentPage, "US");
		}
	}

	private List<Movie> fetchMovies(Response<MainResponse> response) {
		// Parse the raw response from the API
		MainResponse rawResponse = response.body();

		// Get total number of pages from this call
		assert rawResponse != null;
		totalPages = rawResponse.getTotalPages();

		// Return the list of movies
		return rawResponse.getMovies();
	}

	private void loadNextPage() {
		// Make network call
		callMoviesApi().enqueue(new Callback<MainResponse>() {
			@Override
			public void onResponse(
					@NonNull Call<MainResponse> call,
					@NonNull Response<MainResponse> response
			) {
				isLoading = false;

				List<Movie> movies = fetchMovies(response);
				adapter.addAll(movies);

				if (currentPage == totalPages) {
					isLastPage = true;
				}
			}

			@Override
			public void onFailure(@NonNull Call<MainResponse> call, @NonNull Throwable t) {
				Timber.e(t.toString());
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_top_rated:
				// User clicked sort by top rated movies menu item
				sortAction = INTENT_SORT_TOP_RATED_MOVIES;
				// Reset current page
				currentPage = PAGE_START;
				// Load first page of top rated movies
				loadFirstPage();
				// Click handled successfully
				return true;

			case R.id.action_popular:
				// User clicked sort by popular movies menu item
				sortAction = INTENT_SORT_POPULAR_MOVIES;
				// Reset current page
				currentPage = PAGE_START;
				// Load first page of popular movies
				loadFirstPage();
				// Click handled successfully
				return true;
		}

		// Default behaviour
		return super.onOptionsItemSelected(item);
	}

	private void prepareLayout() {
		// Configure layout manager
		layoutManager = new GridLayoutManager(
				getContext(),
				1,
				GridLayoutManager.VERTICAL,
				false
		);

		// Handle span count to support different screen sizes
		recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(this);
	}

	@Override
	public void onListItemClick(int clickedItemMovieId) {
		// Build intent to fire detailed pane
		Intent detailViewRequestIntent = new Intent(getContext(), MovieDetailActivity.class);

		// Pass in the movie ID that was clicked to get the relevant movie details
		detailViewRequestIntent.putExtra(MovieDetailFragment.INTENT_MOVIE_ID, clickedItemMovieId);

		startActivity(detailViewRequestIntent);
	}

	@Override
	public void onGlobalLayout() {
		// This works only for devices with at least Jelly Bean or above
		recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

		// Width of the recycler view
		int viewWidth = recyclerView.getMeasuredWidth();

		// Poster width
		float posterImageWidth = getResources().getDimension(R.dimen.poster_width);

		// Find how many posters can be accommodated within the recycler view
		int newSpanCount = (int) Math.floor(viewWidth / posterImageWidth);

		// Set calculated span count on the layout manager
		layoutManager.setSpanCount(newSpanCount);
		layoutManager.requestLayout();
	}
}
