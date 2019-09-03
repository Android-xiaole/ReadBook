package com.jj.base;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.jj.base.ui.BaseFragment;

public abstract class CusNavigationCallback extends NavCallback {
    public void onGetFragment(int index,BaseFragment fragment){}

    public void onFound(Postcard postcard) {
        super.onFound(postcard);
    }
}
