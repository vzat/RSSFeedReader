package rss.feed.reader.rssfeedreader;

import org.apache.commons.io.IOUtils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorTreeAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorTreeAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TaskComplete {

    int noFeeds;
    ExpandableListView expandableListView;
    SimpleCursorTreeAdapter treeAdapter;
    Cursor directoryTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get Directory Types, Saved and Feed Directories
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        directoryTypes = db.getDirectoryTypes();

        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        treeAdapter = new SimpleCursorTreeAdapter(  this, directoryTypes,
                                                    R.layout.directory_group,
                                                    new String[] {"directoryType"}, new int[] {R.id.directoryGroup},
                                                    R.layout.directory_item,
                                                    new String[] {"directoryName"}, new int[] {R.id.directoryName}) {
            @Override
            protected Cursor getChildrenCursor(Cursor groupCursor) {
                DatabaseHelper db = DatabaseHelper.getInstance();
                if ("Saved".equals(groupCursor.getString(0)))
                    return db.getAllDirectories("Saved");
                else
                    return db.getAllDirectories("Feed");
            }
        };
        expandableListView.setAdapter(treeAdapter);


//        treeAdapter = new CursorTreeAdapter(directoryTypes, this) {
//            @Override
//            protected Cursor getChildrenCursor(Cursor groupCursor) {
//                groupCursor.moveToNext();
//                if ("Saved".equals(groupCursor.getString(0)))
//                    return savedDirectories;
//                else
//                    return feedDirectories;
//            }
//
//            @Override
//            protected View newGroupView(Context context, Cursor cursor, boolean isExpanded, ViewGroup parent) {
//                return null;
//            }
//
//            @Override
//            protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {
//
//            }
//
//            @Override
//            protected View newChildView(Context context, Cursor cursor, boolean isLastChild, ViewGroup parent) {
//                return null;
//            }
//
//            @Override
//            protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
//
//            }
//        }


//        // Set the list views
//        savedListView = (ListView) findViewById(R.id.savedListView);
//        feedListView = (ListView) findViewById(R.id.feedListView);
//
//        // Open database and get the directories
//        DatabaseHelper db = DatabaseHelper.getInstance(this);
//        savedDirectories = db.getAllDirectories("Saved");
//        feedDirectories = db.getAllDirectories("Feed");

//        // Set the columns to get the data from and ids to map to
//        String[] columns = {"directoryName"};
//        int[] ids = {R.id.directoryName};
//
//        // Set the Saved Directories
//        if (savedDirectories != null && savedDirectories.getCount() > 0) {
//            savedAdapter = new SimpleCursorAdapter(this, R.layout.directory_row, savedDirectories, columns, ids, 1);
//            savedListView.setAdapter(savedAdapter);
//
//        }
//
//        // Set the Feed Directories
//        if (feedDirectories != null && savedDirectories.getCount() > 0) {
//            feedAdapter = new SimpleCursorAdapter(this, R.layout.directory_row, feedDirectories, columns, ids, 1);
//            feedListView.setAdapter(feedAdapter);
//        }




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
