package com.jj.comics.ui.mine.rebate.cashout;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;

@Route(path = RouterMap.COMIC_ADDCASHOUTWAY_UNION_ACTIVITY)
public class AddCashOutWayUnionActivity extends BaseActivity<AddCashOutWayUnionPresenter> implements AddCashOutWayUnionContract.IAddCashOutWayUnionView {
    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    public AddCashOutWayUnionPresenter setPresenter() {
        return new AddCashOutWayUnionPresenter();
    }
}
