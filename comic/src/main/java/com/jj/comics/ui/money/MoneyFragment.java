package com.jj.comics.ui.money;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.request.RequestOptions;
import com.jj.base.imageloader.ILFactory;
import com.jj.base.log.LogUtil;
import com.jj.base.ui.BaseCommonFragment;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.money.ShareRecommendAdapter;
import com.jj.comics.data.model.PayInfo;
import com.jj.comics.data.model.ShareRecommendResponse;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.util.LoginHelper;

import java.util.List;

import butterknife.BindView;


@Route(path = RouterMap.COMIC_MONEY_FRAGMENT)
public class MoneyFragment extends BaseCommonFragment<MoneyPresenter> implements MoneyContract.IMoneyView{
//    @BindView(R2.id.vf_apprentice_now)
//    AdapterViewFlipper mViewFlipper;
//
//    private RecentlyApprenticeAdapter mApprenticeAdapter;

    @BindView(R2.id.iv_money_detail)
    ImageView mIvDetail;
    @BindView(R2.id.iv_money_user_info_avatar)
    ImageView mIvAvatar;
    @BindView(R2.id.tv_money_user_info_name)
    TextView mTvName;
    @BindView(R2.id.tv_money_user_info_total_money)
    TextView mTvMoney;
    @BindView(R2.id.tv_money_user_info_total_apprentice)
    TextView mTvApprentice;
    @BindView(R2.id.btn_invite)
    Button mBtnInvite;
    @BindView(R2.id.rv_money_recommend)
    RecyclerView mRecyclerView;
    @BindView(R2.id.cl_user_info)
    ConstraintLayout mClUserInfo;
    private ShareRecommendAdapter mAdapter;

    @Override
    public void initData(Bundle savedInstanceState) {

        //滚动显示提现记录，本期不显示
//        PropertyValuesHolder p1 = PropertyValuesHolder.ofFloat("translationX",900,0);
//        PropertyValuesHolder p2 = PropertyValuesHolder.ofFloat("alpha",0.5f,1);
//        ObjectAnimator anim_in = ObjectAnimator.ofPropertyValuesHolder(mViewFlipper,p1,p2).setDuration(2000);
//        anim_in.setInterpolator(new AccelerateDecelerateInterpolator());
//        mViewFlipper.setInAnimation(anim_in);
//        PropertyValuesHolder p3 = PropertyValuesHolder.ofFloat("translationX",0,-900);
//        PropertyValuesHolder p4 = PropertyValuesHolder.ofFloat("alpha",1,0.5f);
//        ObjectAnimator anim_out = ObjectAnimator.ofPropertyValuesHolder(mViewFlipper,p3,p4).setDuration(2000);
//        anim_out.setInterpolator(new AccelerateDecelerateInterpolator());
//        mViewFlipper.setOutAnimation(anim_out);
//
//        //实时打赏滚动
//        mApprenticeAdapter = new RecentlyApprenticeAdapter(null);
//        mViewFlipper.setAdapter(mApprenticeAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ShareRecommendAdapter(R.layout.comic_item_share_recommend);
        mAdapter.bindToRecyclerView(mRecyclerView,false);
    }

    private void updateInfo() {
        UserInfo userInfo = LoginHelper.getOnLineUser();
        if (userInfo != null) {
            mClUserInfo.setVisibility(View.VISIBLE);

            RequestOptions options = new RequestOptions();
            options.circleCrop();
            ILFactory.getLoader().loadNet(mIvAvatar,userInfo.getAvatar(),
                    options);
            mTvName.setText(userInfo.getNickname());

        }else {
            mClUserInfo.setVisibility(View.GONE);
        }

        RequestOptions options = new RequestOptions();
        options.encodeQuality(100);
        ILFactory.getLoader().loadNet(mIvDetail,"http://fanli.jjmh668.cn/prd/apprentice.png",
                options);

        getP().getUserPayInfo();

        getP().getShareRecommend();
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_activity_money;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        LogUtil.d("HIDDEN：" + hidden);
        super.onHiddenChanged(hidden);
        if (!hidden) {
            updateInfo();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateInfo();
    }

    @Override
    public MoneyPresenter setPresenter() {
        return new MoneyPresenter();
    }

    @Override
    public void onGetUserPayInfo(PayInfo payInfo) {
        if (LoginHelper.getOnLineUser() != null) {
            mTvMoney.setText(payInfo.getTotal_rebate_amount() + "");
            mTvApprentice.setText(payInfo.getDisciple_num() + "");
        }
    }

    @Override
    public void onGetShareRecommend(List<ShareRecommendResponse.DataBean> list) {
        mAdapter.setNewData(list);
    }
}
