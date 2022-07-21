package com.sharkBytesLab.nocostudio.Models;

public class DownloadModel {

    String image;
    String title;
    String link;
    String videoId;




    public DownloadModel()
    {

    }

    public DownloadModel(String image, String title, String link, String videoId) {
        this.image = image;
        this.title = title;
        this.link = link;
        this.videoId = videoId;
    }




    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }
}
