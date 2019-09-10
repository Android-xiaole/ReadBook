package com.jj.comics.util;

import android.app.Activity;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.jj.base.BaseApplication;
import com.jj.base.utils.RouterMap;
import com.jj.comics.common.constants.RequestCode;
import com.jj.comics.data.db.DaoHelper;
import com.jj.comics.data.model.UserInfo;

public class LoginHelper {
    public static UserInfo onLineUser;
    private static DaoHelper<UserInfo> mHelper;

    static {
        mHelper = new DaoHelper<>();
        onLineUser = mHelper.getLoginUser();
    }

    /**
     * 更新用户
     *
     * @param userInfo 用户信息
     * @return
     */
    public static void updateUser(UserInfo userInfo) {
        synchronized (LoginHelper.class) {
            mHelper.updateUser(userInfo);
            onLineUser = userInfo;
        }
    }

    /**
     * 登出所有用户
     */
    public static void logOffAllUser() {
        synchronized (LoginHelper.class) {
            mHelper.logOffAllUser();
            onLineUser = null;
        }
    }

    public static UserInfo getOnLineUser() {
//        if (onLineUser == null) {
//            synchronized (LoginHelper.class) {
//                if (onLineUser == null)
//                    onLineUser = mHelper.getLoginUser();
//            }
//        }
        onLineUser = new UserInfo();//先设置默认登录状态，方便调试
        return onLineUser;
    }

    public static boolean interruptLogin(Activity activity, Bundle bundle) {
        boolean isLogin = getOnLineUser() != null;
        if (!isLogin) {
            Postcard postcard = ARouter.getInstance().build(RouterMap.COMIC_LOGIN_ACTIVITY);
            if (bundle != null) postcard.with(bundle);
            postcard.navigation(activity, RequestCode.LOGIN_REQUEST_CODE);
        }
        return isLogin;
    }
}
