/* **************************************************
Author: Vlad Zat
Description: Display all the feeds from a directory

Created: 2016/11/19
Modified: 2016/11/20
************************************************** */

package rss.feed.reader.rssfeedreader;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class EditFeeds extends AppCompatActivity {
    private Intent intent;
    private ListView listView;
    private SimpleCursorAdapter adapter;
    private DatabaseHelper db;

    private int directoryID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_feeds);

        intent = getIntent();
        directoryID = intent.getIntExtra("directoryID", 1);
        setTitle(intent.getStringExtra("directoryName") + " Feeds");

        // Set up listView
        db = DatabaseHelper.getInstance(this);
        listView = (ListView) findViewById(R.id.list);
        adapter = new SimpleCursorAdapter(this, R.layout.row_feed, db.getFeedsFromDirectory(directoryID), new String[] {"feedName", "feedURL"}, new int[] {R.id.feedName, R.id.feedURL}, 0);
        listView.setAdapter(adapter);

        registerForContextMenu(listView);
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
        // Change the cursor to a new one if a feed has been edited
        if (requestCode == 1 || requestCode == 2) {
            if (resultCode == 1) {
                adapter.changeCursor(db.getFeedsFromDirectory(directoryID));
            }
        }

        // Display a message with the action performed
        if (resultCode != -1)
            if (requestCode == 1)
                Toast.makeText(this, "Feed Added", Toast.LENGTH_SHORT).show();
            else if (requestCode == 2)
                Toast.makeText(this, "Feed Edited", Toast.LENGTH_SHORT).show();
    }

    public void onCreateContextMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.list) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

            Cursor feed = (Cursor) adapter.getItem(info.position);

            contextMenu.setHeaderTitle(feed.getString(1));

            // Reference the following code is from https://www.mikeplate.com/2010/01/21/show-a-context-menu-for-long-clicks-in-an-android-listview/

            // Get the Context Menu Options create it
            String[] menuItems = getResources().getStringArray(R.array.contextMenu);
            for (int i = 0; i < menuItems.length; i++) {
                contextMenu.add(Menu.NONE, i, i, menuItems[i]);
            }

            // Reference Complete
        }
    }

    public boolean onContextItemSelected(MenuItem menuItem) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();

        // Get the Index of the Feed Selected
        int menuItemIndex = menuItem.getItemId();

        if (menuItemIndex == 0 || menuItemIndex == 1) {
            // Get the Feed Selected
            Cursor feed = (Cursor) adapter.getItem(info.position);

            // Get the name and type of the Directory
            Integer feedID = feed.getInt(0);
            String feedName = feed.getString(1);
            String feedURL = feed.getString(2);

            if (menuItemIndex == 0) {
                // Edit Feed
                Intent editFeed = new Intent(this, AddFeed.class);
                editFeed.putExtra("requestCode", 2);
                editFeed.putExtra("feedID", feedID);
                editFeed.putExtra("feedName", feedName);
                editFeed.putExtra("feedURL", feedURL);

                startActivityForResult(editFeed, 2);
            }
            if (menuItemIndex == 1) {
                // Delete the Directory
                db.deleteFeed(feedID);

                // Refresh the Feeds
                adapter.changeCursor(db.getFeedsFromDirectory(directoryID));

                Toast.makeText(this, "Feed " + feedName + " removed", Toast.LENGTH_SHORT).show();
            }

        }

        return true;
    }
}
