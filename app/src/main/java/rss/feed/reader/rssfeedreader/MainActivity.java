package rss.feed.reader.rssfeedreader;

import org.apache.commons.io.IOUtils;

import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TaskComplete {

    int noFeeds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new DataFromFeed(this, this).execute("https://www.reddit.com/.rss");
        new DataFromFeed(this, this).execute("https://www.rte.ie/rss/");

        noFeeds = 2;

//        ArrayList<Article> articles = XMLParser.getParserData("<rss> " +
//                "<item><title>Title1</title><description>Desc1</description><link>www.google.com</link></item> " +
//                "<item><title>Title2</title><description>Desc2</description><link>www.google.com</link></item> " +
//                "<item><title>Title3</title><description>Desc3</description><link>www.google.com</link></item> " +
//                "</rss>");
//
//        for (int i = 0 ; i < articles.size() ; i++) {
//            System.out.println(articles.get(i));
//        }

//        System.out.println("Line Before");
//        System.out.println("Before" + XMLParser.getTextFromTag("<feed><title>  Hello World! </title></feed>", "title", 0, 28) + "After");

//        try {
//            InputStream in = new URL("https://www.reddit.com/.rss").openStream();
//            try {
//                xml = IOUtils.toString(in, StandardCharsets.UTF_8);
//                System.out.println(xml);
//            } finally {
//                in.close();
//            }
//        } catch (MalformedURLException e) {
//            Log.e("URL Error ", e.toString());
//        } catch (IOException e) {
//            Log.e("IO Error ", e.toString());
//        }

//        String XMLTest = "<item><title>Hello</title><link>www.link1.com</link><description>Desc</description></item>";
//        ArrayList<Article> articles= XMLParser.getParserData(XMLTest);
//        // No array gets returned !!!!!
//
//        System.out.println(articles.get(0).getTitle());
//        System.out.println(articles.get(0).getDescription());
//        System.out.println(articles.get(0).getLink());
    }

    public void callback() {
        noFeeds --;

        if (noFeeds == 0) {
            DatabaseHelper db = DatabaseHelper.getInstance(this);
            Cursor cursorArticles = db.getAllArticles();

            try {
                while (cursorArticles.moveToNext()) {
                    Log.d("Cursor: ",   "Title: " + cursorArticles.getString(0) +
                                        ", Description: " + cursorArticles.getString(1) +
                                        ", Link: " + cursorArticles.getString(2) +
                                        ", Date: " + cursorArticles.getString(3));
                }
            } finally {
                cursorArticles.close();
            }
        }

//        System.out.println("The Async Task is Complete");
    }
}
