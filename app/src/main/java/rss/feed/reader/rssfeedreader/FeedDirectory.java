package rss.feed.reader.rssfeedreader;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

public class FeedDirectory extends ListActivity implements TaskComplete {
    int noFeeds;
    int directoryID;
    String directoryName;
    DatabaseHelper db;
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
        db = DatabaseHelper.getInstance(this);
        // *** NOT DELETING ARTICLES
        db.deleteArticlesFromDirectory(directoryID);
        // *** CHANGE TO NON-DEPRECATED CONSTRUCTOR
        adapter = new SimpleCursorAdapter(this, R.layout.row_article_expanded, db.getAllArticles(), new String[] {"title", "description"}, new int[] {R.id.articleTitle, R.id.articleDescription});
        setListAdapter(adapter);

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

    public void callback() {
        noFeeds --;

        if (noFeeds == 0) {
            adapter = new SimpleCursorAdapter(this, R.layout.row_article_expanded, db.getAllArticles(), new String[] {"title", "description"}, new int[] {R.id.articleTitle, R.id.articleDescription});
        }
    }
}
