package com.jj.comics.ui.mine.rebate.cashout;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;

@Route(path = RouterMap.COMIC_CASHOUTWAY_ACTIVITY)
public class CashOutWayActivity extends BaseActivity<CashOutWayPresenter> implements CashOutWayContract.ICashOutWayView{
    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    public CashOutWayPresenter setPresenter() {
        return new CashOutWayPresenter();
    }
}
