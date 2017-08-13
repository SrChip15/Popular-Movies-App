package com.example.android.flixtrove;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

final class JsonParser {

	private static final String LOG_TAG = JsonParser.class.getSimpleName();

	private JsonParser() {
	}

	static List<Movie> extractFeaturesFromJson(String jsonString) {
		if (TextUtils.isEmpty(jsonString)) {
			return null;
		}

		// Initialize list to hold movies
		List<Movie> movies = new ArrayList<>();

		// Try to parse json response. If there's a problem with the way the JSON
		// is formatted, a JSONException exception object will be thrown.
		// Catch the exception so the app doesn't crash, and print the error message to the logs.
		try {
			// Parse the json string
			JSONObject response = new JSONObject(jsonString);

			// Get results array that hold all the movies
			JSONArray moviesArray = response.getJSONArray("results");

			// Traverse all movies and get poster for each movie
			int numberOfMovies = moviesArray.length();
			if (numberOfMovies > 0) {
				for (int i = 0; i < numberOfMovies; i++) {
					// Get movie at index i
					JSONObject currentMovie = moviesArray.getJSONObject(i);

					// Get the movie's poster
					String moviePosterPath = currentMovie.getString("poster_path");

					// Add movie to list
					movies.add(new Movie(moviePosterPath));
				}
			}

			// Return the populated list
			return movies;

		} catch (JSONException je) {
			Log.v(LOG_TAG, "Problem parsing the json response");
		}

		// return nothing if there is problem with JSON extraction
		return null;
	}
}
