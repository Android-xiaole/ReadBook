package com.jj.comics.ui.mine.coin;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
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
        long coin = getIntent().getLongExtra("coin", 0);
        String coinMsg = "暂无余额";
        if (coin > 0)  coinMsg = coin + "";
        mTvCoin.setText(coinMsg);

    }

    @OnClick({R2.id.btn_recharge,R2.id.comic_coin_recharge_history,R2.id.comic_coin_consume_history})
    void onClick(View view) {
        if (view.getId() == R.id.btn_recharge) {
            showToastShort("跳转充值中心");
        }else if (view.getId() == R.id.comic_coin_recharge_history) {
            showToastShort("跳转充值历史");
        }else if (view.getId() == R.id.comic_coin_consume_history) {
            showToastShort("跳转消费历史");
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
