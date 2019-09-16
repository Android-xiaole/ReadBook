package com.jj.comics.ui.mine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.jj.base.imageloader.ILFactory;
import com.jj.base.ui.BaseCommonFragment;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.constants.RequestCode;
import com.jj.comics.data.model.PayInfo;
import com.jj.comics.data.model.SignAutoResponse;
import com.jj.comics.data.model.SignResponse;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.ui.mine.login.LoginActivity;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.util.eventbus.events.LoginEvent;
import com.jj.comics.util.eventbus.events.LogoutEvent;
import com.jj.comics.util.eventbus.events.UpdateAutoBuyStatusEvent;
import com.jj.comics.util.eventbus.events.UpdateSignStatusEvent;
import com.jj.comics.util.eventbus.events.UpdateUserInfoEvent;
import com.jj.comics.widget.comic.MineItemView;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 我的页面
 */
@Route(path = RouterMap.COMIC_MINE_FRAGMENT)
public class MineFragment extends BaseCommonFragment<MinePresenter> implements MineContract.IMineView, MineItemView.OnCheckedChangeListener {
    @BindView(R2.id.comic_mine_buy)
    MineItemView mineItemView_buy;
    @BindView(R2.id.comic_mine_history)
    MineItemView mineItemView_history;
    @BindView(R2.id.comic_mine_notification)
    MineItemView mineItemView_notification;
    @BindView(R2.id.comic_mine_recharge)
    MineItemView mineItemView_recharge;
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

    private int mCoin = 99999;

    @Override
    public void initData(Bundle savedInstanceState) {
        //上传访问我的界面  key为accessUserCenter
        MobclickAgent.onEvent(getContext(), Constants.UMEventId.ACCESS_USER_CENTER);
        getP().getUserInfo();
        getP().getUserPayInfo();
    }

    /**
     * 页面跳转限制
     *
     * @param route
     */
    private void navigationRoute(String route) {
        if (LoginHelper.getOnLineUser() == null) {
            ARouter.getInstance().build(RouterMap.COMIC_LOGIN_ACTIVITY).navigation(getActivity());
        } else {
            ARouter.getInstance().build(route).navigation(getActivity());
        }
    }

    private void navigationRoute(String route, int requestCode) {
        if (LoginHelper.getOnLineUser() == null) {
            ARouter.getInstance().build(RouterMap.COMIC_LOGIN_ACTIVITY).navigation(getActivity(), requestCode);
        } else {
            ARouter.getInstance().build(route).navigation(getActivity(), requestCode);
        }
    }

    @Override
    public void onVisiable(boolean visiable) {
        super.onVisiable(visiable);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case RequestCode.PAY_REQUEST_CODE:
                    showProgress();
                    getP().getUserInfo();
                    break;
                case RequestCode.MINE_REQUEST_CODE:
                    getP().getFeedbackStatus();
                    break;
//                case RequestCode.OPEN_PIC_REQUEST_CODE:
//                    final String file = Environment.getExternalStorageDirectory().getAbsoluteFile().getAbsolutePath()
//                            + File.separator + PackageUtil.getAppName(getContext()) + File.separator;
//                    Uri orgUri = data.getData();
//                    String fileName = FileUtil.getFileName(Utils.getRealFilePath(getContext(), orgUri));
//                    IntentUtils.cropImageUri(getActivity(), orgUri, Uri.fromFile(new File(file + fileName)));
//                    break;
//                case RequestCode.CROP_IMG_REQUEST_CODE:
//                    Uri uri = data.getData();
//                    LogUtil.e(uri.getPath());
//                    break;
            }
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
            ILFactory.getLoader().loadResource(headImg, R.drawable.icon_user_avatar_default,
                    new RequestOptions().transforms(new CenterCrop(), new CircleCrop()));
        } else {
            ILFactory.getLoader().loadNet(headImg, userInfo.getAvatar(),
                    new RequestOptions().transforms(new CenterCrop(), new CircleCrop()).error(R.drawable.img_loading)
                            .placeholder(R.drawable.img_loading));
        }
        //设置昵称
        mNickname.setText(userInfo.getNickname());
    }

    @Override
    public void onGetUserPayInfo(PayInfo payInfo) {
        mCoins.setText(payInfo.getTotal_egold() + "");
        mRebate.setText(payInfo.getCan_drawout_amount() + "");
        mApprentice.setText(payInfo.getDisciple_num() + "");
    }

    @OnClick({R2.id.mine_head_img, R2.id.comic_mine_buy, R2.id.comic_mine_history,
            R2.id.comic_mine_notification, R2.id.comic_mine_recharge, R2.id.comic_mine_help,
            R2.id.edit_user_info,
            R2.id.btn_my_rebate, R2.id.btn_my_coin})
    void onClick(View view) {
        if (view.getId() == R.id.mine_head_img) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        } else if (view.getId() == R.id.comic_mine_buy) {
            ToastUtil.showToastShort("me" + view.getId());
        } else if (view.getId() == R.id.comic_mine_history) {
            ARouter.getInstance().build(RouterMap.COMIC_HISTORY_ACTIVITY).navigation();
        } else if (view.getId() == R.id.comic_mine_notification) {
            ARouter.getInstance().build(RouterMap.COMIC_NOTIFICATION_ACTIVITY).navigation();
        } else if (view.getId() == R.id.comic_mine_recharge) {
            ARouter.getInstance().build(RouterMap.COMIC_PAY_ACTIVITY).navigation();
        } else if (view.getId() == R.id.comic_mine_help) {
            ToastUtil.showToastShort("me" + view.getId());
            ARouter.getInstance().build(RouterMap.COMIC_SEARCH_ACTIVITY).navigation(getActivity());
        } else if (view.getId() == R.id.edit_user_info) {
            ARouter.getInstance().build(RouterMap.COMIC_USERINFO_ACTIVITY).navigation(getActivity());
        } else if (view.getId() == R.id.btn_my_coin) {
            ARouter.getInstance().build(RouterMap.COMIC_MYCOIN_ACTIVITY).withLong("coin", mCoin).navigation();
        } else if (view.getId() == R.id.btn_my_rebate) {

        }
    }

    /**
     * 更新用户基本信息
     *
     * @param user
     */
    private void updateUserInfo(UserInfo user) {
    }

    /**
     * 更新用户支付相关信息
     *
     * @param payInfo
     */
    private void updatePayInfo(PayInfo payInfo) {
    }

    /**
     * 刷新用户信息的通知
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshUserinfo(UpdateUserInfoEvent updateUserInfoEvent) {
        getP().getUserInfo();
    }

    /**
     * 签到成功的消息
     *
     * @param updateSignStatusEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void signSuccessEvent(UpdateSignStatusEvent updateSignStatusEvent) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void logoutEvent(LogoutEvent event) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loginEvent(LoginEvent loginEvent) {
    }

    @Override
    public void onCheckedChanged(CompoundButton switchView, boolean isChecked) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dealAutoBuy(UpdateAutoBuyStatusEvent updateAutoBuyStatusEvent) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean useEventBus() {
        return true;
    }


    @Override
    public void setCacheSize(String size) {
    }

    @Override
    public void onGetFeedbackStatus(int unReadCount) {

    }

    @Override
    public void fillSignAuto(SignAutoResponse response) {
    }

    @Override
    public void onGetTaskInfo(int count) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void signSuccess(SignResponse response) {
    }

}
