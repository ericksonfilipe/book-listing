package com.apps.ericksonfilipe.booklisting;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


public class BookListAdapter extends ArrayAdapter<Book> {

    private Context context;

    public BookListAdapter(Context context, List<Book> books) {
        super(context, 0, books);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.book_list_item, parent, false);
        }
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView moreInfo = (TextView) convertView.findViewById(R.id.more_info);

        Book book = getItem(position);

        title.setText(book.getTitle());
        moreInfo.setText(getMoreInfo(book));

        return convertView;
    }

    private String getMoreInfo(Book book) {
        String moreInformation = "";
        if (book.getAuthors() != null) {
            moreInformation = TextUtils.join(",", book.getAuthors());
        }
        if (book.getPublisher() != null) {
            if (!moreInformation.isEmpty()) {
                moreInformation += " - ";
            }
            moreInformation += book.getPublisher();
        }
        return moreInformation;
    }
}
