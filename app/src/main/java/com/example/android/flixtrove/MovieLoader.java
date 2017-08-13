package com.example.android.flixtrove;

import android.content.AsyncTaskLoader;
import android.content.Context;

/**
 * Sub-class AsyncTaskLoader to load movies on a background thread pool
 */
public class MovieLoader extends AsyncTaskLoader {
	/** Url to query the movie database API */
	private String queryUrl;

	public MovieLoader(Context context, String url) {
		super(context);
		this.queryUrl = url;
	}

	@Override
	protected void onStartLoading() {
		forceLoad();
	}

	@Override
	public Object loadInBackground() {
		// Check for valid string url
		if (queryUrl == null || queryUrl.isEmpty()) {
			// Bail early
			return null;
		}

		// Query the API and return the list of movies
		return NetworkUtils.loadMovies(queryUrl);
	}
}
