package rss.feed.reader.rssfeedreader;

import org.apache.commons.io.IOUtils;

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

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String xml;
        new StringFromURL().execute("https://www.reddit.com/.rss");
        // android.os.NetworkOnMainThreadException

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

    // AsyncTask<Params, Progress, Result>
    private class StringFromURL extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... url) {
            String xml;

            try {
                InputStream in = new URL("https://www.reddit.com/.rss").openStream();
                try {
                    xml = IOUtils.toString(in, StandardCharsets.UTF_8);
                    return xml;
                } finally {
                    in.close();
                }
            } catch (MalformedURLException e) {
                Log.e("URL Error ", e.toString());
            } catch (IOException e) {
                Log.e("IO Error ", e.toString());
            }

            return "ERROR";
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }
}
