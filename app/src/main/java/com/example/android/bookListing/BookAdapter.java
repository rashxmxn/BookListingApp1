package com.example.android.bookListing;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(Activity context, List<Book> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        Book currentBook = getItem(position);

        ImageView bookImageView = (ImageView) listItemView.findViewById(R.id.imageItem);
        bookImageView.setImageBitmap(formatImageFromBitmap(currentBook.getImageUrlBitmap()));

        TextView bookTitle = (TextView) listItemView.findViewById(R.id.bookTitle);
        bookTitle.setText(currentBook.getBookTitle());

        TextView bookAuthor = (TextView) listItemView.findViewById(R.id.bookAuthor);
        bookAuthor.setText(currentBook.getBookAuthor());

        return listItemView;
    }

    private Bitmap formatImageFromBitmap(Bitmap bookThumbnail) {
        Bitmap bitmapResult;
        if (bookThumbnail == null) {
            bitmapResult = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.no_image_available);
        } else {
            bitmapResult = bookThumbnail;
        }
        return bitmapResult;
    }

}

