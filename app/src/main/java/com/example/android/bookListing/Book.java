package com.example.android.bookListing;

import android.graphics.Bitmap;

public class Book {
    private Bitmap ImageUrlBitmap;
    private String BookTitle;
    private String BookAuthor;
    private String BookUrl;

    public Book (Bitmap imageUrlBitmap, String bookTitle, String bookAuthor, String bookUrl){
        ImageUrlBitmap = imageUrlBitmap;
        BookTitle = bookTitle;
        BookAuthor = bookAuthor;
        BookUrl = bookUrl;
    }

    public Bitmap getImageUrlBitmap() {
        return ImageUrlBitmap;
    }

    public String getBookTitle() {
        return BookTitle;
    }

    public String getBookAuthor() {
        return BookAuthor;
    }

    public String getBookUrl() {
        return BookUrl;
    }
}
