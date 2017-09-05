package com.example.android.flixtrove.ui;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoviesListFragment extends Fragment implements ListItemClickListener {
	private GridLayoutManager layoutManager;

	private ProgressBar progressBar;

	private MovieRecyclerAdapter adapter;

	private MovieService apiConnection;

	public static final String INTENT_SORT_POPULAR_MOVIES = "PopularMovies";

	public static final String INTENT_SORT_TOP_RATED_MOVIES = "TopRatedMovies";

	private String sortAction;

	private boolean isLoading = false;

	private boolean isLastPage = false;

	private int totalPages;

	private static final int PAGE_START = 1;

	private int currentPage = PAGE_START;

	public MoviesListFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate grid view fragment layout
		View rootView = inflater.inflate(R.layout.fragment_grid_view, container, false);

		// Get a reference to the recycler view
		final RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

		// Get a reference to the progress bar
		progressBar = (ProgressBar) rootView.findViewById(R.id.main_progress_bar);

		// Configure recycler view
		recyclerView.setHasFixedSize(true);
		// Configure layout manager
		layoutManager = new GridLayoutManager(
				getContext(),
				1,
				GridLayoutManager.VERTICAL,
				false
		);
		recyclerView.setLayoutManager(layoutManager);
		// Handle span count to support different screen sizes
		recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
							// This works only for devices with at least Jelly Bean or above
							recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

							int viewWidth = recyclerView.getMeasuredWidth();

							float posterImageWidth = getResources()
									.getDimension(R.dimen.poster_width);

							int newSpanCount = (int) Math.floor(viewWidth / posterImageWidth);

							layoutManager.setSpanCount(newSpanCount);

							layoutManager.requestLayout();
						}
					}
				});
		// Initialize adapter for recycler view
		adapter = new MovieRecyclerAdapter(getContext(), this);
		recyclerView.setAdapter(adapter);
		// Enable pagination
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

		// Connect to the api
		apiConnection = MovieRepository.getClient().create(MovieService.class);

		// Connect to API and fetch results
		loadFirstPage();

		// Return root view
		return rootView;

	}

	private void loadFirstPage() {
		// Clear old data
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
				Log.e(TAG, t.toString());
			}
		});
	}

	private Call<MainResponse> callMoviesApi() {
		if (sortAction != null && sortAction.equals(INTENT_SORT_TOP_RATED_MOVIES)) {
			Log.d(TAG, "Fetching Top Rated Movies. Current Page: " + currentPage);
			return apiConnection.getTopRatedMovies(PrivateApiKey.YOUR_API_KEY, currentPage);
		} else if (sortAction != null && sortAction.equals(INTENT_SORT_POPULAR_MOVIES)) {
			Log.d(TAG, "Fetching Popular Movies. Current Page: " + currentPage);
			return apiConnection.getPopularMovies(PrivateApiKey.YOUR_API_KEY, currentPage);
		} else {
			Log.d(TAG, "Fetching Discover Movies. Current Page: " + currentPage);
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
				Log.e(TAG, t.toString());
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

	@Override
	public void onListItemClick(int clickedItemMovieId) {
		Intent detailViewRequestIntent = new Intent(getContext(), MovieDetailActivity.class);
		detailViewRequestIntent.putExtra(MovieDetailFragment.INTENT_MOVIE_ID, clickedItemMovieId);
		startActivity(detailViewRequestIntent);
	}
}
