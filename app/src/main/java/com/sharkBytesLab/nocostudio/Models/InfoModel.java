package com.sharkBytesLab.nocostudio.Models;

public class InfoModel {

    int num;
    int songs;
    int subs;

    public InfoModel(int num, int songs, int subs) {
        this.num = num;
        this.songs = songs;
        this.subs = subs;
    }

    public int getSubs() {
        return subs;
    }

    public void setSubs(int subs) {
        this.subs = subs;
    }

    public InfoModel() {
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getSongs() {
        return songs;
    }

    public void setSongs(int songs) {
        this.songs = songs;
    }
}
