package com.example.android.flixtrove.service.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieRepository {
	/** Retrofit object */
	private static Retrofit retrofit = null;

	public static Retrofit getClient() {
		// When retrofit has not been initialized
		if (retrofit == null) {
			retrofit = new Retrofit.Builder()
					.baseUrl(MovieService.BASE_URL)
					.addConverterFactory(GsonConverterFactory.create())
					.build();
		}

		// Return object
		return retrofit;
	}

}
