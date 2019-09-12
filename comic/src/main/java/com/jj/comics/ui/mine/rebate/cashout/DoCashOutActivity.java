package com.jj.comics.ui.mine.rebate.cashout;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;

@Route(path = RouterMap.COMIC_DOCASHOUT_ACTIVITY)
public class DoCashOutActivity extends BaseActivity<DoCashOutPresenter> implements DoCashOutContract.IDoCashOutView {
    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    public DoCashOutPresenter setPresenter() {
        return new DoCashOutPresenter();
    }
}
