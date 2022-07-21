package com.sharkBytesLab.nocostudio.Models;

public class PromotionWebViewModel {

    String channelUrl;
    String channelImage;
    String channelName;


    public PromotionWebViewModel(){}

    public PromotionWebViewModel(String channelUrl, String channelImage, String channelName) {
        this.channelUrl = channelUrl;
        this.channelImage = channelImage;
        this.channelName = channelName;
    }

    public String getChannelUrl() {
        return channelUrl;
    }

    public void setChannelUrl(String channelUrl) {
        this.channelUrl = channelUrl;
    }

    public String getChannelImage() {
        return channelImage;
    }

    public void setChannelImage(String channelImage) {
        this.channelImage = channelImage;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
}
