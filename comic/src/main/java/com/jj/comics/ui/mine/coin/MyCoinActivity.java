package com.jj.comics.ui.mine.coin;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.ui.mine.pay.PayActivity;
import com.jj.comics.widget.UserItemView;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.COMIC_MYCOIN_ACTIVITY)
public class MyCoinActivity extends BaseActivity<MyCoinPresenter> implements MyCoinContract.IMyCoinView{

    @BindView(R2.id.tv_my_coin)
    TextView mTvCoin;
    @BindView(R2.id.btn_recharge)
    TextView mBtnRecharge;
    @BindView(R2.id.comic_coin_recharge_history)
    UserItemView mItemRechargeHistory;
    @BindView(R2.id.comic_coin_consume_history)
    UserItemView mItemConsumeHistory;
    @Override
    protected void initData(Bundle savedInstanceState) {
        long coin = getIntent().getIntExtra(Constants.IntentKey.COIN, 0);
        String coinMsg = "暂无余额";
        if (coin > 0)  coinMsg = coin + "";
        mTvCoin.setText(coinMsg);

    }

    @OnClick({R2.id.btn_recharge,R2.id.comic_coin_recharge_history,R2.id.comic_coin_consume_history})
    void onClick(View view) {
        if (view.getId() == R.id.btn_recharge) {
            PayActivity.toPay(this,"1",0);
        }else if (view.getId() == R.id.comic_coin_consume_history) {
            ARouter.getInstance().build(RouterMap.COMIC_CONSUME_RECORD_ACTIVITY).navigation();
        }else if (view.getId() == R.id.comic_coin_recharge_history) {
            ARouter.getInstance().build(RouterMap.COMIC_RECHARGE_RECORD_ACTIVITY).navigation();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.comic_activity_my_coin;
    }

    @Override
    public MyCoinPresenter setPresenter() {
        return new MyCoinPresenter();
    }
}
