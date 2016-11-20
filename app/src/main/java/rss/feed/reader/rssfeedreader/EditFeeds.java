package rss.feed.reader.rssfeedreader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class EditFeeds extends AppCompatActivity {
    Intent intent;
    ListView listView;
    SimpleCursorAdapter adapter;
    DatabaseHelper db;

    int directoryID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_feeds);

        setTitle("Edit Feeds");

        intent = getIntent();
        directoryID = intent.getIntExtra("directoryID", 1);

        // Set up listView
        db = DatabaseHelper.getInstance(this);
        listView = (ListView) findViewById(R.id.list);
        adapter = new SimpleCursorAdapter(this, R.layout.row_feed, db.getFeedsFromDirectory(directoryID), new String[] {"feedName", "feedURL"}, new int[] {R.id.feedName, R.id.feedURL}, 0);
        listView.setAdapter(adapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.feed_edit, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.addFeed) {
            Intent addFeed = new Intent(this, AddFeed.class);
            addFeed.putExtra("requestCode", 1);
            addFeed.putExtra("directoryID", directoryID);
            startActivityForResult(addFeed, 1);
        }
        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 || requestCode == 2) {
            if (resultCode == 1) {
                adapter.changeCursor(db.getFeedsFromDirectory(directoryID));
            }
        }

        if (resultCode != -1)
            if (requestCode == 1)
                Toast.makeText(this, "Feed Added", Toast.LENGTH_SHORT).show();
            else if (requestCode == 2)
                Toast.makeText(this, "Feed Edited", Toast.LENGTH_SHORT).show();
    }
}
