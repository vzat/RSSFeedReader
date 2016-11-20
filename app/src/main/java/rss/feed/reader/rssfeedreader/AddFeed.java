package rss.feed.reader.rssfeedreader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddFeed extends AppCompatActivity implements View.OnClickListener {
    Intent intent;
    int requestCode;
    int directoryID;
    EditText feedName, feedURL;
    Button addFeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feed);

        setResult(-1);

        setTitle("Add Feed");

        intent = getIntent();
        requestCode = intent.getIntExtra("requestCode", 1);
        directoryID = intent.getIntExtra("directoryID", 1);
        feedName = (EditText) findViewById(R.id.feedName);
        feedURL = (EditText) findViewById(R.id.feedURL);
        addFeed = (Button) findViewById(R.id.addFeed);
        addFeed.setOnClickListener(this);

        if (requestCode == 2) {
            feedName.setText(intent.getStringExtra("feedName"));
            feedName.setSelection(intent.getStringExtra("feedName").length());
            feedURL.setText(intent.getStringExtra("feedURL"));
            feedURL.setSelection(intent.getStringExtra("feedURL").length());
            addFeed.setText("Edit Feed");
            setTitle("Edit Feed");
        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.addFeed) {
            // Get the name and url of the feed from the editTexts
            String name = feedName.getText().toString().trim();
            String url = feedURL.getText().toString().trim();

            if (name.length() > 0 && url.length() > 0) {
                DatabaseHelper db = DatabaseHelper.getInstance(this);

                // Either insert or edit feed
                if (requestCode == 1) {
                    db.insertFeed(name, url, directoryID);
                } else if (requestCode == 2) {
                    db.updateFeed(intent.getIntExtra("feedID", -1), name, url);
                }

                setResult(1);
                finish();
            } else {
                // Display an error message if some fields are empty
                if (name.length() == 0) {
                    Toast.makeText(this, "Feed Name Empty", Toast.LENGTH_SHORT).show();
                } else if (url.length() == 0){
                    Toast.makeText(this, "Feed URL Empty", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}