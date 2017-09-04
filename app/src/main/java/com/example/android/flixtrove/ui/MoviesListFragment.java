package com.example.android.flixtrove.ui;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;

import com.example.android.flixtrove.PrivateApiKey;
import com.example.android.flixtrove.R;
import com.example.android.flixtrove.adapter.MovieRecyclerAdapter;
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
public class MoviesListFragment extends Fragment {

	private GridLayoutManager layoutManager;

	private ProgressBar progressBar;

	private MovieRecyclerAdapter adapter;

	private MovieService apiConnection;

	private boolean isLoading = false;

	private boolean isLastPage = false;

	private int totalPages;

	private static final int PAGE_START = 1;

	private int currentPage = PAGE_START;

	public MoviesListFragment() {
		// Required empty public constructor
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
		adapter = new MovieRecyclerAdapter(getContext());
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
		return apiConnection.getMovies(PrivateApiKey.YOUR_API_KEY, currentPage, "US");
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

}
