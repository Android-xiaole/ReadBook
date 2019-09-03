package com.jj.comics.ui.detail;

import android.app.Activity;

import com.alibaba.android.arouter.launcher.ARouter;
import com.jj.base.utils.RouterMap;

public class DetailActivityHelper {

    /**
     * 进入详情页
     *
     * @param activity
     * @param id       漫画id
     * @param from     来源
     */
    public static void toDetail(Activity activity, long id, String from) {
        ARouter.getInstance().build(RouterMap.COMIC_DETAIL_ACTIVITY)
                .withSerializable("id", id)
                .withString("from", from)
                .navigation(activity);
    }


    /**
     * 进入详情页
     *
     * @param activity
     * @param id       漫画id
     * @param from     来源
     */
    public static void toDetail(Activity activity, String id, String from) {
        ARouter.getInstance().build(RouterMap.COMIC_DETAIL_ACTIVITY)
                .withString("id", id)
                .withString("from", from)
                .navigation(activity);
    }
}
