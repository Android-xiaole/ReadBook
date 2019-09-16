package com.jj.comics.ui.mine.rebate.cashout;

import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.widget.UserItemView;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.COMIC_CASHOUTWAY_ACTIVITY)
public class CashOutWayActivity extends BaseActivity<CashOutWayPresenter> implements CashOutWayContract.ICashOutWayView{
    @BindView(R2.id.btn_add_ali)
    UserItemView mBtnAli;
    @BindView(R2.id.btn_add_union)
    UserItemView mBtnUnion;
    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @OnClick({R2.id.btn_add_union,R2.id.btn_add_ali})
    void onClick(View view) {
        if (view.getId() == R.id.btn_add_ali) {
            ARouter.getInstance().build(RouterMap.COMIC_ADDCASHOUTWAY_ALI_ACTIVITY).navigation();
        }else if (view.getId() == R.id.btn_add_union) {
            ARouter.getInstance().build(RouterMap.COMIC_ADDCASHOUTWAY_UNION_ACTIVITY).navigation();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.comic_activity_cash_out_way;
    }

    @Override
    public CashOutWayPresenter setPresenter() {
        return new CashOutWayPresenter();
    }

    @Override
    public void onGetCashOutWayStatus(boolean ali, boolean union) {
        hideProgress();
        if (ali) {
            mBtnAli.setTitle("支付宝已添加，点击修改");
        }
        if (union) {
            mBtnUnion.setTitle("银行卡已添加，点击修改");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        showProgress();
        getP().getWayStatus();
    }
}
