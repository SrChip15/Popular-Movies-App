package com.example.android.flixtrove;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity
		extends AppCompatActivity
		implements LoaderCallbacks<List<Movie>> {

	private static final int MOVIE_LOADER_ID = 1;
	@BindView(R.id.recycler_view)
	RecyclerView recyclerView;
	private MovieRecyclerAdapter movieAdapter;
	private String QUERY_URL_STRING =
			"https://api.themoviedb.org/3/movie/now_playing?api_key=" +
					PrivateApiKey.YOUR_API_KEY +
					"&language=en-US&page=1&region=US";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Inflate UI
		setContentView(R.layout.activity_main);

		// Bind ButterKnife to UI
		ButterKnife.bind(MainActivity.this);

		// Optimize recyclerView
		recyclerView.setHasFixedSize(true);

		// Connect the {@link RecyclerView} widget to a GridView layout
		// Get the current orientation of the screen
		int orientation = this.getResources().getConfiguration().orientation;
		// Set span count based on orientation
		if (orientation == Configuration.ORIENTATION_PORTRAIT) {
			recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
		} else {
			recyclerView.setLayoutManager(new GridLayoutManager(this, 5));
		}

		// Initialize movieAdapter with empty list
		movieAdapter = new MovieRecyclerAdapter(new ArrayList<Movie>());

		// Set the movieAdapter to the view
		recyclerView.setAdapter(movieAdapter);

		// Get a reference to the loader manager in order to interact with the loaders
		LoaderManager loaderManager = getLoaderManager();

		// Initialize the loader with required parameters
		loaderManager.initLoader(MOVIE_LOADER_ID, null, MainActivity.this);
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
				// Display top rated movies
				return true;
			case R.id.action_most_popular:
				// Display popular movies
				return true;
		}

		// Android framework handles default
		return super.onOptionsItemSelected(item);
	}

	@Override
	public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
		// Kick start the loader to fetch movies
		return new MovieLoader(MainActivity.this, QUERY_URL_STRING);
	}

	@Override
	public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movies) {
		// Clear movieAdapter of old data
		this.movieAdapter.clear();

		// Check if movies are loaded
		if (movies != null && !movies.isEmpty()) {
			this.movieAdapter.addAll(movies);
		}
	}

	@Override
	public void onLoaderReset(Loader<List<Movie>> loader) {
		// Loader is being destroyed since current data is no longer valid
		// Clear out movieAdapter's old data set
		this.movieAdapter.clear();
	}
}
