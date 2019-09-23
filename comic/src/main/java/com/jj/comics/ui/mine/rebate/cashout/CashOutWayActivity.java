package com.jj.comics.ui.mine.rebate.cashout;

import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.data.model.AlipayBean;
import com.jj.comics.data.model.BankBean;
import com.jj.comics.data.model.CashOutWayResponse;
import com.jj.comics.widget.UserItemView;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.COMIC_CASHOUTWAY_ACTIVITY)
public class CashOutWayActivity extends BaseActivity<CashOutWayPresenter> implements CashOutWayContract.ICashOutWayView{
    @BindView(R2.id.btn_add_ali)
    UserItemView mBtnAli;
    @BindView(R2.id.btn_add_union)
    UserItemView mBtnUnion;
    private AlipayBean mAlipay;
    private BankBean mBank;

    @Override
    protected void initData(Bundle savedInstanceState) {
    }

    @OnClick({R2.id.btn_add_union,R2.id.btn_add_ali})
    void onClick(View view) {
        if (view.getId() == R.id.btn_add_ali) {
            ARouter.getInstance().build(RouterMap.COMIC_ADDCASHOUTWAY_ALI_ACTIVITY)
                    .withSerializable(Constants.IntentKey.CASH_OUT_ALI,mAlipay).navigation();
        }else if (view.getId() == R.id.btn_add_union) {
            ARouter.getInstance().build(RouterMap.COMIC_ADDCASHOUTWAY_UNION_ACTIVITY)
                    .withSerializable(Constants.IntentKey.CASH_OUT_BANK,mBank).navigation();
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
    public void onGetCashOutWayStatus(CashOutWayResponse response) {
        hideProgress();
        if (response != null && response.getData() != null) {
            CashOutWayResponse.DataBean data = response.getData();
            mAlipay = data.getAlipay();
            mBank = data.getBank();
            if (mAlipay != null && mAlipay.isStatus()) {
                mBtnAli.setTitle("支付宝已添加，点击修改");
            }

            if (mBank != null && mBank.isStatus()) {
                mBtnUnion.setTitle("银行卡已添加，点击修改");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getP().getWayStatus();
    }
}
