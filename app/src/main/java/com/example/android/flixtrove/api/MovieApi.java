package com.example.android.flixtrove.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieApi {
	/** Base API query url */
	private static final String BASE_URL = "https://api.themoviedb.org/3/";

	/** Retrofit object */
	private static Retrofit retrofit = null;

	public static Retrofit getClient() {
		// When retrofit has not been initialized
		if (retrofit == null) {
			retrofit = new Retrofit.Builder()
					.baseUrl(BASE_URL)
					.addConverterFactory(GsonConverterFactory.create())
					.build();
		}

		// Return object
		return retrofit;
	}

}
