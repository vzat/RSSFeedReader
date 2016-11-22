package rss.feed.reader.rssfeedreader;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleCursorAdapter;

public class SaveArticle extends AppCompatActivity implements ListView.OnItemClickListener {
    Intent intent;

    ListView listView;
    SimpleCursorAdapter adapter;
    DatabaseHelper db;

    int articleID;
    int savedDirectoryID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_article);

        // Set up listView
        db = DatabaseHelper.getInstance(this);
        listView = (ListView) findViewById(R.id.list);
        adapter = new SimpleCursorAdapter(this, R.layout.row_save_article, db.getAllDirectories("Saved"), new String[] {"directoryName"}, new int[] {R.id.savedDirectory}, 0);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        // Set up initial values for the article and saved directory
        intent = getIntent();
        articleID = intent.getIntExtra("articleID", -1);
        savedDirectoryID = db.getSavedDirectory(articleID);
        System.out.println(savedDirectoryID);

        // If article is already saved then check that radio button
        // *** MAKE A CUSTOM ADAPTER
        if (savedDirectoryID != -1) {
            Cursor cursor = adapter.getCursor();
            int pos = 0;
            while (cursor.moveToNext()) {
                if (cursor.getInt(0) == savedDirectoryID)
                    break;
                pos++;
            }
            ((RadioButton)listView.getChildAt(pos)).setChecked(true);
        }

    }

    public void onItemClick(AdapterView l, View v, int position, long id) {
        Cursor cursor = (Cursor) adapter.getItem(position);

        savedDirectoryID = cursor.getInt(0);

        System.out.println("CLICKED ON ITEM");

        // Reference the following code is from http://stackoverflow.com/questions/8337180/custom-single-choice-listview
        for (int i = 0 ; i < l.getCount() ; i++) {
            RadioButton radioButton = (RadioButton) l.getChildAt(i).findViewById(R.id.radio);
            radioButton.setChecked(false);
            System.out.println("FALSE " + i);
        }

        RadioButton radioButton = (RadioButton) v.findViewById(R.id.radio);
        radioButton.setChecked(true);
        // Reference Complete
    }

    public void onBackPressed() {
        super.onBackPressed();

        // Update the article in the database with the savedDirectory
        db.setSavedDirectory(articleID, savedDirectoryID);

        finish();
    }
}