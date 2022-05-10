package com.example.android.bookListing;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private static final String BOOKS_API_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=";

    private static String authorsList;

    private QueryUtils() {

    }

    public static String getUrl(String searchText) {
        return BOOKS_API_REQUEST_URL + searchText;
    }


    public static List<Book> fetchBooksData(String requestUrl) {

        URL url = createUrl(requestUrl);


        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        List<Book> books = extractBooks(jsonResponse);

        Log.e(LOG_TAG, "Fetching the data from List<Book> object");


        return books;
    }


    public static ArrayList<Book> extractBooks(String booksJSON) {


        if (TextUtils.isEmpty(booksJSON)) {
            return null;
        }

        ArrayList<Book> books = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(booksJSON);

            JSONArray bookArray = baseJsonResponse.getJSONArray("items");

            for (int i = 0; i < bookArray.length(); i++) {

                JSONObject currentBook = bookArray.getJSONObject(i);

                JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");

                String imageUrl;
                Bitmap thumbnailBitmap = null;

                if (volumeInfo.has("imageLinks")) {
                    imageUrl = (volumeInfo.getJSONObject("imageLinks")).getString("smallThumbnail");
                    URL url = new URL(imageUrl);
                    thumbnailBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                }

                String bookTitle = volumeInfo.getString("title");

                if (volumeInfo.has("authors")) {
                    JSONArray bookAuthorsList = volumeInfo.getJSONArray("authors");

                    authorsList = "";

                    for (int a = 0; a < bookAuthorsList.length(); a++) {
                        if (a == bookAuthorsList.length() - 1) {
                            authorsList += (String) bookAuthorsList.get(a);
                        } else {
                            authorsList += bookAuthorsList.get(a) + ", ";
                        }

                    }
                }

                String bookLink = volumeInfo.getString("infoLink");

                Book book = new Book(thumbnailBitmap, bookTitle, authorsList, bookLink);

                books.add(book);

            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the book JSON results", e);
        } catch (MalformedURLException e) {
            Log.e("QueryUtils", "There is Malformed URL", e);
        } catch (IOException e) {
            Log.e("QueryUtils", "There is I/O Exception", e);
        }

        return books;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {

        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error Response code: " +
                        urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG,
                    "Problem retrieving the book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {

                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
}
