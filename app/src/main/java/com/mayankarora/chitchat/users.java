package com.mayankarora.chitchat;

public class users {
    private String image;
    private String name;
    private String status;
    private String thumb_image;
    private String online;

    public String getThumb_image() {
        return thumb_image;
    }

    public users() {
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public users(String image, String name, String status, String thumb_image,String online) {
        this.image = image;
        this.name = name;
        this.status = status;
        this.thumb_image=thumb_image;
        this.online=online;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }
}
