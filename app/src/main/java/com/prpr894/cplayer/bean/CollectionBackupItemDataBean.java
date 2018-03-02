package com.prpr894.cplayer.bean;

import java.util.List;

public class CollectionBackupItemDataBean {
    private String backupDate;
    private List<LiveRoomItemDataBean> data;

    public List<LiveRoomItemDataBean> getData() {
        return data;
    }

    public void setData(List<LiveRoomItemDataBean> data) {
        this.data = data;
    }

    public String getBackupDate() {
        return backupDate;
    }

    public void setBackupDate(String backupDate) {
        this.backupDate = backupDate;
    }


}
