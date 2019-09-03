package com.jj.comics.util;

import com.jj.base.BaseApplication;
import com.jj.comics.common.constants.Constants;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;

public class TencentHelper {
    private static Tencent mTencent;
    private static IWXAPI mWxApi;

    public static Tencent getTencent() {
        if (mTencent == null)
            mTencent = Tencent.createInstance(Constants.QQ_APPID(), BaseApplication.getApplication());
        return mTencent;
    }

    public static IWXAPI getWxApi(String appid) {
        mWxApi = null;
        mWxApi = WXAPIFactory.createWXAPI(BaseApplication.getApplication(), appid, !Constants.DEBUG);
        mWxApi.registerApp(appid);
        return mWxApi;
    }

}
