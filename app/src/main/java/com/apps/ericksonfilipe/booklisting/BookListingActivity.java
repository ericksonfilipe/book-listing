package com.apps.ericksonfilipe.booklisting;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BookListingActivity extends AppCompatActivity {

    private EditText searchText;
    private Button searchButton;
    private TextView noDataTextView;
    private ProgressBar progressBar;
    private ListView booksListView;

    private List<Book> bookList;
    private int startIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_listing);

        searchText = (EditText) findViewById(R.id.search_text);
        searchButton = (Button) findViewById(R.id.search_button);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        noDataTextView = (TextView) findViewById(R.id.no_data_text_view);
        booksListView = (ListView) findViewById(R.id.books_list_view);

        loadListeners();
    }

    private void loadListeners() {
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    startIndex = 0;
                    bookList = new ArrayList<>();
                    performSearch();
                    return true;
                }
                return false;
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startIndex = 0;
                bookList = new ArrayList<>();
                performSearch();
            }
        });
        booksListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            private int mFirstVisibleItem, mVisibleItemCount, mTotalItemCount;
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                int lastItem = mFirstVisibleItem + mVisibleItemCount;
                if (lastItem == mTotalItemCount && scrollState == SCROLL_STATE_IDLE) {
                    startIndex = mTotalItemCount - 1;
                    performSearch();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                mFirstVisibleItem = firstVisibleItem;
                mVisibleItemCount = visibleItemCount;
                mTotalItemCount = totalItemCount;
            }
        });
    }

    private void performSearch() {
        noDataTextView.setVisibility(View.GONE);
        Utils.hideKeyboard(this);
        if (Utils.hasInternetConnection(this)) {
            progressBar.setVisibility(View.VISIBLE);
            new FetchBooksTask().execute(searchText.getText().toString(), String.valueOf(startIndex));
        } else {
            Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_LONG).show();
        }
    }

    private void handleJsonResponse(String jsonResponse) {
        int previousSize = bookList.size();
        try {
            final JSONArray books = new JSONObject(jsonResponse).getJSONArray("items");
            for (int i = 0; i < books.length(); i++) {
                final JSONObject volumeInfo = books.getJSONObject(i).getJSONObject("volumeInfo");
                Book book = new Book();
                book.setTitle(volumeInfo.getString("title"));
                if (volumeInfo.has("authors")) {
                    for (int j = 0; j < volumeInfo.getJSONArray("authors").length(); j++) {
                        book.getAuthors().add(volumeInfo.getJSONArray("authors").getString(j));
                    }
                }
                if (volumeInfo.has("publisher")) {
                    book.setPublisher(volumeInfo.getString("publisher"));
                }
                bookList.add(book);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (bookList.size() == 0) {
            noDataTextView.setVisibility(View.VISIBLE);
        }
        booksListView.setAdapter(new BookListAdapter(this, bookList));
        if (previousSize != 0 && bookList.size() > previousSize) {
            booksListView.setSelection(previousSize - 1);
        }
    }

    private class FetchBooksTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... arguments) {
            if (arguments.length > 0) {
                String search = arguments[0];
                if (!search.isEmpty()) {
                    return BooksAPI.getBooks(arguments[0], arguments[1]);
                }
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            if (result == null) {
                Toast.makeText(BookListingActivity.this, R.string.server_error, Toast.LENGTH_LONG).show();
            } else {
                handleJsonResponse(result);
            }
        }
    }
}
