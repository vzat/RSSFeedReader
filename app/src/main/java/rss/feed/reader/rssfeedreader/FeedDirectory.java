/* **************************************************
Author: Vlad Zat
Description: Display all the articles in either feed directories or saved directories

Created: 2016/11/19
Modified: 2016/11/20
************************************************** */

package rss.feed.reader.rssfeedreader;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class FeedDirectory extends AppCompatActivity implements TaskComplete, ListView.OnItemClickListener {
    int noFeeds;
    int directoryID;
    String directoryName, directoryType;

    DatabaseHelper db;
    ListView listView;
    SimpleCursorAdapter adapter;
    SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_directory);

        // Get data about this directory
        directoryID = this.getIntent().getIntExtra("directoryID", 0);
        directoryName = this.getIntent().getStringExtra("directoryName");
        directoryType = this.getIntent().getStringExtra("directoryType");

        // Set the title of the Activity
        this.setTitle(directoryName);

        // Show Articles
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                refresh();
            }
});
        listView = (ListView) findViewById(R.id.list);
        db = DatabaseHelper.getInstance(this);
        adapter = new SimpleCursorAdapter(this, R.layout.row_article_expanded, db.getAllArticlesFromDirectoryFiltered(directoryID, directoryType), new String[] {"title", "description"}, new int[] {R.id.articleTitle, R.id.articleDescription}, 0);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        // Disable the swipe refresh layout if it's a saved directory
        if ("Saved".equals(directoryType)) {
            swipeRefresh.setEnabled(false);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if ("Feed".equals(directoryType)) {
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.feed_directory, menu);
            return true;
        } else return false;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if ("Feed".equals(directoryType)) {
            if (menuItem.getItemId() == R.id.refreshDirectory) {
                swipeRefresh.post(new Runnable() {
                    public void run() {
                        swipeRefresh.setRefreshing(true);
                    }
                });
                refresh();
                return true;
            }
            if (menuItem.getItemId() == R.id.editFeed) {
                Intent editFeed = new Intent(this, EditFeeds.class);
                editFeed.putExtra("directoryID", directoryID);
                editFeed.putExtra("directoryName", directoryName);
                startActivity(editFeed);
                return true;
            }
            if (menuItem.getItemId() == R.id.editFilters) {
                Intent editFilters = new Intent(this, FilterActivity.class);
                editFilters.putExtra("directoryID", directoryID);
                editFilters.putExtra("directoryName", directoryName);
                startActivityForResult(editFilters, 2);
                return true;
            }
        }
        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Refresh the articles shown if it's a saved directory
        if ((requestCode == 1 && "Saved".equals(directoryType)) || (requestCode == 2)) {
            adapter.swapCursor(db.getAllArticlesFromDirectoryFiltered(directoryID, directoryType));
        }
        // Show toast if filters have been changed
        if (requestCode == 2 && resultCode == 1) {
            showToast("Filters Applied");
        }
    }

    public void onItemClick(AdapterView l, View v, int position, long id) {
        // Go to the article pressed
        Cursor cursor = (Cursor) adapter.getItem(position);

        Intent goToArticle = new Intent(this, ArticleActivity.class);
        goToArticle.putExtra("articleID", cursor.getInt(0));
        goToArticle.putExtra("articleTitle", cursor.getString(1));
        goToArticle.putExtra("articleDescription", cursor.getString(2));
        goToArticle.putExtra("articleLink", cursor.getString(3));
        goToArticle.putExtra("articleDate", cursor.getString(4));

        startActivityForResult(goToArticle, 1);
    }

    public void refresh() {
        // Animate the listView
        listView.animate().translationY(listView.getHeight());

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // Delete all the articles from the directory
            db.deleteArticlesFromDirectory(directoryID);

            // Get all the feeds from the directory
            Cursor feeds = db.getFeedsFromDirectory(directoryID);

            // Get the data from feeds
            noFeeds = feeds.getCount();
            try {
                while (feeds.moveToNext()) {
                    new DataFromFeed(this, this).execute(feeds.getString(2), directoryID);
                }
            } finally {
                feeds.close();
            }
        } else {
            showToast("No Network Connection Available");
            swipeRefresh.post(new Runnable() {
                public void run() {
                    swipeRefresh.setRefreshing(false);
                }
            });
        }
    }

    public void callback() {
        // Decrease the number of feeds waiting
        noFeeds --;

        // Get the articles from the database if all the feeds are finished
        if (noFeeds == 0) {
            adapter.changeCursor(db.getAllArticlesFromDirectoryFiltered(directoryID, directoryType));

            // Show the listView and hide the progress swipeRefresh
            swipeRefresh.post(new Runnable() {
                public void run() {
                    swipeRefresh.setRefreshing(false);
                }
            });

            listView.animate().translationY(0.0f);
        }
    }

    public void callback(String url) {}

    public void showToast(String text) {
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();
    }
}
