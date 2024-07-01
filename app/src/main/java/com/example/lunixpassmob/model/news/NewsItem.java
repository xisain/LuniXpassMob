package com.example.lunixpassmob.model.news;

public class NewsItem {
    private String title;
    private String imageUrl;
    private String id;
    public NewsItem(String id,String imageUrl, String title) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
