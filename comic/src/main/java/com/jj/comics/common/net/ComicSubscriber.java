package com.jj.comics.common.net;


import android.app.Activity;

import com.alibaba.android.arouter.launcher.ARouter;
import com.jj.base.BaseApplication;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.base.utils.RouterMap;
import com.jj.comics.common.constants.RequestCode;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.util.SharedPreManger;
import com.jj.comics.util.TencentHelper;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.umeng.analytics.MobclickAgent;

/**
 * 这里面处理了服务器返回403，也就是登录过期的时候，自动弹出登录提示
 * @param <T>
 */
public abstract class ComicSubscriber<T> extends ApiSubscriber2<T> {

    @Override
    public void onError(Throwable e) {
        if (e instanceof NetError){
            if (((NetError) e).getType() == NetError.AuthError){
                SharedPreManger.getInstance().removeToken();
                LoginHelper.logOffAllUser();
                MobclickAgent.onProfileSignOff();
                Activity topActivity = BaseApplication.getApplication().getTopActivity();
                if (topActivity!=null){
                    TencentHelper.getTencent().logout(topActivity);
                    AccessTokenKeeper.clear(topActivity);
                    createLoginDialog(topActivity);
                }
                onEnd();
                return;
            }
        }
        super.onError(e);
    }

//    private LoginNotifyDialog loginNotifyDialog;
    public void createLoginDialog(final Activity activity) {
        ARouter.getInstance().build(RouterMap.COMIC_LOGIN_ACTIVITY)
                .navigation(activity, RequestCode.LOGIN_REQUEST_CODE);
        //取消登录页面之前的弹窗提醒
//        if (loginNotifyDialog == null) loginNotifyDialog = new LoginNotifyDialog();
//        loginNotifyDialog.show(((FragmentActivity) activity).getSupportFragmentManager(), new DialogUtilForComic.OnDialogClick() {
//            @Override
//            public void onConfirm() {
//                loginNotifyDialog.dismiss();
//                ARouter.getInstance().build(RouterMap.COMIC_LOGIN_ACTIVITY)
//                        .navigation(activity, RequestCode.LOGIN_REQUEST_CODE);
//            }
//
//            @Override
//            public void onRefused() {
//
//            }
//
//
//        });
    }
}
