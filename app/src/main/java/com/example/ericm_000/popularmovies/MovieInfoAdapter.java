package com.example.ericm_000.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ericm_000 on 05/04/2016.
 */
public class MovieInfoAdapter extends ArrayAdapter<MovieInfo> {

    private static final String LOG_TAG = MovieInfoAdapter.class.getSimpleName();

    private static final String TMDB_POSTER_URL = "http://image.tmdb.org/t/p/";
    private static final String TMDB_POSTER_SIZE = "w342"; //w185 ou w342
    private static final double POSTER_RATIO = 1.5f;
    private int mPosterWidth;
    private int mPosterHeight;

    public MovieInfoAdapter(Context context, int resource, int textViewResourceId, List<MovieInfo> objects) {
        super(context, resource, textViewResourceId, objects);
        mPosterWidth = 0;
        mPosterHeight = 0;
        Picasso.with(context)
            .setIndicatorsEnabled(true);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieInfo mi = getItem(position);
        ImageView imageView = (ImageView) convertView;

        // Recycle if necessary
        if (convertView == null) {
            imageView = new ImageView(getContext());
            // Properties of the imageview
            imageView.setAdjustViewBounds(false);
            imageView.setCropToPadding(true);
            imageView.setLayoutParams(new GridView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.WRAP_CONTENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

//        Log.v(LOG_TAG, "Getting picture for: " + mi.getmTitle());

        // Call picasso to load the picture
        if (mPosterWidth == 0) {
            GridView gv = (GridView)parent;
            mPosterWidth = gv.getColumnWidth();
            mPosterHeight = (int) (mPosterWidth*POSTER_RATIO);
//            Log.v(LOG_TAG, "Poster width=" + mPosterWidth + ", Poster height="+mPosterHeight);
        }

        Picasso.with(getContext())
                .load(TMDB_POSTER_URL + TMDB_POSTER_SIZE + mi.getmPosterPath())
                .resize(mPosterWidth, mPosterHeight)
                .into(imageView);

        return imageView;
    }
}
