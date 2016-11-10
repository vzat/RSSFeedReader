package rss.feed.reader.rssfeedreader;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class XMLParser {
    public static ArrayList<Article> getParserData(String data) {
        try {
            // Create Instance of a XML Parser
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            // Set the Parser Input
            parser.setInput(new StringReader(data));

            // Create ArrayList where to store the data
            ArrayList<Article> articles= new ArrayList<Article>();

            // Create event type variable to check the current location in the XML file
            int eventType = parser.getEventType();

            // Create varaible to store the current tag
            String tag = "IGNORE";

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    tag = parser.getName();
                } else if (eventType == XmlPullParser.TEXT) {
                    if ("item".equals(tag)) {
                        articles.add(new Article());
                    } else if ("title".equals(tag)) {
                        articles.get(articles.size() - 1).setTitle(parser.getText().trim());
                    } else if ("description".equals(tag)) {
                        articles.get(articles.size() - 1).setDescription(parser.getText().trim());
                    } else if ("link".equals(tag)) {
                        articles.get(articles.size() - 1).setLink(parser.getText().trim());
                    }
                }
                eventType = parser.next();
            }
            return articles;
        } catch (Exception e) {
            Log.e("XML Parse Exception ", e.toString());
        }
        return  null;
    }
}
