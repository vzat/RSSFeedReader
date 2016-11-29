/* **************************************************
Author: Vlad Zat
Description: Add a new feed to the current directory

Created: 2016/11/19
Modified: 2016/11/20
************************************************** */

package rss.feed.reader.rssfeedreader;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class AddFeed extends AppCompatActivity implements View.OnClickListener, TaskComplete {
    private Intent intent;
    private int requestCode;
    private int directoryID;
    private EditText feedName, feedURL;
    private Button addFeed;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feed);

        // Setup the activity
        setResult(-1);
        setTitle("Add Feed");
        intent = getIntent();
        requestCode = intent.getIntExtra("requestCode", 1);
        directoryID = intent.getIntExtra("directoryID", 1);
        feedName = (EditText) findViewById(R.id.feedName);
        feedURL = (EditText) findViewById(R.id.feedURL);
        addFeed = (Button) findViewById(R.id.addFeed);
        addFeed.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.loadingFeed);
        progressBar.setVisibility(View.GONE);

        // Specific settings if the feed is edited
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
                // Check if there is a network connection
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    // Add https in front of the link if there is none or modify it if it's not secure
                    if (!url.contains("http")) {
                        url = "https://" + url;
                    } else if (!url.contains("https")) {
                        url = url.replace("http", "https");
                    }

                    // Check the url or get a new one
                    new FindRSS(this, this).execute(url);

                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(this, "No Network Connection Available \nFeed may be invalid", Toast.LENGTH_SHORT).show();
                    callback(url);
                }
            } else {
                // Display an error message if some fields are empty
                if (name.length() == 0) {
                    Toast.makeText(this, "Feed Name Empty", Toast.LENGTH_SHORT).show();
                } else if (url.length() == 0) {
                    Toast.makeText(this, "Feed URL Empty", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void callback() {}

    public void callback(String url) {
        progressBar.setVisibility(View.GONE);
        if (!"ERROR".equals(url)) {
            String name = feedName.getText().toString().trim();
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
            // Display an error message if no feed was found
            Toast.makeText(this, "No Feed Found", Toast.LENGTH_SHORT).show();
        }
    }
}
