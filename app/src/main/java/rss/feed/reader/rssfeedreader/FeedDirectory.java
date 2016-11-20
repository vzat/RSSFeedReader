package rss.feed.reader.rssfeedreader;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
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
    String directoryName;
    DatabaseHelper db;
    ListView listView;
    SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_directory);

        // Get data about this directory
        directoryID = this.getIntent().getIntExtra("directoryID", 0);
        directoryName = this.getIntent().getStringExtra("directoryName");

        // Set the title of the Activity
        this.setTitle(directoryName);

        // Show Articles
        listView = (ListView) findViewById(R.id.list);
        db = DatabaseHelper.getInstance(this);
        adapter = new SimpleCursorAdapter(this, R.layout.row_article_expanded, db.getAllArticlesFromDirectory(directoryID), new String[] {"title", "description"}, new int[] {R.id.articleTitle, R.id.articleDescription}, 0);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

//        db.deleteArticlesFromDirectory(directoryID);

        // *** Refresh Directory
        // Get all the feeds from the directory
//        Cursor feeds = db.getFeedsFromDirectory(directoryID);
//
//        // Get the data from feeds
//        noFeeds = feeds.getCount();
//        try {
//            while (feeds.moveToNext()) {
//                new DataFromFeed(this, this).execute(feeds.getString(2));
//                System.out.println("Parsing Feed");
//            }
//        } finally {
//            feeds.close();
//        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.feed_directory, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.refreshDirectory) {
            refresh();
            return true;
        }
        if (menuItem.getItemId() == R.id.editFeed) {
//            Intent addFeed = new Intent(this, AddFeed.class);
//            addFeed.putExtra("requestCode", 1);
//            addFeed.putExtra("directoryID", directoryID);
//            startActivityForResult(addFeed, 1);

            Intent editFeed = new Intent(this, EditFeeds.class);
            editFeed.putExtra("directoryID", directoryID);
            startActivity(editFeed);
            return true;
        }
        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 || requestCode == 2) {
            if (resultCode == 1) {
                refresh();
            }
        }

        if (resultCode != -1)
            if (requestCode == 1)
                showToast("Feed Added");
            else
                showToast("Feed Edited");
    }

    public void onItemClick(AdapterView l, View v, int position, long id) {
        Cursor cursor = (Cursor) adapter.getItem(position);

        Intent goToArticle = new Intent(this, ArticleActivity.class);
        goToArticle.putExtra("articleID", cursor.getInt(0));
        goToArticle.putExtra("articleTitle", cursor.getString(1));
        goToArticle.putExtra("articleDescription", cursor.getString(2));
        goToArticle.putExtra("articleLink", cursor.getString(3));
        goToArticle.putExtra("articleDate", cursor.getString(4));

        startActivity(goToArticle);
    }

    public void refresh() {
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
    }

    public void callback() {
        noFeeds --;

        if (noFeeds == 0) {
            adapter.changeCursor(db.getAllArticlesFromDirectory(directoryID));
        }
    }

    public void showToast(String text) {
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();
    }
}
