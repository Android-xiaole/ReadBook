package com.jj.comics.data.model;

import android.text.TextUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class SearchModel {
    @Id
    private String key;
    private long time;

    @Generated(hash = 1170587659)
    public SearchModel(String key, long time) {
        this.key = key;
        this.time = time;
    }

    @Generated(hash = 506184495)
    public SearchModel() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SearchModel && TextUtils.equals(((SearchModel) obj).key, key))
            return true;
        return super.equals(obj);
    }
}
