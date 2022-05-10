package com.example.android.bookListing;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements LoaderCallbacks<List<Book>> {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final int BOOK_LOADER_ID = 1;
    private static final String LIST_STATE = "listState";
    private BookAdapter adapter;
    private SearchView searchTextView;
    private TextView mEmptyStateTextView;
    private View loadingIndicator;
    private ListView booksListView;
    private Parcelable mListState = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        booksListView = (ListView) findViewById(R.id.list);

        loadingIndicator = findViewById(R.id.loading_indicator);

        searchTextView = (SearchView) findViewById(R.id.search_text);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        booksListView.setEmptyView(mEmptyStateTextView);

        searchTextView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                mEmptyStateTextView.setText(null);
                booksListView.setVisibility(View.GONE);
                loadingIndicator.setVisibility(View.VISIBLE);

                getLoaderManager().restartLoader(BOOK_LOADER_ID, null, MainActivity.this);

                searchTextView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        Button searchButton = (Button) findViewById(R.id.search_button);

        ArrayList<Book> books = QueryUtils.extractBooks(QueryUtils.getUrl(searchTextView.getQuery().toString()));

        adapter = new BookAdapter(MainActivity.this, books);
        booksListView.setAdapter(adapter);

        booksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book currentBook = adapter.getItem(position);

                Uri bookUri = Uri.parse(currentBook.getBookUrl());

                Intent playStoreIntent = new Intent(Intent.ACTION_VIEW, bookUri);
                startActivity(playStoreIntent);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mEmptyStateTextView.setText(null);
                booksListView.setVisibility(View.GONE);
                loadingIndicator.setVisibility(View.VISIBLE);

                getLoaderManager().restartLoader(BOOK_LOADER_ID, null, MainActivity.this);

                searchTextView.clearFocus();
            }
        });

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();

            loaderManager.initLoader(BOOK_LOADER_ID, null, this);
        } else {
            loadingIndicator.setVisibility(View.GONE);

            mEmptyStateTextView.setText(R.string.no_internet);
        }

    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {
        Uri baseUri = Uri.parse(QueryUtils.getUrl(searchTextView.getQuery().toString()));
        Uri.Builder uriBuilder = baseUri.buildUpon();

        return new BookLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> book) {

        loadingIndicator.setVisibility(View.GONE);
        booksListView.setVisibility(View.VISIBLE);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        // Check if there is a network connection, which affects the empty state text view output
        if (networkInfo != null && networkInfo.isConnected()) {
            // Set empty state text to display "There are no books to display."
            mEmptyStateTextView.setText(R.string.no_books_data);
        } else {
            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet);
        }

        // Clear the adapter of previous book data (in order to avoid deletion on empty adapter)
        if (adapter != null) {
            adapter.clear();
        }
        // If there is a valid list of {@link Book}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (book != null && !book.isEmpty()) {
            adapter.addAll(book);
        }

        // Restore the ListView scroll position after the loader finished on loading
        if (mListState != null)
            booksListView.onRestoreInstanceState(mListState);

    }

    // Reset the loader (such as the occasion when rotating the screen)
    // by clearing the contents of adapter
    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        adapter.clear();
    }

    // Restore the ListView scroll position by getting a constant value
    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        mListState = state.getParcelable(LIST_STATE);
    }

    // Disable the SearchView when resuming the Activity as well as restoring the
    // ListView scroll position by calling onRestoreInstanceState method
    @Override
    protected void onResume() {
        super.onResume();
        searchTextView.clearFocus();
        if (mListState != null)
            booksListView.onRestoreInstanceState(mListState);
    }

    // Save the listView scroll position
    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        mListState = booksListView.onSaveInstanceState();
        state.putParcelable(LIST_STATE, mListState);
    }
}
