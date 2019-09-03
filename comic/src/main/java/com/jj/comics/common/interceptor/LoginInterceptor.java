package com.jj.comics.common.interceptor;

import android.app.Activity;
import android.content.Context;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Interceptor;
import com.alibaba.android.arouter.facade.callback.InterceptorCallback;
import com.alibaba.android.arouter.facade.template.IInterceptor;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.comics.util.LoginHelper;

/**
 * FBI WARNING ! MAGIC ! DO NOT TOUGH !
 * Created by WangZQ on 2018/8/29 - 14:59.
 */
@Interceptor(priority = 1)
public class LoginInterceptor implements IInterceptor {
    private Context mContext;

    @Override
    public void process(Postcard postcard, InterceptorCallback callback) {
        if (postcard.getExtra() == RouterMap.NEED_LOGIN && LoginHelper.getOnLineUser() == null) {
            callback.onInterrupt(new Exception("拦截到需要登录"));
            if (mContext instanceof BaseActivity)
                LoginHelper.interruptLogin((Activity) mContext, postcard.getExtras());
        } else {
            callback.onContinue(postcard);
        }
    }

    @Override
    public void init(Context context) {
        mContext = context;
    }
}
