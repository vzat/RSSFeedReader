/* **************************************************
Author: Vlad Zat
Description: Main Page where it displays all the directories

Created: 2016/11/12
Modified: 2016/11/26
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
import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnCreateContextMenuListener, ExpandableListView.OnChildClickListener {
    ExpandableListView expandableListView;
    SimpleCursorTreeAdapter treeAdapter;
    Cursor directoryTypes;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Directories");

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
                if ("Read Later".equals(groupCursor.getString(1))) {
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
            // Update the cursor for the type of directory created or edited
            if (resultCode == 0) {
                treeAdapter.setChildrenCursor(0, db.getAllDirectories("Saved"));
            } else if (resultCode == 1) {
                treeAdapter.setChildrenCursor(1, db.getAllDirectories("Feed"));
            }

            // Display message if a directory was added or edited
            if (resultCode != -1)
                if (requestCode == 1)
                    showToast("Directory Added");
                else
                    showToast("Directory Edited");
        }
    }

    public boolean onChildClick(ExpandableListView l, View v, int groupPosition, int childPosition, long id) {
        Cursor directorySelected = treeAdapter.getChild(groupPosition, childPosition);

        // Go to the directory selected
        Intent goToDirectory = new Intent(this, FeedDirectory.class);
        goToDirectory.putExtra("directoryID", directorySelected.getInt(0));
        goToDirectory.putExtra("directoryName", directorySelected.getString(1));
        goToDirectory.putExtra("directoryType", directorySelected.getString(2));
        startActivity(goToDirectory);

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

                // Get the Context Menu Options and create it

                // Reference the following code is from https://www.mikeplate.com/2010/01/21/show-a-context-menu-for-long-clicks-in-an-android-listview/
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
}
