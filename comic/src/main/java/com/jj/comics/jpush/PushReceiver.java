package com.jj.comics.jpush;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.jj.base.log.LogUtil;
import com.jj.base.utils.RouterMap;
import com.jj.comics.common.constants.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;

public class PushReceiver extends JPushMessageReceiver {

    @Override
    public void onMessage(Context context, CustomMessage customMessage) {
        super.onMessage(context, customMessage);
        LogUtil.e(customMessage.toString());
    }

    @Override
    public void onNotifyMessageArrived(Context context, NotificationMessage notificationMessage) {
        super.onNotifyMessageArrived(context, notificationMessage);
    }

    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage notificationMessage) {
        super.onNotifyMessageOpened(context, notificationMessage);
        if (notificationMessage.notificationExtras!=null){
            try {
                JSONObject object = new JSONObject(notificationMessage.notificationExtras);
                String bookId = object.optString("id");
                if (!TextUtils.isEmpty(bookId)) {
                    if (!Constants.ISLIVE_MAIN){
                        //首页不存活就跳转到闪屏页
                        ARouter.getInstance().build(RouterMap.COMIC_SPLASH_ACTIVITY)
                                .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .withBoolean(Constants.IntentKey.IS_JPUSH,true)
                                .withString(Constants.IntentKey.ID,bookId)
                                .withString("from", "极光推送")
                                .navigation(context);
                    }else{
                        //首页存活就直接跳转到详情页
                        ARouter.getInstance().build(RouterMap.COMIC_DETAIL_ACTIVITY)
                                .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .withBoolean(Constants.IntentKey.IS_JPUSH,true)
                                .withString(Constants.IntentKey.ID,bookId)
                                .withString("from", "极光推送")
                                .navigation(context);
                    }
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                LogUtil.e("onNotifyMessageOpened:notificationExtras消息转换json失败");
            }
            ARouter.getInstance().build(RouterMap.COMIC_SPLASH_ACTIVITY)
                    .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .navigation(context);
        }
    }
}
