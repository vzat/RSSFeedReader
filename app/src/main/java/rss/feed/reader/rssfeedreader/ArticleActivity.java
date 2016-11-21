package rss.feed.reader.rssfeedreader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

public class ArticleActivity extends AppCompatActivity {
    Intent intent;
    int articleID;
    String articleTitle, articleDescription, articleLink, articleDate;

    TextView title, description, link, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        intent = getIntent();

        // Get the data
        articleID = intent.getIntExtra("articleID", -1);
        articleTitle = intent.getStringExtra("articleTitle");
        articleDescription = intent.getStringExtra("articleDescription");
        articleLink = intent.getStringExtra("articleLink");
        articleDate = intent.getStringExtra("articleDate");

//        Spanned span = Html.fromHtml(articleDescription);
//        articleDescription = span.toString();

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
}
