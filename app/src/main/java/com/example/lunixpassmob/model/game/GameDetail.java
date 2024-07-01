package com.example.lunixpassmob.model.game;

public class GameDetail {
    private String publisher;
    private String release_date;
    private String size;

    public GameDetail(String publisher, String release_date, String size){
        this.publisher = publisher;
        this.release_date = release_date;
        this.size = size;
    }
    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
