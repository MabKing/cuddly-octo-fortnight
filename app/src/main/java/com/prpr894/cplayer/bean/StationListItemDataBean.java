package com.prpr894.cplayer.bean;

import java.io.Serializable;

/**
 * "title":"娴瓙",
 * "xinimg":"http://i1.mobile-dad.com/uploads/apk_img/201801/556763_1_151703117360af.png",
 * "address":"/live/langzi.html",
 * "Number":"127"
 */
public class StationListItemDataBean implements Serializable {
    private String title;
    private String xinimg;
    private String address;
    private String Number;

    @Override
    public String toString() {
        return "StationListItemDataBean{" +
                "title='" + title + '\'' +
                ", xinimg='" + xinimg + '\'' +
                ", address='" + address + '\'' +
                ", Number='" + Number + '\'' +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getXinimg() {
        return xinimg;
    }

    public void setXinimg(String xinimg) {
        this.xinimg = xinimg;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        this.Number = number;
    }
}
