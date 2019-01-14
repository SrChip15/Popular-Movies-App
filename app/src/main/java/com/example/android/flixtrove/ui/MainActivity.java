package com.example.android.flixtrove.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.flixtrove.R;
import com.example.android.flixtrove.ui.detail.MovieDetailFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get a reference to the fragment manager to add the list view fragment to screen
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment listFragment = fragmentManager.findFragmentById(R.id.fragment_container);

        // Initialize list view fragment
        if (listFragment == null) {
            listFragment = new MoviesListFragment();

            // Fragment transaction
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, listFragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Intent intent = getIntent();
        // Disable sort menu for detailed pane
        if (intent != null && intent.hasExtra(MovieDetailFragment.INTENT_MOVIE_ID)) {
            menu.removeItem(R.id.action_sort);
        }

        // Return modified menu
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Fragment handles sort options
        if (id == R.id.action_top_rated || id == R.id.action_popular) {
            return false;
        }

        // Android framework handles default
        return super.onOptionsItemSelected(item);
    }
}
