package com.jj.novelpro.arouter;

import android.content.Context;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.service.DegradeService;
import com.alibaba.android.arouter.launcher.ARouter;
import com.jj.base.utils.RouterMap;

@Route(path = "/a/*")
public class DegradeServiceImpl implements DegradeService {
    @Override
    public void onLost(Context context, Postcard postcard) {
        ARouter.getInstance().build(RouterMap.COMIC_SPLASH_ACTIVITY).navigation();
    }

    @Override
    public void init(Context context) {
    }
}
