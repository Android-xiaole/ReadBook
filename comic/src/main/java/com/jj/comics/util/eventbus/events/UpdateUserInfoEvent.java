package com.jj.comics.util.eventbus.events;

import com.jj.comics.data.model.UserInfo;

/**
 * 更新用户数据的通知
 * 如果userInfo==null就调用接口刷新用户信息
 * 如果userInfo!=null就用model刷新
 */
public class UpdateUserInfoEvent {

    private UserInfo userInfo;

    public UpdateUserInfoEvent(){

    }

    public UpdateUserInfoEvent(UserInfo userInfo){
        this.userInfo = userInfo;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
