package rss.feed.reader.rssfeedreader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String XMLTest = "<item><title>Hello</title><link>www.link1.com</link><description>Desc</description></item>";
        ArrayList<Article> articles= XMLParser.getParserData(XMLTest);
        // No array gets returned !!!!!

        System.out.println(articles.get(0).getTitle());
        System.out.println(articles.get(0).getDescription());
        System.out.println(articles.get(0).getLink());
    }
}
