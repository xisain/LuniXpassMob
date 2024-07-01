package com.example.lunixpassmob.model.user;

public class Profile {
    private String username;
    private String email;
    private String role;
    private String uid;

    private Subscriptions subscription;

    public Profile(String username, String email, String role, String uid, Subscriptions subscription){
       this.username = username;
        this.email = email;
        this.role = role;
        this.uid = uid;
        this.subscription = subscription;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Subscriptions getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscriptions subscription) {
        this.subscription = subscription;
    }
}
