package com.example.android.flixtrove.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ProgressBar;

import com.example.android.flixtrove.PrivateApiKey;
import com.example.android.flixtrove.R;
import com.example.android.flixtrove.service.model.MainResponse;
import com.example.android.flixtrove.service.model.Movie;
import com.example.android.flixtrove.service.network.MovieRepository;
import com.example.android.flixtrove.service.network.MovieService;
import com.example.android.flixtrove.ui.adapter.MovieRecyclerAdapter;
import com.example.android.flixtrove.ui.adapter.MovieRecyclerAdapter.ListItemClickListener;
import com.example.android.flixtrove.ui.adapter.PaginationScrollListener;
import com.example.android.flixtrove.ui.detail.MovieDetailActivity;
import com.example.android.flixtrove.ui.detail.MovieDetailFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoviesListFragment
        extends Fragment
        implements ListItemClickListener, OnGlobalLayoutListener {

    /* Class variables */
    private GridLayoutManager layoutManager;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.main_progress_bar)
    ProgressBar progressBar;
    private MovieRecyclerAdapter adapter;
    private MovieService apiConnection;
    private String sortAction;
    private int totalPages;
    private int currentPage = FIRST_PAGE;
    @SuppressWarnings("FieldCanBeLocal")
    private PaginationScrollListener scrollListener;
    private List<Movie> movies = new ArrayList<>();

    /* Class Constants */
    public static final String INTENT_SORT_POPULAR_MOVIES = "PopularMovies";
    public static final String INTENT_SORT_TOP_RATED_MOVIES = "TopRatedMovies";
    private static final String KEY_RETAINED_STATE = "RetainedState";
    private static final int FIRST_PAGE = 1;

    public MoviesListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);
        ButterKnife.bind(this, rootView);

        prepareLayout();

        // Connect to API and fetch results
        apiConnection = MovieRepository.getClient().create(MovieService.class);

        if (savedInstanceState == null) {
            loadMovies();
        } else {
            //TODO - Figure out a way to remember scrolled place in list; currently, when the list
            // gets bigger or on multiple orientation changes, the adapter starts at beginning position
            adapter.addAll(movies);
            progressBar.setVisibility(View.GONE);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        movies = adapter.getData();
        outState.putBoolean(KEY_RETAINED_STATE, true);
    }

    private void loadMovies() {
        // Clear existing data
        adapter.clear();

        callMoviesApi(FIRST_PAGE).enqueue(new Callback<MainResponse>() {
            @Override
            public void onResponse(
                    @NonNull Call<MainResponse> call,
                    @NonNull Response<MainResponse> response
            ) {
                Timber.d("Fetch data from: %s", call.request().url().toString());
                List<Movie> movies = fetchMovies(response);
                adapter.addAll(movies);

                // Hide progress bar
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<MainResponse> call, @NonNull Throwable t) {
                Timber.e(t.toString());
            }
        });
    }

    private Call<MainResponse> callMoviesApi(int page) {
        //TODO - Fix page info arg for sort options
        if (sortAction != null && sortAction.equals(INTENT_SORT_TOP_RATED_MOVIES)) {
            Timber.d("Fetching Top Rated Movies. Current Page: %s", currentPage);
            return apiConnection.getTopRatedMovies(PrivateApiKey.YOUR_API_KEY, currentPage);
        } else if (sortAction != null && sortAction.equals(INTENT_SORT_POPULAR_MOVIES)) {
            Timber.d("Fetching Popular Movies. Current Page: %s", currentPage);
            return apiConnection.getPopularMovies(PrivateApiKey.YOUR_API_KEY, currentPage);
        } else {
            Timber.d("Fetching Discover Movies. Current Page: %s", page);
            return apiConnection.getMovies(PrivateApiKey.YOUR_API_KEY, page, "US");
        }
    }

    private List<Movie> fetchMovies(Response<MainResponse> response) {
        MainResponse rawResponse = response.body();
        assert rawResponse != null;
        totalPages = rawResponse.getTotalPages();
        Timber.d("Total number of pages: %s", totalPages);

        return rawResponse.getMovies();
    }

    private void loadNextPage(int page) {
        if (page <= totalPages) {
            callMoviesApi(page).enqueue(new Callback<MainResponse>() {
                @Override
                public void onResponse(
                        @NonNull Call<MainResponse> call,
                        @NonNull Response<MainResponse> response
                ) {
                    Timber.d("(Next Page) Fetch data from: %s", call.request().url().toString());
                    //isLoading = false;

                    List<Movie> movies = fetchMovies(response);
                    adapter.addAll(movies);
                }

                @Override
                public void onFailure(@NonNull Call<MainResponse> call, @NonNull Throwable t) {
                    Timber.e(t.toString());
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_top_rated:
                sortAction = INTENT_SORT_TOP_RATED_MOVIES;
                // Reset current page
                currentPage = FIRST_PAGE;
                // Load first page of top rated movies
                loadMovies();
                // Click handled successfully
                return true;

            case R.id.action_popular:
                // User clicked sort by popular movies menu item
                sortAction = INTENT_SORT_POPULAR_MOVIES;
                // Reset current page
                currentPage = FIRST_PAGE;
                // Load first page of popular movies
                loadMovies();
                // Click handled successfully
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void prepareLayout() {
        recyclerView.setHasFixedSize(true);

        adapter = new MovieRecyclerAdapter(getContext(), this);
        recyclerView.setAdapter(adapter);

        // Configure layout manager
        layoutManager = new GridLayoutManager(
                getContext(),
                1,
                GridLayoutManager.VERTICAL,
                false
        );

        // Handle span count to support different screen sizes
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(this);

        recyclerView.setLayoutManager(layoutManager);
        scrollListener = new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems(int page) {
                loadNextPage(page);
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
    }

    @Override
    public void onListItemClick(int clickedPosition, int clickedItemMovieId) {
        Intent detailViewRequestIntent = new Intent(getContext(), MovieDetailActivity.class);
        detailViewRequestIntent.putExtra(MovieDetailFragment.INTENT_MOVIE_ID, clickedItemMovieId);
        startActivity(detailViewRequestIntent);
    }

    /**
     * Detect possible number of columns on current screen and set the calculated span count for the
     * grid layout. IOW, enable consistent look across different screens
     */
    @Override
    public void onGlobalLayout() {
        // This works only for devices with at least Jelly Bean or above
        recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

        int viewWidth = recyclerView.getMeasuredWidth();
        float posterImageWidth = getResources().getDimension(R.dimen.poster_width);

        // Find how many posters can be accommodated within the recycler view
        int newSpanCount = (int) Math.floor(viewWidth / posterImageWidth);

        layoutManager.setSpanCount(newSpanCount);
        layoutManager.requestLayout();
    }
}
