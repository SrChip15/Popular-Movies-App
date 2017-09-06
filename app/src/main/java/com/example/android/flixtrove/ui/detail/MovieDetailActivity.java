package com.example.android.flixtrove.ui.detail;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.android.flixtrove.R;

public class MovieDetailActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Initialize list view fragment to attach to activity
		MovieDetailFragment detailFragment = new MovieDetailFragment();

		// Get a reference to the fragment manager to add the list view fragment to screen
		FragmentManager fragmentManager = getSupportFragmentManager();

		// Fragment transaction for displaying discover/movies API call
		fragmentManager.beginTransaction()
				.add(R.id.fragment_container, detailFragment)
				.commit();
	}

	/** Helper method to set movie title as the action bar title from the detailed pane fragment */
	public void setActionBarTitle(String title) {
		getSupportActionBar().setTitle(title);
	}
}
