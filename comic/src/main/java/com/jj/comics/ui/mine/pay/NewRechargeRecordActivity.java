package com.jj.comics.ui.mine.pay;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.android.material.tabs.TabLayout;
import com.jj.base.ui.BaseActivity;
import com.jj.base.ui.BaseFragment;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.SharedPref;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.ViewPagerAdapter;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.ui.dialog.NotifyWithCheckBoxDialog;
import com.jj.comics.ui.dialog.DialogUtilForComic;

import java.util.ArrayList;
import java.util.List;

import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;

/**
 * 充值记录和消费记录
 */
@Route(path = RouterMap.COMIC_NEW_RECHARGE_RECORD_ACTIVITY)
public class NewRechargeRecordActivity extends BaseActivity<NewRechargeRecordPresenter> implements NewRechargeContract.INewRechargeView{
    //顶部切换导航栏
    @BindView(R2.id.recharge_tab)
    TabLayout mTab;
    //页面切换
    @BindView(R2.id.recharge_pager)
    ViewPager mPager;

    private ViewPagerAdapter mAdapter;
    private List<BaseFragment> mList;

    @Override
    public void initData(Bundle savedInstanceState) {
        //顶部状态栏
//        StatusBarUtil.setColor(this, getResources().getColor(R.color.comic_yellow_ffd850), 77);
        mList = new ArrayList<>();
        //添加充值记录
        mList.add(new RechargeRecordFragment());
        //消费记录
        mList.add(new PayRecordFragment());
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager(), mList, new String[]{getString(R.string.comic_recharge_record_text), getString(R.string.comic_pay_record_text)});
        mPager.setAdapter(mAdapter);
        mTab.setupWithViewPager(mPager);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                //设置消费记录查看提醒
                if (i == 1 && SharedPref.getInstance(getApplicationContext()).getBoolean(Constants.SharedPrefKey.REMIND_PAY_RECORD, true)) {
                    showRemind();
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    /**
     * 加载布局
     *
     * @return
     */
    @Override
    public int getLayoutId() {
        return R.layout.comic_activity_new_recharge_record;
    }

    @Override
    public NewRechargeRecordPresenter setPresenter() {
        return null;
    }

    private NotifyWithCheckBoxDialog notifyWithCheckBoxDialog;
    /**
     * 消费记录提醒弹窗
     */
    private void showRemind() {
        if (notifyWithCheckBoxDialog == null)notifyWithCheckBoxDialog = new NotifyWithCheckBoxDialog();
        notifyWithCheckBoxDialog.show(getSupportFragmentManager(), getString(R.string.comic_pay_record_remind_title), getString(R.string.comic_pay_record_remind_msg), new DialogUtilForComic.OnDialogClickWithSelect() {
            @Override
            public void onConfirm() {

            }

            @Override
            public void onRefused() {
                SharedPref.getInstance(getApplicationContext()).putBoolean(Constants.SharedPrefKey.REMIND_PAY_RECORD, true);
            }

            @Override
            public void onSelected(boolean noMore) {
                SharedPref.getInstance(getApplicationContext()).putBoolean(Constants.SharedPrefKey.REMIND_PAY_RECORD, !noMore);
            }
        });
//        DialogUtil.showRemindDialogWithNoMore(NewRechargeRecordActivity.this, getString(R.string.comic_pay_record_remind_title), getString(R.string.comic_pay_record_remind_msg), new DialogUtil.OnDialogClickWithNoMore() {
//            @Override
//            public void onConfirm() {
//                SharedPref.getInstance(getApplicationContext()).putBoolean(Constants.SharedPrefKey.REMIND_PAY_RECORD, false);
//            }
//
//            @Override
//            public void onRefused() {
//
//            }
//
//            @Override
//            public void onNoMore(boolean noMore) {
//
//            }
//        });
    }

    @Override
    public boolean hasFragment() {
        return true;
    }
}
