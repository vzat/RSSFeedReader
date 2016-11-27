/* **************************************************
Author: Vlad Zat

Created: 2016/11/26
Modified: 2016/11/27
************************************************** */

package rss.feed.reader.rssfeedreader;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class MyWebView extends AppCompatActivity {
    WebView webView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_web_view);
        setTitle("");
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            Log.d("No Action Bar ", e.getStackTrace().toString());
        }

        // Setup WebView
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        webView = (WebView) findViewById(R.id.webView);
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView ww, int progress) {
                progressBar.setProgress(progress);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView ww, WebResourceRequest request) {
                webView.loadUrl(request.getUrl().toString());
                return false;
            }
            public void onPageStarted(WebView ww, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
                setTitle("");
            }
            public void onPageFinished(WebView ww, String url) {
                progressBar.setVisibility(View.INVISIBLE);
                setTitle(webView.getTitle());
            }
        });
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.loadUrl(getIntent().getStringExtra("linkURL"));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.webview, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.openBrowser) {
            // Open the current link in an external browser
            try {
                Intent openBrowser = new Intent(Intent.ACTION_VIEW);
                openBrowser.setData(Uri.parse(webView.getUrl()));
                startActivity(openBrowser);
            } catch (ActivityNotFoundException e) {
                Log.d("Cannot open browser ", e.getStackTrace().toString());
            }
            return true;
        }
        if (menuItem.getItemId() == android.R.id.home) {
            // Exit activity if the return button is pressed
            finish();
            return true;
        }
        return false;
    }

    public void onBackPressed() {
        // If there is a previous web page go to it otherwise return to the previous activity
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            finish();
        }
    }
}
