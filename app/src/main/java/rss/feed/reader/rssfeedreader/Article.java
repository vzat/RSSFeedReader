/* **************************************************
Author: Vlad Zat
Description: Encapsulates article information

Created: 2016/11/12
Modified: 2016/11/12
************************************************** */


package rss.feed.reader.rssfeedreader;

public class Article {
    private String title;
    private String description;
    private String link;
    private String date;

    public Article(String title, String description, String link, String date) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.setDate(date);
    }

    public Article() {
        title = "Unknown Title";
        description = "Unknown Description";
        link = "www.google.com";
        setDate("???");
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String toString() {
        return "Title: " + title + " Description: " + description + " Link: " + link;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}