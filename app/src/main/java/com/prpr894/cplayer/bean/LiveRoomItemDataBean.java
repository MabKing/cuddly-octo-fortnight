package com.prpr894.cplayer.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * "userid":"396577",
 * "nickname":"火车轮流开",
 * "logourl":"http://xiansamu.com/api/public/upload/avatar/20180225/1519558044_avatar.jpg",
 * "play_url":"rtmp://pull.xiansamu.com/5showmic/396577_1519808324"
 */
@Entity
public class LiveRoomItemDataBean {
    @Id
    private String userid;
    private String nickname;
    private String logourl;
    private String play_url;

    @Generated(hash = 949875384)
    public LiveRoomItemDataBean(String userid, String nickname, String logourl,
            String play_url) {
        this.userid = userid;
        this.nickname = nickname;
        this.logourl = logourl;
        this.play_url = play_url;
    }

    @Generated(hash = 1615497048)
    public LiveRoomItemDataBean() {
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getLogourl() {
        return logourl;
    }

    public void setLogourl(String logourl) {
        this.logourl = logourl;
    }

    public String getPlay_url() {
        return play_url;
    }

    public void setPlay_url(String play_url) {
        this.play_url = play_url;
    }
}
