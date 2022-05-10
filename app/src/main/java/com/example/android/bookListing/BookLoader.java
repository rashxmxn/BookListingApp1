package com.example.android.bookListing;

import android.content.AsyncTaskLoader;

import java.util.List;

public class BookLoader extends AsyncTaskLoader<List<Book>> {

    private static final String LOG_TAG = BookLoader.class.getName();

    private final String Url;

    public BookLoader(MainActivity context, String url) {
        super(context);
        Url = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Book> loadInBackground() {
        if (Url == null) {
            return null;
        }

        return QueryUtils.fetchBooksData(Url);
    }


}
