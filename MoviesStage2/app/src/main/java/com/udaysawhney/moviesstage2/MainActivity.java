package com.udaysawhney.moviesstage2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.udaysawhney.moviesstage2.Activities.SettingsActivity;
import com.udaysawhney.moviesstage2.Model.Movie;
import com.udaysawhney.moviesstage2.Model.MovieResponse;
import com.udaysawhney.moviesstage2.RestApi.Client;
import com.udaysawhney.moviesstage2.RestApi.Service;
import com.udaysawhney.moviesstage2.ViewAdapters.MoviesAdapter;
import com.udaysawhney.moviesstage2.ViewModel.MainViewModel;
import com.udaysawhney.moviesstage2.roomDatabase.FavouriteEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.R.attr.theme;
import static android.R.attr.x;
import static java.lang.System.in;

public class MainActivity extends AppCompatActivity  {

    private RecyclerView recyclerView;
    private MoviesAdapter adapter;
    ProgressDialog pd;
    private AppCompatActivity activity = MainActivity.this;
    public static final String LOG_TAG = MainActivity.class.getName();

    private static String LIST_STATE = "list_state";
    private Parcelable savedRecyclerLayoutState;
    private static final String BUNDLE_RECYCLER_LAYOUT = "recycler_layout";
    private ArrayList<Movie> moviesInstance = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null){
            moviesInstance = savedInstanceState.getParcelableArrayList(LIST_STATE);
            //Log.d("adding movie",Integer.toString(moviesInstance.size()));
            savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
            displayData();
        }else {
            //Log.d("empty saved Instance","no movies");
            initViews();
        }
    }

    private void displayData(){
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        Log.d(LOG_TAG,"Number of saved movies " + Integer.toString(moviesInstance.size()));
        adapter = new MoviesAdapter(this, moviesInstance);
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        restoreLayoutManagerPosition();
        adapter.notifyDataSetChanged();
    }

    private void restoreLayoutManagerPosition() {
        if (savedRecyclerLayoutState != null) {
            recyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    private void initViews(){
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        loadJSON();
    }

    private void initViews2(){
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        getAllFavorite();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList(LIST_STATE, moviesInstance);
        savedInstanceState.putParcelable(BUNDLE_RECYCLER_LAYOUT, recyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        moviesInstance = savedInstanceState.getParcelableArrayList(LIST_STATE);
        savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
        super.onRestoreInstanceState(savedInstanceState);
    }


    public Activity getActivity(){
        Context context = this;
        while (context instanceof ContextWrapper){
            if (context instanceof Activity){
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;

    }

    private void loadJSON(){
        String sortOrder = checkSortOrder();
        Log.d(LOG_TAG,sortOrder);
        if (sortOrder.equals(this.getString(R.string.pref_most_popular))) {
            Log.d(LOG_TAG,"loading most popular movies");
            try {
                if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please obtain API Key firstly from themoviedb.org", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                    return;
                }

                Client Client = new Client();
                Service apiService =
                        Client.getClient().create(Service.class);
                Call<MovieResponse> call = apiService.getPopularMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN);
                call.enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "the value is " + moviesInstance.size(), Toast.LENGTH_SHORT).show();

                            if (response.body() != null) {
                                List<Movie> movies = response.body().getResults();
                                //moviesInstance.clear();
                                moviesInstance.addAll(movies);
                                recyclerView.setAdapter(new MoviesAdapter(getApplicationContext(), movies));
                                recyclerView.smoothScrollToPosition(0);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieResponse> call, Throwable t) {
                        Log.d("Error", t.getMessage());
                        Toast.makeText(MainActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();

                    }
                });
            } catch (Exception e) {
                Log.d("Error", e.getMessage());
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        } else if (sortOrder.equals(this.getString(R.string.pref_highest_rated))){
            try {
                if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please obtain API Key firstly from themoviedb.org", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                    return;
                }

                Client Client = new Client();
                Service apiService =
                        Client.getClient().create(Service.class);
                Call<MovieResponse> call = apiService.getTopRatedMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN);
                call.enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        List<Movie> movies = response.body().getResults();
                        //moviesInstance.clear();
                        moviesInstance.addAll(movies);
                        recyclerView.setAdapter(new MoviesAdapter(getApplicationContext(), movies));
                        recyclerView.smoothScrollToPosition(0);
                    }

                    @Override
                    public void onFailure(Call<MovieResponse> call, Throwable t) {
                        Log.d("Error", t.getMessage());
                        Toast.makeText(MainActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();

                    }
                });
            } catch (Exception e) {
                Log.d("Error", e.getMessage());
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d(LOG_TAG,"going to database");
            initViews2();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String checkSortOrder(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sortOrder = preferences.getString(
                this.getString(R.string.pref_sort_order_key),
                this.getString(R.string.pref_most_popular)
        );

        return sortOrder;
    }

    private void getAllFavorite(){
        Log.d(LOG_TAG,"Number of saved movies " + Integer.toString(moviesInstance.size()));
            adapter = new MoviesAdapter(this, moviesInstance);

            MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
            viewModel.getFavorite().observe(this, new Observer<List<FavouriteEntry>>() {
                @Override
                public void onChanged(@Nullable List<FavouriteEntry> imageEntries) {
                    List<Movie> movies = new ArrayList<>();
                    for (FavouriteEntry entry : imageEntries) {
                        Movie movie = new Movie();
                        movie.setId(entry.getMovieid());
                        movie.setOverview(entry.getOverview());
                        movie.setOriginalTitle(entry.getTitle());
                        movie.setPosterPath(entry.getPosterpath());
                        movie.setVoteAverage(entry.getUserrating());

                        movies.add(movie);
                        Log.d(LOG_TAG,"movie in database" + entry.getTitle());
                    }

                    adapter.setMovies(movies);
                    moviesInstance.addAll(movies);
                    recyclerView.setAdapter(adapter);
                    recyclerView.smoothScrollToPosition(0);
                }
            });
    }
}

