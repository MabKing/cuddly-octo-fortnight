package com.prpr894.cplayer.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * "userid":"396577",
 * "title":"火车轮流开",
 * "img":"http://xiansamu.com/api/public/upload/avatar/20180225/1519558044_avatar.jpg",
 * "address":"rtmp://pull.xiansamu.com/5showmic/396577_1519808324"
 */
@Entity
public class LiveRoomItemDataBean {

    @Id
    private String address;
    private String title;
    private String img;


    @Generated(hash = 412868379)
    public LiveRoomItemDataBean(String address, String title, String img) {
        this.address = address;
        this.title = title;
        this.img = img;
    }

    @Generated(hash = 1615497048)
    public LiveRoomItemDataBean() {
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
