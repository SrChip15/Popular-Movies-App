package com.example.android.flixtrove;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Helper methods related to requesting and receiving
 */
final class NetworkUtils {

	private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

	/**
	 * Prohibit instantiating objects of this class
	 */
	private NetworkUtils() {
	}

	static List<Movie> loadMovies(String queryUrl) {
		// Parse url
		URL url = createUrl(queryUrl);

		// Perform HTTP request to the URL and receive a JSON response back
		String jsonResponseString = null;

		// Extract information from json response
		try {
			jsonResponseString = makeHttpRequest(url);
		} catch (IOException ioe) {
			Log.e(LOG_TAG, "Problem making the HTTP request to " + url.toString());
		}

		// Extract and return the required information from the JSON response string
		return JsonParser.extractFeaturesFromJson(jsonResponseString);

	}

	private static URL createUrl(String stringUrl) {
		URL url;
		try {
			url = new URL(stringUrl);
		} catch (MalformedURLException exception) {
			Log.e(LOG_TAG, "Problem building the URL ", exception);
			return null;
		}
		return url;
	}

	private static String makeHttpRequest(URL url) throws IOException {
		String jsonResponseString = "";

		// If the URL is null, then return early
		if (url == null) {
			return jsonResponseString;
		}

		HttpURLConnection urlConnection = null;
		InputStream inputStream = null;
		try {
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setReadTimeout(10_000 /* milliseconds */);
			urlConnection.setConnectTimeout(15_000 /* milliseconds */);
			urlConnection.connect();
			// Check for successful connection response code
			if (urlConnection.getResponseCode() == 200) {
				// Connection established
				inputStream = urlConnection.getInputStream();
				jsonResponseString = readFromStream(inputStream);
			} else {
				// Connection failure
				// Log error in connection
				Log.e(LOG_TAG, "Error Code: " + urlConnection.getResponseCode());
			}
		} catch (IOException e) {
			Log.e(LOG_TAG, "Problem retrieving the movie JSON results. ", e);
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
			if (inputStream != null) {
				// Closing the input stream could throw an IOException, which is why
				// the makeHttpRequest(URL url) method signature specifies than an IOException
				// could be thrown.
				inputStream.close();
			}
		}
		return jsonResponseString;
	}

	/**
	 * Convert the {@link InputStream} into a String which contains the
	 * whole JSON response from the server.
	 */
	private static String readFromStream(InputStream inputStream) throws IOException {
		StringBuilder output = new StringBuilder();
		if (inputStream != null) {
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream,
					Charset.forName("UTF-8")); // decoding the input bits
			BufferedReader reader = new BufferedReader(inputStreamReader);
			String line = reader.readLine();
			while (line != null) { // until all data has been transmitted from the http request
				output.append(line);
				line = reader.readLine(); // parse next line of decoded input stream
			}
		}
		return output.toString(); // return string from string builder object
	}
}
