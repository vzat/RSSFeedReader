/* **************************************************
Author: Vlad Zat
Description: Parse XML from a String

Created: 2016/11/12
Modified: 2016/11/13
************************************************** */

package rss.feed.reader.rssfeedreader;

import android.text.Html;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class XMLParser {
    public static ArrayList<Article> getParserData(String data) {

        ArrayList<Article> articles = new ArrayList<Article>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        // Check if the feed is RSS or Atom
        if (data.indexOf("<rss") != -1) {

            // RSS Feed
            int startItem = data.indexOf("<item>", 0);
            DateFormat pubDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");

            while (startItem != -1) {
                // Get the position of the end tag of the item
                int endItem = data.indexOf("</item>", startItem);
                endItem = (endItem == -1) ? data.length() : endItem;

                // Get the article data from the item
                String title, description, link, date;

                title = getTextFromTag(data, "title", startItem, endItem);
                description = getTextFromTag(data, "description", startItem, endItem);
                link = getTextFromTag(data, "link", startItem, endItem);

                // Convert pubDate or current time to common format
                String initialDate = getTextFromTag(data, "pubDate", startItem, endItem);
                if (initialDate != null) {
                    try {
                        Date pubDate = pubDateFormat.parse(initialDate);
                        date = dateFormat.format(pubDate);
                    } catch (ParseException e) {
                        date = dateFormat.format(Calendar.getInstance().getTime());
                        Log.e("Error Parsing PubDate ", e.toString());
                    }
                } else {
                    date = dateFormat.format(Calendar.getInstance().getTime());
                }

                articles.add(new Article(title, description, link, date));

                // Get the position of the start tag of the item
                startItem = data.indexOf("<item>", startItem + 1);
            }
            return articles;
        } else if (data.indexOf("<feed") != -1) {

            // Atom Feed
            int startItem = data.indexOf("<entry>", 0);
            DateFormat updatedFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");

            while (startItem != -1) {
                // Get the position of the end tag of the item
                int endItem = data.indexOf("</entry>", startItem);
                endItem = (endItem == -1) ? data.length() : endItem;

                // Get the article data from the item
                String title, description, link, date;

                title = getTextFromTag(data, "title", startItem, endItem);
                description = getTextFromTag(data, "summary", startItem, endItem);

                // In Atom the link is stored in the href attribute
                link = null;
                int linkStart = data.indexOf("<link", startItem);
                if (linkStart != -1) {
                    linkStart = data.indexOf("href=", linkStart);
                    if (linkStart != -1) {
                        linkStart = linkStart + "href=".length() + 1;
                        int linkEnd = data.indexOf('"', linkStart);
                        if (linkEnd != -1 && linkEnd < endItem) {
                            link = data.substring(linkStart, linkEnd);
                        }
                    }
                }

                // Convert updated or current time to common format
                String initialDate = getTextFromTag(data, "updated", startItem, endItem);
                if (initialDate != null) {
                    try {
                        Date updated = updatedFormat.parse(initialDate);
                        date = dateFormat.format(updated);
                    } catch (ParseException e) {
                        date = dateFormat.format(Calendar.getInstance().getTime());
                        Log.e("Error Parsing PubDate ", e.toString());
                    }
                } else {
                    date = dateFormat.format(Calendar.getInstance().getTime());
                }

                articles.add(new Article(title, description, link, date));

                // Get the position of the start tag of the item
                startItem = data.indexOf("<entry>", startItem + 1);
            }
            return articles;
        }

        return null;
    }

    private static String getTextFromTag(String data, String tag, int indexStart, int indexEnd) {

        // Create the start and end tag
        String tagStart = "<" + tag + ">";
        String tagEnd = "</" + tag + ">";

        // Get the position of the start of the text and the end of the text
        int startText = data.indexOf(tagStart, indexStart) + tagStart.length();
        int endText = data.indexOf(tagEnd, indexStart);

        // Return the text from the tag if the text is within the start and end index
        if (endText <= indexEnd && startText != -1 && endText != -1) {
            String text = data.substring(startText, endText).trim();

            // Strip the text of html tags
            if ("title".equals(tag) || "description".equals(tag) || "summary".equals(tag)) {
                // Convert html to string
                text = Html.fromHtml(text).toString();
                // Remove starting and ending tags
                text = text.replaceAll("<[^>]+>", "");
                text = text.replaceAll("<[^>]+/>", "");
                // Remove html characters
                text = text.replaceAll("&#\\w{2,4};", "");
                text = text.replaceAll("&\\w{3,7};", "");
            }
            return text;
        } else {
            return null;
        }
    }
}
