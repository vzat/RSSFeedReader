/* **************************************************
Author: Vlad Zat
Description: Get articles from a feed in an asynchronous task

Created: 2016/11/12
Modified: 2016/11/13
************************************************** */

package rss.feed.reader.rssfeedreader;

import android.content.Context;
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
public class DataFromFeed extends AsyncTask<Object, Void, Void> {
    TaskComplete taskComplete;
    Context context;

    public DataFromFeed(TaskComplete taskComplete, Context context) {
        this.taskComplete = taskComplete;
        this.context = context;
    }

    protected Void doInBackground(Object... args) {
        String xml;

        try {
            InputStream in = new URL((String)args[0]).openStream();
            try {
                // The IOUtils method is from the external library Apache Commons IO
                xml = IOUtils.toString(in, StandardCharsets.UTF_8);

                // Store the articles in the database
                ArrayList<Article> articles = XMLParser.getParserData(xml);
                DatabaseHelper db = DatabaseHelper.getInstance(context);
                if (articles != null)
                    db.insertArticles(articles, (Integer)args[1]);
            } finally {
                in.close();
            }
        } catch (MalformedURLException e) {
            Log.e("URL Error ", e.toString());
        } catch (IOException e) {
            Log.e("IO Error ", e.toString());
        }

        return null;
    }

    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        // Callback to the Activity
        taskComplete.callback();
    }
}