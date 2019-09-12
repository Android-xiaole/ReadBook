package com.jj.comics.ui.mine.rebate;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.android.material.tabs.TabLayout;
import com.jj.base.ui.BaseActivity;
import com.jj.base.ui.BaseFragment;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.ViewPagerAdapter;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.data.model.PayInfo;
import com.jj.comics.ui.mine.rebate.detail.CashOutFragment;
import com.jj.comics.ui.mine.rebate.detail.RebateFragment;
import com.jj.comics.widget.comic.toolbar.ComicToolBar;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.COMIC_MY_REBATE_ACTIVITY)
public class MyRebateActivity extends BaseActivity<MyRebatePrenenter> implements MyRebateContract.IMyRebateView {
    @BindView(R2.id.tv_rebate_money)
    TextView mTvMoney;
    @BindView(R2.id.tv_rebate_recenly)
    TextView mTvRecently;
    @BindView(R2.id.tv_rebate_total)
    TextView mTvTotalRebate;
    @BindView(R2.id.tv_rebate_total_out)
    TextView mTvTotalOut;
    @BindView(R2.id.vp_rebate)
    ViewPager mViewPager;
    @BindView(R2.id.btn_rebate_cash_out)
    ImageView mBtnCashOut;
    @BindView(R2.id.tb_rebate_detail)
    TabLayout mTabLayout;
    @BindView(R2.id.toolBar)
    ComicToolBar mToolBar;
    private ViewPagerAdapter mViewPagerAdapter;

    @Override
    protected void initData(Bundle savedInstanceState) {
        Serializable serializable = getIntent().getSerializableExtra(Constants.IntentKey.PAY_INFO);
        PayInfo payInfo = null;
        if (serializable instanceof PayInfo)
            payInfo = (PayInfo) serializable;

        if (payInfo != null) {
            String can_drawout_amount = payInfo.getCan_drawout_amount() + "";
            if (!can_drawout_amount.contains(".")) can_drawout_amount = can_drawout_amount + ".00";
            SpannableString spannableString = new SpannableString(can_drawout_amount);
            spannableString.setSpan(new RelativeSizeSpan(0.85f),can_drawout_amount.indexOf("."),
                    can_drawout_amount.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mTvMoney.setText(spannableString);
            mTvRecently.setText(payInfo.getNewest_rebate() + "");
            mTvTotalRebate.setText(payInfo.getTotal_rebate_amount() + "");
            mTvTotalOut.setText(payInfo.getTotal_drawcash_amount() + "");
        }

        ArrayList<BaseFragment> fragments = new ArrayList<>();
        fragments.add(new RebateFragment());
        fragments.add(new CashOutFragment());
        String[] titles = {"返利","提现"};
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragments,
                titles);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(mViewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mToolBar.addChildClickListener(new ComicToolBar.OnComicToolBarListener() {
            @Override
            public void onComicToolBarLeftIconClick(View childView) {

            }

            @Override
            public void onComicToolBarRightIconClick(View childView) {

            }

            @Override
            public void onComicToolBarRightTextClick(View childView) {
                ARouter.getInstance().build(RouterMap.COMIC_CASHOUTWAY_ACTIVITY).navigation();
            }
        });

    }

    @OnClick({R2.id.btn_rebate_cash_out})
    void onClick(View view) {
       if (view.getId() == R.id.btn_rebate_cash_out) {
            ARouter.getInstance().build(RouterMap.COMIC_DOCASHOUT_ACTIVITY).navigation();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.comic_activity_my_rebate;
    }

    @Override
    public MyRebatePrenenter setPresenter() {
        return new MyRebatePrenenter();
    }
}