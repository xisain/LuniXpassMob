package com.example.lunixpassmob.model.user;

public class Subscriptions {
    private String subs_start_date;
    private String subs_end_date;

    private Boolean subs_status;

    public Subscriptions(String subs_start_date, String subs_end_date, Boolean subs_status){
        this.subs_start_date = subs_start_date;
        this.subs_end_date = subs_end_date;
        this.subs_status = subs_status;


    }

    public String getSubs_start_date() {
        return subs_start_date;
    }

    public void setSubs_start_date(String subs_start_date) {
        this.subs_start_date = subs_start_date;
    }

    public String getSubs_end_date() {
        return subs_end_date;
    }

    public void setSubs_end_date(String subs_end_date) {
        this.subs_end_date = subs_end_date;
    }

    public Boolean getSubs_status() {
        return subs_status;
    }

    public void setSubs_status(Boolean subs_status) {
        this.subs_status = subs_status;
    }
}
