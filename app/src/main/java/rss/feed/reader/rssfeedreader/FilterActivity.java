/* **************************************************
Author: Vlad Zat
Description: Display the filters from the current directory

Created: 2016/11/21
Modified: 2016/11/25
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

public class FilterActivity extends AppCompatActivity {
    ListView listView;
    SimpleCursorAdapter adapter;
    DatabaseHelper db;

    int directoryID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        // Get the directoryID
        setResult(-1);
        directoryID = this.getIntent().getIntExtra("directoryID", 0);
        setTitle(this.getIntent().getStringExtra("directoryName") + " Filters");

        // Set up listView
        listView = (ListView) findViewById(R.id.list);
        db = DatabaseHelper.getInstance(this);
        adapter = new SimpleCursorAdapter(this, R.layout.row_filter, db.getFilters(directoryID), new String[] {"filterName"}, new int[] {R.id.filter}, 0);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.filter, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.addFilter) {
            Intent addFilter = new Intent(this, AddFilter.class);
            addFilter.putExtra("directoryID", directoryID);
            startActivityForResult(addFilter, 1);
            return true;
        }
        return false;
    }

    public void onActivityResult(int requestCode, int responseCode, Intent data) {
        if (requestCode == 1 && responseCode != -1) {
            adapter.swapCursor(db.getFilters(directoryID));
            Toast.makeText(this, "Filter Added", Toast.LENGTH_SHORT).show();
            setResult(1);
        }
        if (requestCode == 2 && responseCode != -1) {
            adapter.swapCursor(db.getFilters(directoryID));
            Toast.makeText(this, "Filter Edited", Toast.LENGTH_SHORT).show();
            setResult(1);
        }
    }

    public void onCreateContextMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.list) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

            Cursor filter = (Cursor) adapter.getItem(info.position);

            contextMenu.setHeaderTitle(filter.getString(1));

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

        // Get the Index of the Filter Selected
        int menuItemIndex = menuItem.getItemId();

        if (menuItemIndex == 0 || menuItemIndex == 1) {
            // Get the Filter Selected
            Cursor filter = (Cursor) adapter.getItem(info.position);

            // Get the name and type of the Directory
            int filterID = filter.getInt(0);
            String filterName = filter.getString(1);

            if (menuItemIndex == 0) {
                // Edit Feed
                Intent editFilter = new Intent(this, AddFilter.class);
                editFilter.putExtra("requestCode", 2);
                editFilter.putExtra("filterID", filterID);
                editFilter.putExtra("filterName", filterName);

                startActivityForResult(editFilter, 2);
            }
            if (menuItemIndex == 1) {
                // Delete the Directory
                db.deleteFilter(filterID);

                // Refresh the Feeds
                adapter.changeCursor(db.getFilters(directoryID));

                Toast.makeText(this, "Filter " + filterName + " removed", Toast.LENGTH_SHORT).show();
                setResult(1);
            }

        }

        return true;
    }
}
