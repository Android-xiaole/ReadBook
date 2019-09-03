package com.jj.comics.data.model;


import androidx.annotation.NonNull;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class SignInDay implements Comparable<SignInDay>, MultiItemEntity {
    private String title;
    private boolean status;
    private String award;

    public SignInDay() {
    }

    public SignInDay(String title, boolean status, String award) {
        this.title = title;
        this.status = status;
        this.award = award;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getAward() {
        return award;
    }

    public void setAward(String award) {
        this.award = award;
    }

    @Override
    public int getItemType() {
        return 0;
    }

    @Override
    public int compareTo(@NonNull SignInDay o) {
        return 0;
    }
}
