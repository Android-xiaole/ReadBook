package com.jj.comics.data.model;


import androidx.annotation.NonNull;

import com.jj.base.mvp.IModel;
import com.jj.base.net.NetError;

import java.util.List;

public class SignInDayModel implements Comparable<SignInDay>, IModel {
    private int currSigninAward;
    private List<SignInDay> signinStatusList;
    private String token;
    private String expenseToken;
    private int code;
    private String msg;

    public SignInDayModel() {
    }

    public SignInDayModel(int currSigninAward, List<SignInDay> signinStatusList, String token, String expenseToken, int code, String msg) {
        this.currSigninAward = currSigninAward;
        this.signinStatusList = signinStatusList;
        this.token = token;
        this.expenseToken = expenseToken;
        this.code = code;
        this.msg = msg;
    }

    public int getCurrSigninAward() {
        return currSigninAward;
    }

    public void setCurrSigninAward(int currSigninAward) {
        this.currSigninAward = currSigninAward;
    }

    public List<SignInDay> getSigninStatusList() {
        return signinStatusList;
    }

    public void setSigninStatusList(List<SignInDay> signinStatusList) {
        this.signinStatusList = signinStatusList;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getExpenseToken() {
        return expenseToken;
    }

    public void setExpenseToken(String expenseToken) {
        this.expenseToken = expenseToken;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public int compareTo(@NonNull SignInDay o) {
        return 0;
    }

    @Override
    public NetError error() {
        return null;
    }
}
