package rss.feed.reader.rssfeedreader;

public class Article {
    private String title;
    private String description;
    private String link;

    public Article(String title, String description, String link) {
        this.title = title;
        this.description = description;
        this.link = link;
    }

    public Article() {
        title = "Unknwon Title";
        description = "Unknown Description";
        link = "www.google.com";
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
}
