package com.example.ericm_000.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;


///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link MainFragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link MainFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class MainFragment extends Fragment {

    private final static  String LOG_TAG = MainFragment.class.getSimpleName();
//    private ArrayAdapter<String> mMoviesAdapter;
    private MovieInfoAdapter mMovieInfoAdapter;
    private List<MovieInfo> mMoviesList;

    public MainFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mMoviesList = new ArrayList<MovieInfo>();

        // Create the adapter
/*        ArrayList<String> movies = new ArrayList<String>();
        mMoviesAdapter = new ArrayAdapter<String>(getActivity()
        , R.layout.list_item_movie
        , R.id.list_item_movies_textview
        , movies);

        ListView listViewMovies = (ListView) rootView.findViewById(R.id.listview_movies);
        if (listViewMovies != null) {
            listViewMovies.setAdapter(mMoviesAdapter);
        } else {
            Log.e(LOG_TAG, "Could not find the list view for movies");
        }

        // Open the MovieDetailActivity activity when an item is clicked
        listViewMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detailIntent = new Intent(getActivity(), MovieDetailActivity.class);
                detailIntent.putExtra(Intent.EXTRA_TEXT, mMoviesList.get(position).getmPosterPath());
                startActivity(detailIntent);
            }
        });*/

        mMovieInfoAdapter = new MovieInfoAdapter(getActivity()
            , R.layout.list_item_movie
            , R.id.list_item_movies_textview
            , mMoviesList);
        GridView gridViewMovies = (GridView) rootView.findViewById(R.id.gridview_movies);

        if (gridViewMovies != null) {
            gridViewMovies.setAdapter(mMovieInfoAdapter);
            gridViewMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent detailIntent = new Intent(getActivity(), MovieDetailActivity.class);
                    detailIntent.putExtra("MovieInfo", mMoviesList.get(position));
                    startActivity(detailIntent);
                }
            });
        } else {
            Log.e(LOG_TAG, "Could not find the grid view for movies");
        }

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }
        // TODO emorand Refresh should be deleted on shipment
        if (item.getItemId() == R.id.action_refresh) {
            updateMovies();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mainfragment, menu);
    }

    private void updateMovies() {
        new FetchMoviesTask().execute("");
    }

    private void sortAddMovieInfo() {

        // Sort according to preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = getString(R.string.pref_sort_bypopularity);
        if (sharedPref != null) {
            sortBy = sharedPref.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_bypopularity));
        }
        if (sortBy.equals(getString(R.string.pref_sort_byreleasedate))) {
            Collections.sort(mMoviesList, new MovieInfo.ReleaseDateComparator());
        } else if (sortBy.equals(getString(R.string.pref_sort_bytitle))) {
            Collections.sort(mMoviesList, new MovieInfo.TitleComparator());
        } else {
            // default behavior
            Collections.sort(mMoviesList, new MovieInfo.PopularityComparator());
        }

        // Add to the adapter
        mMovieInfoAdapter.clear();
        mMovieInfoAdapter.addAll(mMoviesList);
    }

    /**
     * Retrieve movie information from themoviedb's API
     */
    public class FetchMoviesTask extends AsyncTask<String, Void, List<MovieInfo>> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private final String MOVIES_BASE_URL = "https://api.themoviedb.org/3/discover/movie?";
        private final String MOVIES_AFTER = "primary_release_date.gte";
        private final String MOVIES_BEFORE = "primary_release_date.lte";
        private final String MOVIES_APIKEY = "api_key";

        @Override
        protected void onPostExecute(List<MovieInfo> movies) {
            // Add new data to the adapter
            if (movies == null) {
                Log.e(LOG_TAG, "No data retrieved from the movie database");
                return;
            }
            mMoviesList = movies;
            sortAddMovieInfo();
        }

        @Override
        protected List<MovieInfo> doInBackground(String... params) {

            // Declared outside the try/catch to be closed in the finally
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            List<MovieInfo> ret = null;

            try {
                // Recover the movies for the past month
                SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calendar = new GregorianCalendar(); //Init to day by default
                String moviesBefore = dateFormater.format(calendar.getTime());
                calendar.roll(Calendar.MONTH, false);
                String moviesAfter = dateFormater.format(calendar.getTime());

                // Build the query
                Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendQueryParameter(MOVIES_AFTER, moviesAfter)
                        .appendQueryParameter(MOVIES_BEFORE, moviesBefore)
                        .appendQueryParameter(MOVIES_APIKEY, getString(R.string.apikey))
                        .build();
                String urlString = builtUri.toString();
                Log.v(LOG_TAG, "Generated the URL: " + urlString);
                URL url = new URL(urlString);

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                // TODO emorand this seem to be called more than once
//                Log.v(LOG_TAG,buffer.toString());

                try {
                    ret = parseMoviesData(buffer.toString());
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "JSONException parsing: " + buffer.toString() + " " + e);
                }

            } catch (IOException ex) {
                Log.e(LOG_TAG, ex.getMessage());
            }finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }


            return ret;
        }

        private List<MovieInfo> parseMoviesData(String JSON) throws JSONException {
            final String TMDB_RESULTS = "results";
            final String TMDB_POSTER = "poster_path";
            final String TMDB_RELEASE_DATE = "release_date";
            final String TMDB_TITLE = "title";
            final String TMDB_POPULARITY = "popularity";
            final String TMDB_DESCRIPTION = "overview";

            JSONObject moviesJson = new JSONObject(JSON);
            JSONArray moviesArray = moviesJson.getJSONArray(TMDB_RESULTS);
            int lentgh = moviesArray.length();

            List<MovieInfo> ret = new ArrayList<MovieInfo>();
            for (int i = 0 ; i < lentgh ; i++) {
                JSONObject movie = moviesArray.getJSONObject(i);
                MovieInfo mi = new MovieInfo();
                mi.setmTitle(movie.getString(TMDB_TITLE));
                mi.setmPopularity(movie.getDouble(TMDB_POPULARITY));
                mi.setmReleaseDate(movie.getString(TMDB_RELEASE_DATE));
                mi.setmPosterPath(movie.getString(TMDB_POSTER));
                mi.setmDescription(movie.getString(TMDB_DESCRIPTION));
                ret.add(mi);
            }

            return ret;
        }

    }
}
