package com.jj.comics.ui.mine.rebate.cashout;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;

@Route(path = RouterMap.COMIC_ADDCASHOUTWAY_ALI_ACTIVITY)
public class AddCashOutWayAliActivity extends BaseActivity<AddCashOutWayAliPresenter> implements AddCashOutWayAliContract.IAddCashOutWayAliView {
    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    public AddCashOutWayAliPresenter setPresenter() {
        return new AddCashOutWayAliPresenter();
    }
}
