package com.jj.comics.ui.mine;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.jj.base.imageloader.ILFactory;
import com.jj.base.ui.BaseCommonFragment;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.data.model.PayInfo;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.ui.mine.pay.PayActivity;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.util.eventbus.events.LoginEvent;
import com.jj.comics.util.eventbus.events.LogoutEvent;
import com.jj.comics.util.eventbus.events.UpdateUserInfoEvent;
import com.jj.comics.widget.comic.MineItemView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.utils.ScreenUtils;

/**
 * 我的页面
 */
@Route(path = RouterMap.COMIC_MINE_FRAGMENT)
public class MineFragment extends BaseCommonFragment<MinePresenter> implements MineContract.IMineView {
    @BindView(R2.id.comic_mine_buy)
    MineItemView mineItemView_buy;
    @BindView(R2.id.comic_mine_history)
    MineItemView mineItemView_history;
    @BindView(R2.id.comic_mine_notification)
    MineItemView mineItemView_notification;
    @BindView(R2.id.comic_mine_help)
    MineItemView mineItemView_help;
    @BindView(R2.id.mine_head_img)
    ImageView headImg;
    @BindView(R2.id.mine_nickname)
    TextView mNickname;
    @BindView(R2.id.mine_rebate)
    TextView mRebate;
    @BindView(R2.id.mine_coins)
    TextView mCoins;
    @BindView(R2.id.mine_apprentice)
    TextView mApprentice;
    @BindView(R2.id.leave_name)
    TextView mLeaveName;
    @BindView(R2.id.rootView)
    RelativeLayout rootView;

    private PayInfo mPayInfo;

    @Override
    public void initData(Bundle savedInstanceState) {
        //设置toolbar距离上端的高度
        int statusBarHeight = ScreenUtils.getStatusBarHeight();
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) rootView.getLayoutParams();
        lp.topMargin = statusBarHeight;
        rootView.setLayoutParams(lp);
        //上传访问我的界面  key为accessUserCenter
        if (LoginHelper.interruptLogin(getActivity(), null)) {
            getP().getUserInfo();
            getP().getUserPayInfo();
        }
    }

    @Override
    public void onVisiable(boolean visiable) {
        super.onVisiable(visiable);
        if (visiable && LoginHelper.getOnLineUser() != null) {
            getP().getUserInfo();
            getP().getUserPayInfo();
        }
    }


    @Override
    public int getLayoutId() {
        return R.layout.comic_fragment_mine;
    }

    @Override
    public MinePresenter setPresenter() {
        return new MinePresenter();
    }

    @Override
    public void onGetUserInfo(UserInfo userInfo) {
        //设置头像
        if (TextUtils.isEmpty(userInfo.getAvatar())) {
            ILFactory.getLoader().loadResource(headImg, R.drawable.bg_avatar,
                    new RequestOptions().transforms(new CenterCrop(), new CircleCrop()));
        } else {
            ILFactory.getLoader().loadNet(headImg, userInfo.getAvatar(),
                    new RequestOptions().transforms(new CenterCrop(), new CircleCrop()).error(R.drawable.img_loading)
                            .placeholder(R.drawable.img_loading));
        }
        String nickName = userInfo.getNickname();
        //设置昵称
        mNickname.setText(nickName);
        mLeaveName.setText(userInfo.getClass_name() == null ? "" : userInfo.getClass_name());
        if (userInfo.getIs_vip() == 1) {
            headImg.setBackgroundResource(R.drawable.header_bg);
        } else {
            headImg.setBackgroundResource(R.drawable.header_cicle);
        }
    }

    @Override
    public void onGetUserPayInfo(PayInfo payInfo) {
        mPayInfo = payInfo;
        mCoins.setText(payInfo.getTotal_egold() + "");
        mRebate.setText(payInfo.getCan_drawout_amount() + "");
        mApprentice.setText(payInfo.getDisciple_num() + "");
    }

    @OnClick({R2.id.comic_mine_coin_pay, R2.id.comic_mine_vip_pay, R2.id.mine_nickname, R2.id.mine_head_img, R2.id.comic_mine_buy, R2.id.comic_mine_history,
            R2.id.comic_mine_notification, R2.id.comic_mine_help,
            R2.id.rl_edit_user_info,
            R2.id.btn_my_rebate, R2.id.btn_my_coin, R2.id.btn_my_apprentice})
    void onClick(View view) {
        if (view.getId() == R.id.mine_head_img || view.getId() == R.id.mine_nickname) {
            if (LoginHelper.getOnLineUser() == null) {
                ARouter.getInstance().build(RouterMap.COMIC_LOGIN_ACTIVITY).navigation(getActivity());
            }
        } else if (view.getId() == R.id.comic_mine_buy) {
            ARouter.getInstance().build(RouterMap.COMIC_BOUGHT_ACTIVITY).navigation();
        } else if (view.getId() == R.id.comic_mine_history) {
            ARouter.getInstance().build(RouterMap.COMIC_HISTORY_ACTIVITY).navigation();
        } else if (view.getId() == R.id.comic_mine_notification) {
            ARouter.getInstance().build(RouterMap.COMIC_NOTIFICATION_ACTIVITY).navigation();
        } else if (view.getId() == R.id.comic_mine_help) {
            ARouter.getInstance().build(RouterMap.COMIC_HELP_ACTIVITY).navigation(getActivity());
        } else if (view.getId() == R.id.rl_edit_user_info) {
            if (LoginHelper.getOnLineUser() != null) {
                ARouter.getInstance().build(RouterMap.COMIC_USERINFO_ACTIVITY).navigation(getActivity());
            }
        } else if (view.getId() == R.id.btn_my_coin) {
            int egold = 0;
            if (mPayInfo != null) {
                egold = mPayInfo.getTotal_egold();
            }
            ARouter.getInstance().build(RouterMap.COMIC_MYCOIN_ACTIVITY).withInt(Constants.IntentKey.COIN, egold).navigation();
        } else if (view.getId() == R.id.btn_my_rebate) {
            ARouter.getInstance().build(RouterMap.COMIC_MY_REBATE_ACTIVITY).withSerializable(Constants.IntentKey.PAY_INFO, mPayInfo).navigation();
        } else if (view.getId() == R.id.btn_my_apprentice) {
            ARouter.getInstance().build(RouterMap.COMIC_MINE_APPRENTICE_ACTIVITY).navigation();
        } else if (view.getId() == R.id.comic_mine_coin_pay) {//书币充值
            PayActivity.toPay(getActivity(), "1", 0);
        } else if (view.getId() == R.id.comic_mine_vip_pay) {//开通会员
            PayActivity.toPay(getActivity(), "2", 0);
        }
    }

    /**
     * 刷新用户信息的通知
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshUserinfo(UpdateUserInfoEvent event) {
        if (event.getUserInfo() != null) {
            onGetUserInfo(event.getUserInfo());
        } else {
            getP().getUserInfo();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void logoutEvent(LogoutEvent event) {
        mNickname.setText("未登录");
        mCoins.setText("0");
        mRebate.setText("0");
        mApprentice.setText("0");
        ILFactory.getLoader().loadResource(headImg, R.drawable.bg_avatar,
                new RequestOptions().transforms(new CenterCrop(), new CircleCrop()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loginEvent(LoginEvent loginEvent) {
        getP().getUserInfo();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean useEventBus() {
        return true;
    }
}
