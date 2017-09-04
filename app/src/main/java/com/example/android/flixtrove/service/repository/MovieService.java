package com.example.android.flixtrove.service.repository;

import com.example.android.flixtrove.service.model.MainResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MovieService {

	String BASE_URL = "https://api.themoviedb.org/3/";

	@GET("discover/movie")
	Call<MainResponse> getMovies(
			@Query("api_key") String apiKey,
			@Query("page") int pageNumber,
			@Query("region") String region
	);

	@GET("movie/top_rated")
	Call<MainResponse> getTopRatedMovies(
			@Query("api_key") String apiKey,
			@Query("page") int pageNumber
	);

	@GET("movie/most_popular")
	Call<MainResponse> getMostPopularMovies(
			@Query("api_key") String apiKey,
			@Query("page") int pageNumber
	);

}
