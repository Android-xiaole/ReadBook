package com.jj.comics.util;

import com.jj.base.BaseApplication;
import com.jj.base.utils.SharedPref;
import com.jj.comics.common.constants.Constants;

/**
 * 共享参数管理类
 */
public class SharedPreManger {

    private static SharedPreManger instance;
    private SharedPref sharedPref;

    public static SharedPreManger getInstance(){
        if (instance == null){
            synchronized (SharedPreManger.class){
                instance = new SharedPreManger();
            }
        }
        return instance;
    }

    private SharedPreManger(){
        sharedPref = SharedPref.getInstance(BaseApplication.getApplication());
    }

    /**
     * 保存登录的token
     */
    public void saveToken(String token){
        sharedPref.putString(Constants.SharedPrefKey.TOKEN,token);
    }

    /**
     * 获取登录的token
     */
    public String getToken(){
        return sharedPref.getString(Constants.SharedPrefKey.TOKEN,"");
    }

    /**
     * 删除token
     */
    public void removeToken(){
        sharedPref.remove(Constants.SharedPrefKey.TOKEN);
    }

    /**
     * 保存邀请码
     */
    public void saveInvitecode(String invite_code){
        sharedPref.putString(Constants.SharedPrefKey.INVITE_CODE,invite_code);
    }

    /**
     * 获取邀请码
     */
    public String getInvitecode(){
        return sharedPref.getString(Constants.SharedPrefKey.INVITE_CODE,"");
    }

    /**
     * 保存自动购买状态
     */
    public void saveAutoBuyStatus(boolean isAutoBuy){
        sharedPref.putBoolean(Constants.SharedPrefKey.AUTO_BUY,isAutoBuy);
    }

    /**
     * 获取自动购买状态
     */
    public boolean getAutoBuyStatus(){
        return sharedPref.getBoolean(Constants.SharedPrefKey.AUTO_BUY,false);
    }
    /**
     * 保存自动购买状态
     */
    public void saveReceiveStatus(boolean isAutoReceive){
        sharedPref.putBoolean(Constants.SharedPrefKey.RECEIVE,isAutoReceive);
    }

    /**
     * 获取自动购买状态
     */
    public boolean getReceiveStatus(){
        return sharedPref.getBoolean(Constants.SharedPrefKey.RECEIVE,true);
    }
}
