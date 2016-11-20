package rss.feed.reader.rssfeedreader;

import org.apache.commons.io.IOUtils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorTreeAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnCreateContextMenuListener, ExpandableListView.OnChildClickListener {

    int noFeeds;
    ExpandableListView expandableListView;
    SimpleCursorTreeAdapter treeAdapter;
    Cursor directoryTypes;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get Directory Types, Saved and Feed Directories
        db = DatabaseHelper.getInstance(this);

        directoryTypes = db.getDirectoryTypes();

        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        treeAdapter = new SimpleCursorTreeAdapter(  this, directoryTypes,
                                                    R.layout.directory_group,
                                                    new String[] {"directoryType"}, new int[] {R.id.directoryGroup},
                                                    R.layout.directory_item,
                                                    new String[] {"directoryName"}, new int[] {R.id.directoryName}) {
            @Override
            protected Cursor getChildrenCursor(Cursor groupCursor) {
                if ("Saved Directories".equals(groupCursor.getString(1))) {
                    return db.getAllDirectories("Saved");
                } else {
                    return db.getAllDirectories("Feed");
                }
            }

            @Override
            public boolean isChildSelectable(int groupPosition, int childPosition) {
                return true;
            }
        };
        expandableListView.setAdapter(treeAdapter);
        expandableListView.setOnChildClickListener(this);
        registerForContextMenu(expandableListView);

        // Temp add Feeds
//        Cursor feedDir = db.getAllDirectories("Feed");
//        feedDir.moveToNext();
//        db.insertFeed("Reddit", "https://www.reddit.com/.rss", feedDir.getInt(0));
//        db.insertFeed("RTE", "http://www.rte.ie/news/rss/news-headlines.xml", feedDir.getInt(0));

//        expandableListView.setOnClickListener(new ExpandableListView.OnChildClickListener() {
//            public boolean onChildClick(ExpandableListView l, View v, int groupPos, int childPos, long id) {
//                    showToast("Hello");
//                  return true;
//            }
//        });



//        DatabaseHelper db = DatabaseHelper.getInstance(this);
//        db.deleteArticlesFromDirectory(1);
//        new DataFromFeed(this, this).execute("https://www.reddit.com/.rss");
//        new DataFromFeed(this, this).execute("http://www.rte.ie/news/rss/news-headlines.xml");
//
//        noFeeds = 2;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.directory, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.addDirectory) {
            Intent addDirectory = new Intent(this, AddDirectory.class);
            addDirectory.putExtra("requestCode", 1);
            startActivityForResult(addDirectory, 1);
            return true;
        }
        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 || requestCode == 2) {
            if (resultCode == 0) {
                treeAdapter.setChildrenCursor(0, db.getAllDirectories("Saved"));
            } else if (resultCode == 1) {
                treeAdapter.setChildrenCursor(1, db.getAllDirectories("Feed"));
            }

            if (resultCode != -1)
                if (requestCode == 1)
                    showToast("Directory Added");
                else
                    showToast("Directory Edited");
        }
    }

    public boolean onChildClick(ExpandableListView l, View v, int groupPosition, int childPosition, long id) {
        Cursor directorySelected = treeAdapter.getChild(groupPosition, childPosition);

        if ("Feed".equals(directorySelected.getString(2))) {
            Intent goToDirectory = new Intent(this, FeedDirectory.class);
            goToDirectory.putExtra("directoryID", directorySelected.getInt(0));
            goToDirectory.putExtra("directoryName", directorySelected.getString(1));
            startActivity(goToDirectory);
        } else if ("Saved".equals(directorySelected.getString(2))) {

        }



        return true;
    }

    public void onCreateContextMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.expandableListView) {
            ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;

            // Get the Position of the Directory Selected
            int groupPosition = ExpandableListView.getPackedPositionGroup(info.packedPosition);
            int childPosition = ExpandableListView.getPackedPositionChild(info.packedPosition);

            if (groupPosition != -1 && childPosition != -1) {
                // Get the Directory Selected
                Cursor directorySelected = treeAdapter.getChild(groupPosition, childPosition);

                // Set the Context Menu Header to the Directory Name
                contextMenu.setHeaderTitle(directorySelected.getString(1));


                // Reference the following code is from https://www.mikeplate.com/2010/01/21/show-a-context-menu-for-long-clicks-in-an-android-listview/

                // Get the Context Menu Options create it
                String[] menuItems = getResources().getStringArray(R.array.contextMenu);
                for (int i = 0; i < menuItems.length; i++) {
                    contextMenu.add(Menu.NONE, i, i, menuItems[i]);
                }

                // Reference Complete
            }
        }
    }

    public boolean onContextItemSelected(MenuItem menuItem) {
        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuItem.getMenuInfo();

        // Get the Index of the Directory Selected
        int menuItemIndex = menuItem.getItemId();

        if (menuItemIndex == 0 || menuItemIndex == 1) {
            // Get the Position of the Directory Selected
            int groupPosition = ExpandableListView.getPackedPositionGroup(info.packedPosition);
            int childPosition = ExpandableListView.getPackedPositionChild(info.packedPosition);

            // Get the Directory Selected
            Cursor directorySelected = treeAdapter.getChild(groupPosition, childPosition);

            // Get the name and type of the Directory
            int directoryID = directorySelected.getInt(0);
            String directoryName = directorySelected.getString(1);
            String directoryType = directorySelected.getString(2);

            if (menuItemIndex == 0) {
                // Edit Directory
                Intent editDirectory = new Intent(this, AddDirectory.class);
                editDirectory.putExtra("requestCode", 2);
                editDirectory.putExtra("directoryID", directoryID);
                editDirectory.putExtra("directoryName", directoryName);
                editDirectory.putExtra("directoryType", directoryType);
                startActivityForResult(editDirectory, 2);
            }
            if (menuItemIndex == 1) {
                // Delete Directory

                // Delete the Directory
                db.deleteDirectory(directorySelected.getInt(0));

                // Refresh the Directories
                if ("Saved".equals(directorySelected.getString(2))) {
                    treeAdapter.setChildrenCursor(0, db.getAllDirectories("Saved"));
                } else if ("Feed".equals(directorySelected.getString(2))) {
                    treeAdapter.setChildrenCursor(1, db.getAllDirectories("Feed"));
                }

                showToast("Directory " + directoryName + " removed");
            }

        }

        return true;
    }

    public void showToast(String text) {
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();
    }

//    public void callback() {
//        noFeeds --;
//
//        if (noFeeds == 0) {
//            DatabaseHelper db = DatabaseHelper.getInstance(this);
//            Cursor cursorArticles = db.getAllArticles();
//
//            try {
//                while (cursorArticles.moveToNext()) {
//                    Log.d("Cursor: ",   "Title: " + cursorArticles.getString(1) +
//                                        ", Description: " + cursorArticles.getString(2) +
//                                        ", Link: " + cursorArticles.getString(3) +
//                                        ", Date: " + cursorArticles.getString(4));
//                }
//            } finally {
//                cursorArticles.close();
//            }
//            db.closeDBs();
//        }
//    }
}
