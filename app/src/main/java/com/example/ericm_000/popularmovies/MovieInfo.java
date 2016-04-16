package com.example.ericm_000.popularmovies;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;

/**
 * Created by ericm_000 on 03/04/2016.
 */
public class MovieInfo implements Serializable {
    private static String LOG_TAG = MovieInfo.class.getSimpleName();

    private String mTitle;
    private String mDescription;
    private String mPosterPath;
    private Calendar mReleaseDate;
    private double mPopularity;
    private static SimpleDateFormat sDateFormat;
    private static NumberFormat sNumberFormat;

    MovieInfo() {
        if (MovieInfo.sDateFormat == null) {
            MovieInfo.sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        }
        if (MovieInfo.sNumberFormat == null) {
            MovieInfo.sNumberFormat = new DecimalFormat("#0.00");
        }
    }

    @Override
    public String toString() {
        return mTitle + " - " + getReleaseDateAsString() + " - " + getPopularityAsString();
    }

    //Reverse sort on popularity by default
    public static class PopularityComparator implements Comparator<MovieInfo> {

        @Override
        public int compare(MovieInfo lhs, MovieInfo rhs) {
            return Double.compare(rhs.getmPopularity(), lhs.getmPopularity());
        }
    }

    public static class TitleComparator implements Comparator<MovieInfo> {

        @Override
        public int compare(MovieInfo lhs, MovieInfo rhs) {
            return lhs.getmTitle().compareTo(rhs.getmTitle());
        }
    }

    //Reverse sort on release date by default
    public static class ReleaseDateComparator implements  Comparator<MovieInfo> {
        @Override
        public int compare(MovieInfo lhs, MovieInfo rhs) {
            int comp = rhs.getmReleaseDate().compareTo(lhs.getmReleaseDate());
            if (comp == 0) {
                // Sort by alphabetical order if the same release date
                return lhs.getmTitle().compareTo(rhs.getmTitle());
            } else {
                return comp;
            }
        }
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getPopularityAsString() {
        return sNumberFormat.format(mPopularity);
    }

    public double getmPopularity() {
        return mPopularity;
    }

    public void setmPopularity(double mPopularity) {
        this.mPopularity = mPopularity;
    }

    public String getReleaseDateAsString() {
        return sDateFormat.format(mReleaseDate.getTime());
    }

    public Calendar getmReleaseDate() {
        return mReleaseDate;
    }

    public void setmReleaseDate(String mReleaseDate) {
        //We assume release date is of the form yyyy-mm-dd
        int year = Integer.parseInt(mReleaseDate.substring(0,4));
        int month = Integer.parseInt(mReleaseDate.substring(5,7)) - 1; //Month is 0 based
        int day = Integer.parseInt(mReleaseDate.substring(8,10));
        this.mReleaseDate = new GregorianCalendar(year, month, day);
    }

    public String getmPosterPath() {
        return mPosterPath;
    }

    public void setmPosterPath(String mPosterPath) {
        this.mPosterPath = mPosterPath;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }
}
