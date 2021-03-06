/* **************************************************
Author: Vlad Zat
Description: Save an article to a saved directory

Created: 2016/11/19
Modified: 2016/11/20
************************************************** */

package rss.feed.reader.rssfeedreader;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

public class SaveArticle extends AppCompatActivity implements ListView.OnItemClickListener {
    private Intent intent;

    private ListView listView;
    private CursorRadioAdapter adapter;
    private DatabaseHelper db;

    private int articleID;
    private int savedDirectoryID;

    // Custom adapter to show the directory name and a radio button next to it
    private class CursorRadioAdapter extends CursorAdapter {
        int savedDirectoryID;

        public CursorRadioAdapter(Context context, Cursor cursor, int savedDirectoryID) {
            super(context, cursor, 0);
            this.savedDirectoryID = savedDirectoryID;
        }

        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.row_save_article, parent, false);
        }

        public void bindView(View view, Context context, Cursor cursor) {
            TextView textView = (TextView) view.findViewById(R.id.savedDirectory);
            textView.setText(cursor.getString(1));

            // Check the corresponding directory name if the article is already saved in a directory
            if (savedDirectoryID != -1 && savedDirectoryID == cursor.getInt(0)) {
                RadioButton radioButton = (RadioButton) view.findViewById(R.id.radio);
                radioButton.setChecked(true);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_article);
        setResult(-1);

        // Set up initial values for the article and saved directory
        db = DatabaseHelper.getInstance(this);
        intent = getIntent();
        articleID = intent.getIntExtra("articleID", -1);
        savedDirectoryID = db.getSavedDirectory(articleID);
        setTitle("Save " + intent.getStringExtra("articleTitle"));

        // Set up listView
        listView = (ListView) findViewById(R.id.list);
        adapter = new CursorRadioAdapter(this, db.getAllDirectories("Saved"), savedDirectoryID);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    public void onItemClick(AdapterView l, View v, int position, long id) {
        Cursor cursor = (Cursor) adapter.getItem(position);

        // Deselect all the radio buttons
        // Reference the following code is from http://stackoverflow.com/questions/8337180/custom-single-choice-listview
        for (int i = 0 ; i < l.getCount() ; i++) {
            RadioButton radioButton = (RadioButton) l.getChildAt(i).findViewById(R.id.radio);
            radioButton.setChecked(false);
        }
        // Reference Complete

        // Check only the radio button pressed
        if (cursor.getInt(0) != savedDirectoryID) {
            RadioButton radioButton = (RadioButton) v.findViewById(R.id.radio);
            radioButton.setChecked(true);
            savedDirectoryID = cursor.getInt(0);
            setResult(1);
        } else {
            savedDirectoryID = -1;
            setResult(0);
        }
    }

    public void onPause() {
        super.onPause();
        db.setSavedDirectory(articleID, savedDirectoryID);
    }
}