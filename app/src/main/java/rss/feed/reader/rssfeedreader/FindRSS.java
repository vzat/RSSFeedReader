/* **************************************************
Author: Vlad Zat

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
public class FindRSS extends AsyncTask<String, Void, String> {
    TaskComplete taskComplete;
    Context context;

    public FindRSS(TaskComplete taskComplete, Context context) {
        this.taskComplete = taskComplete;
        this.context = context;
    }

    protected String doInBackground(String... args) {
        String xml;

        try {
            InputStream in = new URL(args[0]).openStream();
            try {
                // The IOUtils method is from the external library Apache Commons IO
                xml = IOUtils.toString(in, StandardCharsets.UTF_8);

                // Change to http if https is not working
                if ("".equals(xml)) {
                    args[0] = args[0].replace("https", "http");
                    in = new URL(args[0]).openStream();
                    xml = IOUtils.toString(in, StandardCharsets.UTF_8);
                }

                // Check if the current link is a valid rss feed
                if (xml.contains("<item>") || xml.contains("<entry>")) {
                    return args[0];
                }

                String type = "NONE";
                if (xml.contains("type=\"application/rss+xml\"")) {
                    // Check if the page contains a link to a rss feed
                    type = "type=\"application/rss+xml\"";
                } else if (xml.contains("type=\"application/atom+xml\"")) {
                    // Check if the page contains a link to an atom feed
                    type = "type=\"application/atom+xml\"";
                }

                if (!"NONE".equals(type)) {
                    // Get the url of the RSS of Atom Feed
                    int typePos = xml.indexOf(type);
                    int linkPos = xml.lastIndexOf("link", typePos);
                    int hrefPos = xml.indexOf("href", linkPos);
                    int linkStart = xml.indexOf("\"", hrefPos);
                    int linkEnd = xml.indexOf("\"", linkStart + 1);

                    if (linkStart != -1 && linkEnd != -1) {
                        // Get the URL and check if it's a valid RSS or Atom Feed
                        String url = xml.substring(linkStart + 1, linkEnd);
                        in = new URL(url).openStream();

                        // The IOUtils method is from the external library Apache Commons IO
                        xml = IOUtils.toString(in, StandardCharsets.UTF_8);

                        // Check if the current link is a valid rss feed
                        if (xml.contains("<item>") || xml.contains("<entry>")) {
                            return url;
                        }
                    }
                }
                return "ERROR";
            } finally {
                in.close();
            }
        } catch (MalformedURLException e) {
            Log.e("URL Error ", e.toString());
            return "ERROR";
        } catch (IOException e) {
            Log.e("IO Error ", e.toString());
            return "ERROR";
        }

    }

    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        // Callback to the Activity
        taskComplete.callback(result);
    }
}