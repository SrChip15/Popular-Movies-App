package com.example.android.flixtrove.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.flixtrove.R;

public class MainActivity extends AppCompatActivity {
	/** Tag for log messages */
	private static final String TAG = MainActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		MoviesListFragment listFragment = new MoviesListFragment();

		FragmentManager fragmentManager = getSupportFragmentManager();

		fragmentManager.beginTransaction()
				.add(R.id.fragment_list_container, listFragment)
				.commit();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		switch (id) {
			case R.id.action_top_rated:
				// TODO - display results from movies/top_rated API call
				return true;
			case R.id.action_most_popular:
				// TODO - display results from movies/popular API call
				// Display popular movies
				return true;
		}

		// Android framework handles default
		return super.onOptionsItemSelected(item);
	}
}
