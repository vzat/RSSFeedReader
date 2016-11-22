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
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class SaveArticle extends AppCompatActivity implements ListView.OnItemClickListener {
    Intent intent;

    ListView listView;
//    SimpleCursorAdapter adapter;
    CursorRadioAdapter adapter;
    DatabaseHelper db;

    int articleID;
    int savedDirectoryID;

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

        // Set up initial values for the article and saved directory
        db = DatabaseHelper.getInstance(this);
        intent = getIntent();
        articleID = intent.getIntExtra("articleID", -1);
        savedDirectoryID = db.getSavedDirectory(articleID);
        System.out.println(savedDirectoryID);

        // Set up listView
        listView = (ListView) findViewById(R.id.list);
//        adapter = new SimpleCursorAdapter(this, R.layout.row_save_article, db.getAllDirectories("Saved"), new String[] {"directoryName"}, new int[] {R.id.savedDirectory}, 0);
        adapter = new CursorRadioAdapter(this, db.getAllDirectories("Saved"), savedDirectoryID);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    public void onItemClick(AdapterView l, View v, int position, long id) {
        Cursor cursor = (Cursor) adapter.getItem(position);

        //savedDirectoryID = cursor.getInt(0);

        // Reference the following code is from http://stackoverflow.com/questions/8337180/custom-single-choice-listview
        for (int i = 0 ; i < l.getCount() ; i++) {
            RadioButton radioButton = (RadioButton) l.getChildAt(i).findViewById(R.id.radio);
            radioButton.setChecked(false);
        }
        // Reference Complete

        if (cursor.getInt(0) != savedDirectoryID) {
            RadioButton radioButton = (RadioButton) v.findViewById(R.id.radio);
            radioButton.setChecked(true);
            savedDirectoryID = cursor.getInt(0);
        } else {
            savedDirectoryID = -1;
        }
    }

    public void onPause() {
        super.onPause();
        db.setSavedDirectory(articleID, savedDirectoryID);
    }
}