package com.example.ericm_000.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment {

//    private String mPosterPath;
    private MovieInfo mMovieInfo;

    public MovieDetailFragment() {
    }

//    http://image.tmdb.org/t/p/w185/6bCplVkhowCjTHXWv49UjRPn0eK.jpg

    private static final String TMDB_POSTER_URL = "http://image.tmdb.org/t/p/";
    private static final String TMDB_POSTER_SIZE = "w342"; //w185 ou w342

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("MovieInfo")) {
            mMovieInfo = (MovieInfo) intent.getSerializableExtra("MovieInfo");

            // Load text view
//            TextView tv = (TextView) rootView.findViewById(R.id.textview_movie_detail_title);
//            tv.setText(mMovieInfo.getmTitle());

            android.support.v7.widget.Toolbar tb = (android.support.v7.widget.Toolbar) rootView.findViewById(R.id.toolbar_movie_detail);
            tb.setTitle(mMovieInfo.getmTitle());

            TextView tv = (TextView) rootView.findViewById(R.id.textview_movie_detail_releasedate);
            tv.setText("Release Date: " + mMovieInfo.getReleaseDateAsString());

            tv = (TextView) rootView.findViewById(R.id.textview_movie_detail_popularity);
            tv.setText("Popularity: " + mMovieInfo.getPopularityAsString());

            tv = (TextView) rootView.findViewById(R.id.textview_movie_detail_overview);
            tv.setText(mMovieInfo.getmDescription());

            // Load the image using Picasso API
            ImageView posterView = (ImageView)rootView.findViewById(R.id.imageview_movie_detail);
            Picasso.with(getActivity()).load(TMDB_POSTER_URL + TMDB_POSTER_SIZE + mMovieInfo.getmPosterPath()).into(posterView);
        }

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
