package rss.feed.reader.rssfeedreader;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

// AsyncTask<Params, Progress, Result>
public class DataFromFeed extends AsyncTask<String, Void, String> {
    TaskComplete taskComplete;

    public DataFromFeed(TaskComplete taskComplete) {
        this.taskComplete = taskComplete;
    }

    protected String doInBackground(String... url) {
        String xml;

        try {
            InputStream in = new URL(url[0]).openStream();
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
        ArrayList<Article> articles = XMLParser.getParserData(result);

        for (int i = 0 ; i < articles.size() ; i++) {
            System.out.println(articles.get(i));
        }

        // Callback to the Activity
        taskComplete.callback();
    }
}