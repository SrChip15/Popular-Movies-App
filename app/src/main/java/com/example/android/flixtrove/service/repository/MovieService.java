package com.example.android.flixtrove.service.repository;

import com.example.android.flixtrove.service.model.MainResponse;
import com.example.android.flixtrove.service.model.MovieDetailResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
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

	@GET("movie/popular")
	Call<MainResponse> getPopularMovies(
			@Query("api_key") String apiKey,
			@Query("page") int pageNumber
	);

	@GET("movie/{movie_id}")
	Call<MovieDetailResponse> getMovie (
			@Path("movie_id") int movieId,
			@Query("api_key") String apiKey
	);

}
