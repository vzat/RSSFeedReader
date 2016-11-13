package rss.feed.reader.rssfeedreader;

import org.apache.commons.io.IOUtils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TaskComplete {

    int noFeeds;
    ListView savedListView, feedListView;
    SimpleCursorAdapter savedAdapter, feedAdapter;
    Cursor savedDirectories, feedDirectories;
    // Change to ExpandableListView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the list views
        savedListView = (ListView) findViewById(R.id.savedListView);
        feedListView = (ListView) findViewById(R.id.feedListView);

        // Open database and get the directories
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        savedDirectories = db.getAllDirectories("Saved");
        feedDirectories = db.getAllDirectories("Feed");

        // Set the columns to get the data from and ids to map to
        String[] columns = {"directoryName"};
        int[] ids = {R.id.directoryName};

        // Set the Saved Directories
        if (savedDirectories != null && savedDirectories.getCount() > 0) {
            savedAdapter = new SimpleCursorAdapter(this, R.layout.directory_row, savedDirectories, columns, ids, 1);
            savedListView.setAdapter(savedAdapter);

        }

        // Set the Feed Directories
        if (feedDirectories != null && savedDirectories.getCount() > 0) {
            feedAdapter = new SimpleCursorAdapter(this, R.layout.directory_row, feedDirectories, columns, ids, 1);
            feedListView.setAdapter(feedAdapter);
        }

//        DatabaseHelper db = DatabaseHelper.getInstance(this);
//        db.deleteArticlesFromDirectory(1);
//        new DataFromFeed(this, this).execute("https://www.reddit.com/.rss");
//        new DataFromFeed(this, this).execute("http://www.rte.ie/news/rss/news-headlines.xml");
//
//        noFeeds = 2;
    }

    public void callback() {
        noFeeds --;

        if (noFeeds == 0) {
            DatabaseHelper db = DatabaseHelper.getInstance(this);
            Cursor cursorArticles = db.getAllArticles();

            try {
                while (cursorArticles.moveToNext()) {
                    Log.d("Cursor: ",   "Title: " + cursorArticles.getString(1) +
                                        ", Description: " + cursorArticles.getString(2) +
                                        ", Link: " + cursorArticles.getString(3) +
                                        ", Date: " + cursorArticles.getString(4));
                }
            } finally {
                cursorArticles.close();
            }
            db.closeDBs();
        }
    }
}
