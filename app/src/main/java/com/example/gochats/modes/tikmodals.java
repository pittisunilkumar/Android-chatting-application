package com.example.gochats.modes;

public class tikmodals
{
    public tikmodals()
    {
    }

    private String title;
    private String description;
    private String user_id;

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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPost_catagories() {
        return post_catagories;
    }

    public void setPost_catagories(String post_catagories) {
        this.post_catagories = post_catagories;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getMedia_url() {
        return media_url;
    }

    public void setMedia_url(String media_url) {
        this.media_url = media_url;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public tikmodals(String title, String description, String user_id, String post_catagories, String post_id, String view, String user_name, String media_url, String thumbnail) {
        this.title = title;
        this.description = description;
        this.user_id = user_id;
        this.post_catagories = post_catagories;
        this.post_id = post_id;
        this.view = view;
        this.user_name = user_name;
        this.media_url = media_url;
        this.thumbnail = thumbnail;
    }

    private String post_catagories;
    private String post_id;
    private String view;
    private String user_name;
    private String media_url;
    private String thumbnail;
}
