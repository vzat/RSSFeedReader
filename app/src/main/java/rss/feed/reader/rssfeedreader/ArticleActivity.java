/* **************************************************
Author: Vlad Zat
Description: Display an article

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
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

public class ArticleActivity extends AppCompatActivity {
    private Intent intent;
    private int articleID;
    private String articleTitle, articleDescription, articleLink, articleDate;

    private TextView title, description, link, date;

    private float touchPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        setTitle("");

        intent = getIntent();

        // Get the data
        articleID = intent.getIntExtra("articleID", -1);
        articleTitle = intent.getStringExtra("articleTitle");
        articleDescription = intent.getStringExtra("articleDescription");
        articleLink = intent.getStringExtra("articleLink");
        articleDate = intent.getStringExtra("articleDate");

        // Get the textViews
        title = (TextView) findViewById(R.id.articleTitle);
        description = (TextView) findViewById(R.id.articleDescription);
        link = (TextView) findViewById(R.id.articleLink);
        date = (TextView) findViewById(R.id.articleDate);

        // Set the text
        title.setText(articleTitle);
        description.setText(articleDescription);
        link.setText(articleLink);
        date.setText(articleDate);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.article, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.saveArticle) {

            Intent saveArticle = new Intent(this, SaveArticle.class);
            saveArticle.putExtra("articleID", articleID);
            saveArticle.putExtra("articleTitle", articleTitle);
            startActivityForResult(saveArticle, 1);
            return true;
        }
        return false;
    }

    public void onActivityResult(int request, int response, Intent data) {
        if (request == 1 && response == 1) {
            Toast.makeText(this, "Article Saved", Toast.LENGTH_SHORT).show();
        } else if (request == 1 && response == 0) {
            Toast.makeText(this, "Article Removed", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadPage(View v) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // If the link is pressed then open the webview
            String linkURL = ((TextView) v).getText().toString();
            Intent loadURL = new Intent(this, MyWebView.class);
            loadURL.putExtra("linkURL", linkURL);
            loadURL.putExtra("articleTitle", articleTitle);
            startActivity(loadURL);
        } else {
            Toast.makeText(this, "No Network Connection Available", Toast.LENGTH_SHORT);
        }
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        // Unblock the touchEvent because of the scrollView
        super.dispatchTouchEvent(event);
        return onTouchEvent(event);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // Get the position where the screen was touched
            touchPos = event.getX();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            // Calculate the distance swiped by the finger
            if (event.getX() - touchPos > 300) {
                // Swipe Left
                setResult(1);
                finish();
            } else if (event.getX() - touchPos < -300) {
                // Swipe Right
                setResult(2);
                finish();
            }
        }
        return super.onTouchEvent(event);
    }
}
