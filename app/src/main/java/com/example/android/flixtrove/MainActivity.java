package com.example.android.flixtrove;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

	@BindView(R.id.tv_url)
	TextView displayUrl;

	@BindView(R.id.button_top_rated_web)
	Button showInWeb;

	@BindView(R.id.iv_picasso_test)
	ImageView picassoTester;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ButterKnife.bind(MainActivity.this);

		picassoTester.setVisibility(View.INVISIBLE);

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
				// Display top rated movies
				return true;
			case R.id.action_most_popular:
				// Display popular movies
				return true;
		}

		// Android framework handles default
		return super.onOptionsItemSelected(item);
	}

	public void showTopRatedMoviesInWeb(View view) {
		String query_api_key = "api_key";
		String key = PrivateApiKey.YOUR_API_KEY;
		String searchListType = "top_rated";
		Uri.Builder builder = new Uri.Builder();
		builder.scheme("https")
				.authority("api.themoviedb.org")
				.appendPath("3")
				.appendPath("movie")
				.appendPath(searchListType)
				.appendQueryParameter(query_api_key, key);

		Uri queryUri = builder.build();

		Intent webIntent = new Intent(Intent.ACTION_VIEW, queryUri);
		if (webIntent.resolveActivity(getPackageManager()) != null) {
			startActivity(webIntent);
		}
	}

	public void showPopularMoviesInWeb(View view) {
		String query_api_key = "api_key";
		String key = PrivateApiKey.YOUR_API_KEY;
		String searchListType = "popular";
		Uri.Builder builder = new Uri.Builder();
		builder.scheme("https")
				.authority("api.themoviedb.org")
				.appendPath("3")
				.appendPath("movie")
				.appendPath(searchListType)
				.appendQueryParameter(query_api_key, key);

		Uri queryUri = builder.build();

		Intent webIntent = new Intent(Intent.ACTION_VIEW, queryUri);
		if (webIntent.resolveActivity(getPackageManager()) != null) {
			startActivity(webIntent);
		}
	}
}
