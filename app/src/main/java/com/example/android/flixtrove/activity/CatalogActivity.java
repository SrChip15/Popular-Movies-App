package com.example.android.flixtrove.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;

import com.example.android.flixtrove.PrivateApiKey;
import com.example.android.flixtrove.R;
import com.example.android.flixtrove.adapter.MovieRecyclerAdapter;
import com.example.android.flixtrove.adapter.PaginationScrollListener;
import com.example.android.flixtrove.api.MovieApi;
import com.example.android.flixtrove.api.MovieApiEndpoints;
import com.example.android.flixtrove.pojos.MainResponse;
import com.example.android.flixtrove.pojos.Movie;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CatalogActivity extends AppCompatActivity {
	/**
	 * Tag for log messages
	 */
	private static final String TAG = CatalogActivity.class.getSimpleName();

	/** Start from page 1 */
	private static final int START_PAGE = 1;

	/**
	 * API network call handler
	 */
	private static Retrofit retrofit = null;

	/**
	 * Recycler view to host list of now playing movies
	 */
	@BindView(R.id.recycler_view)
	RecyclerView recyclerView;

	@BindView(R.id.main_progress_bar)
	ProgressBar progressBar;

	/**
	 * Movie adapter for populating the recycler view
	 */
	private MovieRecyclerAdapter adapter;

	/** Movie API endpoints object */
	private MovieApiEndpoints apiConnection;

	/** Flag for fetching data over network */
	private boolean isLoading = false;

	/** Flag when total number of available pages in API is reached */
	private boolean isLastPage = false;

	/** Current page being fetched */
	private int currentPage = START_PAGE;

	/** Total number of available pages from the API. Def */
	private int totalPages;

	/** Layout manager to display movie posters in a grid */
	private GridLayoutManager layoutManager;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Inflate UI
		setContentView(R.layout.activity_main);

		// Bind ButterKnife to UI
		ButterKnife.bind(CatalogActivity.this);

		// Initialize adapter
		adapter = new MovieRecyclerAdapter(this);

		recyclerView.setHasFixedSize(true);

		// layout manager for adapter
		layoutManager =
				new GridLayoutManager
						(
								this,
								1,              // Below global layout listener handles span count
								GridLayoutManager.VERTICAL,
								false           // reverse layout
						);

		recyclerView.setLayoutManager(layoutManager);

		// Default animation for item in list
		recyclerView.setItemAnimator(new DefaultItemAnimator());

		// Set number of movie posters to display per row
		// This works for every android device (Jelly Bean+) of varying screen sizes
		recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					// This works only for devices with at least Jelly Bean or above
					recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
					int viewWidth = recyclerView.getMeasuredWidth();
					float posterImageWidth = getResources().getDimension(R.dimen.poster_width) - 10;
					int newSpanCount = (int) Math.floor(viewWidth / posterImageWidth);
					layoutManager.setSpanCount(newSpanCount);
					layoutManager.requestLayout();
				}
			}
		});

		// Set the movieAdapter to the view
		recyclerView.setAdapter(adapter);

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
		apiConnection = MovieApi.getClient().create(MovieApiEndpoints.class);

		// Connect to API and fetch results
		loadFirstPage();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		switch (id) {
			case R.id.action_top_rated:
				// TODO - Fire up TopRatedMoviesActivity
				return true;
			case R.id.action_most_popular:
				// TODO - Fire up MostPopularMoviesActivity
				// Display popular movies
				return true;
		}

		// Android framework handles default
		return super.onOptionsItemSelected(item);
	}

	private void loadFirstPage() {
		// Clear old data
		adapter.clear();

		// Make network call
		callMoviesApi().enqueue(new Callback<MainResponse>() {
			@Override
			public void onResponse(@NonNull Call<MainResponse> call, @NonNull Response<MainResponse> response) {
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

	private void loadNextPage() {
		callMoviesApi().enqueue(new Callback<MainResponse>() {
			@Override
			public void onResponse(@NonNull Call<MainResponse> call, @NonNull Response<MainResponse> response) {
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
}
