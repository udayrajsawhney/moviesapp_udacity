package com.udaysawhney.moviesstage2.RestApi;

import com.udaysawhney.moviesstage2.Model.MovieResponse;
import com.udaysawhney.moviesstage2.Model.Review;
import com.udaysawhney.moviesstage2.Model.TrailerResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Service {

    @GET("movie/popular")
    Call<MovieResponse> getPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/top_rated")
    Call<MovieResponse> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("movie/{movie_id}/videos")
    Call<TrailerResponse> getMovieTrailer(@Path("movie_id") int id, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/reviews")
    Call<Review> getReview(@Path("movie_id") int id, @Query("api_key") String apiKey);
}
