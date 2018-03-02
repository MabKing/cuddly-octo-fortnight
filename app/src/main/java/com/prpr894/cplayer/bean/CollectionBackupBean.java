package com.prpr894.cplayer.bean;

import java.util.List;

public class CollectionBackupBean {
    private String backupDate;
    private List<LiveRoomListBean> data;

    public String getBackupDate() {
        return backupDate;
    }

    public void setBackupDate(String backupDate) {
        this.backupDate = backupDate;
    }

    public List<LiveRoomListBean> getData() {
        return data;
    }

    public void setData(List<LiveRoomListBean> data) {
        this.data = data;
    }
}
