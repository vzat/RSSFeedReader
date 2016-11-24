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
        directoryID = this.getIntent().getIntExtra("directoryID", 0);
        setTitle(this.getIntent().getStringExtra("directoryName") + " Filters");

        // Set up listView
        listView = (ListView) findViewById(R.id.list);
        db = DatabaseHelper.getInstance(this);
        adapter = new SimpleCursorAdapter(this, R.layout.row_filter, db.getFilters(directoryID), new String[] {"filterName"}, new int[] {R.id.filter}, 0);
        listView.setAdapter(adapter);
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
        }
    }


}
