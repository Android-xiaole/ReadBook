package com.jj.comics.ui.mine;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
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
import com.jj.comics.common.callback.LoginNavigationCallbackImpl;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.constants.RequestCode;
import com.jj.comics.data.model.PayInfo;
import com.jj.comics.data.model.SignAutoResponse;
import com.jj.comics.data.model.SignResponse;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.ui.mine.settings.SettingActivity;
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
import q.rorbin.badgeview.QBadgeView;

/**
 * 我的页面
 */
@Route(path = RouterMap.COMIC_MINE_FRAGMENT)
public class MineFragment extends BaseCommonFragment<MinePresenter> implements MineContract.IMineView, MineItemView.OnCheckedChangeListener {
    @BindView(R2.id.comic_mine_head)
    ImageView mHead;
    @BindView(R2.id.tv_user_name)
    TextView mNickName;
    @BindView(R2.id.btn_user_check_in)
    TextView mTvCheckIn;
    @BindView(R2.id.tv_user_desc)
    TextView mUserDesc;
    @BindView(R2.id.tv_user_id)
    TextView mUserId;
    @BindView(R2.id.iv_user_sex)
    ImageView mGender;
    @BindView(R2.id.comic_mine_coin)
    TextView mUserCoin;
    @BindView(R2.id.comic_mine_vip)
    ImageView mUserVip;
    @BindView(R2.id.comic_mine_head_container)
    View mHeadContainer;
    //    @BindView(R2.id.comic_mine_buy)
//    Switch mBuy;
//    @BindView(R2.id.comic_mine_cache)
//    TextView mCacheSize;
    @BindView(R2.id.comic_mine_vip_time)
    TextView mVipTime;
    @BindView(R2.id.rl__)
    RelativeLayout mRl__;
    @BindView(R2.id.comic_mine_auto_buy)
    MineItemView mineItemView_autoBuy;
    @BindView(R2.id.comic_mine_clear_cache)
    MineItemView mineItemView_clearCache;
    @BindView(R2.id.comic_mine_feedback)
    MineItemView mineItemView_feedback;
    @BindView(R2.id.comic_mine_fuli_center)
    MineItemView mineItemView_fuli;
    private LoginNavigationCallbackImpl mLoginCallBack;
    private QBadgeView taskQBadgeView;
    private QBadgeView feedBackQBadgeView;

    @Override
    public void initData(Bundle savedInstanceState) {
        //上传访问我的界面  key为accessUserCenter
        MobclickAgent.onEvent(getContext(), Constants.UMEventId.ACCESS_USER_CENTER);
        mineItemView_autoBuy.setChecked(getP().hasAllSubscribe());
        mineItemView_autoBuy.setOnCheckedChangeListener(this);
        mLoginCallBack = new LoginNavigationCallbackImpl(getBaseActivity());

        mUserDesc.setVisibility(View.GONE);
        taskQBadgeView = new QBadgeView(getContext());
        feedBackQBadgeView = new QBadgeView(getContext());
    }

    @OnClick({R2.id.comic_mine_coin_container, R2.id.comic_mine_vip_container, R2.id.comic_mine_recharge_center,
            R2.id.comic_mine_recharge_record, /*R2.id.comic_mine_pay_record, */R2.id.comic_mine_clear_cache,
            /*R2.id.comic_mine_exit,*/ R2.id.comic_mine_head, R2.id.comic_mine_kefu,
            R2.id.comic_mine_feedback, R2.id.tv_user_desc, R2.id.comic_mine_vip_center, R2.id.comic_mine_fuli_center,
            R2.id.comic_mine_reward_center, R2.id.tv_user_name, R2.id.btn_user_check_in, R2.id.tv_user_id, R2.id.btn_settings_user,
            R2.id.rl__})
    void dealClick(View view) {
        if (view.getId() == R.id.comic_mine_recharge_record) {//充值记录
            navigationRoute(RouterMap.COMIC_NEW_RECHARGE_RECORD_ACTIVITY);
        } /*else if (view.getId() == R.id.comic_mine_pay_record) {//自动购买
//            ARouter.getInstance()
//                    .build(RouterMap.COMIC_PAY_RECORD_ACTIVITY)
//                    .navigation(getActivity(), mLoginCallBack);

        }*/ else if (view.getId() == R.id.comic_mine_clear_cache) {//清除缓存
            getP().clearCache();
        } else if (view.getId() == R.id.comic_mine_coin_container || view.getId() == R.id.comic_mine_recharge_center) {//退出
            //进入充值界面
            navigationRoute(RouterMap.COMIC_PAY_ACTIVITY);
        } else if (view.getId() == R.id.comic_mine_vip_center || view.getId() == R.id.comic_mine_vip_container) {
            navigationRoute(RouterMap.COMIC_PAY_ACTIVITY);
        } else if (view.getId() == R.id.comic_mine_fuli_center) {
            navigationRoute(RouterMap.COMIC_GOLD_CENTER_ACTIVITY);
        } else if (view.getId() == R.id.comic_mine_reward_center) {
            navigationRoute(RouterMap.COMIC_REWARD_ACTIVITY);
        } else if (view.getId() == R.id.btn_settings_user) {
            SettingActivity.toPay(getBaseActivity());
        } else if (view.getId() == R.id.comic_mine_kefu) {//跳转到联系客服页面
            ARouter.getInstance().build(RouterMap.COMIC_KEFU_ACTIVITY).navigation(getActivity());
        } else if (view.getId() == R.id.comic_mine_feedback) {//跳转到我的反馈页面
            navigationRoute(RouterMap.COMIC_MYFEEDBACK_ACTIVITY, RequestCode.MINE_REQUEST_CODE);
        } else if (view.getId() == R.id.comic_mine_head || view.getId() == R.id.rl__
                || view.getId() == R.id.tv_user_name || view.getId() == R.id.tv_user_desc) {

            if (LoginHelper.getOnLineUser() == null) {
                ARouter.getInstance().build(RouterMap.COMIC_LOGIN_ACTIVITY).navigation(getActivity());
            }
            //******************************************
            //4.0第一版本隐藏此处功能
//            else {
//                //无网络连接
//                if (NetWorkUtil.getNetworkType() == NetWorkUtil.NetworkType.NETWORK_UNKNOWN || NetWorkUtil.getNetworkType() == NetWorkUtil.NetworkType.NETWORK_NO) {
//                    ToastUtil.showToastShort(BaseApplication.getApplication().getString(com.jj.base.R.string.base_connect_error));
//                    return;
//                }
//                ARouter.getInstance().build(RouterMap.COMIC_CHECK_USER_INFO_ACTIVITY).navigation(getActivity());
//            }

        } else if(view.getId() == R.id.tv_user_id) {
            ClipboardManager clipboardManager =
                    (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardManager.setPrimaryClip(ClipData.newPlainText(null,mUserId.getText()));
            showToastShort("复制用户ID成功");
        }else if (view.getId() == R.id.btn_user_check_in) {
            navigationRoute(RouterMap.COMIC_GOLD_CENTER_ACTIVITY, RequestCode.MINE_REQUEST_CODE);
        }
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
        if (visiable) {
            UserInfo loginUser = LoginHelper.getOnLineUser();
            if (loginUser == null) {
                mUserDesc.setText(R.string.comic_desc_not_login_text);
                mGender.setVisibility(View.GONE);
                mUserId.setVisibility(View.INVISIBLE);
            } else {
                getP().getUserInfo();
                getP().getUserPayInfo();
                getP().getFeedbackStatus();
                getP().signAuto();
                getP().getTaskStatus();
            }

        }
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
        updateUserInfo(userInfo);
    }

    @Override
    public void onGetUserPayInfo(PayInfo payInfo) {
        updatePayInfo(payInfo);
    }

    /**
     * 更新用户基本信息
     *
     * @param user
     */
    private void updateUserInfo(UserInfo user) {
        if (user != null) {
            if (TextUtils.isEmpty(user.getAvatar()))
                ILFactory.getLoader().loadResource(mHead, R.drawable.icon_user_avatar_default,
                        new RequestOptions().transforms(new CenterCrop(), new CircleCrop()));
            else ILFactory.getLoader().loadNet(mHead, user.getAvatar(),
                    new RequestOptions().transforms(new CenterCrop(), new CircleCrop()).error(R.drawable.img_loading)
                            .placeholder(R.drawable.img_loading));
            if (user.getSex() == -1) {
                mGender.setVisibility(View.GONE);
            } else {
                if (user.getSex() == 1) {
                    //男生
                    ILFactory.getLoader().loadResource(mGender, R.drawable.icon_user_sex_man, null);
                } else if (user.getSex() == 0) {
                    ILFactory.getLoader().loadResource(mGender, R.drawable.icon_user_sex_women, null);
                } else if (user.getSex() == 2) {
                    ILFactory.getLoader().loadResource(mGender, R.drawable.icon_user_sex_women, null);
                }
                mGender.setVisibility(View.VISIBLE);
            }
            mNickName.setText(user.getNickname());
            mUserId.setVisibility(View.VISIBLE);
            mUserId.setText(String.format(getString(R.string.comic_id_format), user.getUid() + ""));
        } else {
            ILFactory.getLoader().loadResource(mHead, R.drawable.icon_user_avatar_default,
                    new RequestOptions().transforms(new CenterCrop(), new CircleCrop()));
            mUserDesc.setText(R.string.comic_desc_not_login_text);
            mGender.setVisibility(View.GONE);
            mUserId.setVisibility(View.INVISIBLE);
            mVipTime.setText(R.string.comic_click_open_vip_text);
        }

        //********************************
        //接口未开发，4.0第一版隐藏个性签签名
        mUserDesc.setVisibility(View.GONE);
    }

    /**
     * 更新用户支付相关信息
     *
     * @param payInfo
     */
    private void updatePayInfo(PayInfo payInfo) {
        //设置金币数量
        mUserCoin.setText(String.format(getString(R.string.comic_coin_format), payInfo.getTotal_egold() + ""));

        if (payInfo.getIs_svip() == 1) {//如果用户是SVIP则优先显示SVIP信息
            ILFactory.getLoader().loadResource(mUserVip, R.drawable.img_comic_pay_vip_open_svip, null);
            int svip_endday = payInfo.getSvip_endday();
            if (svip_endday > 1000) {
                mVipTime.setText("永久会员");
            }else{
                mVipTime.setText("还剩" + svip_endday +"天");
            }
        } else if (payInfo.getIs_vip() == 1) {//其次显示vip信息
            ILFactory.getLoader().loadResource(mUserVip, R.drawable.img_comic_pay_vip_open_vip, null);
            int vip_endday = payInfo.getVip_endday();
            mVipTime.setText("还剩" + vip_endday +"天");
        } else {//最后是未开通vip
            ILFactory.getLoader().loadResource(mUserVip, R.drawable.img_comic_pay_vip_close, null);
            mVipTime.setText(R.string.comic_click_open_vip_text);
        }
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
        mTvCheckIn.setVisibility(View.GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void logoutEvent(LogoutEvent event) {
        mNickName.setText(R.string.comic_click_login_text);
        mUserCoin.setText(String.format(getString(R.string.comic_coin_format), 0 + ""));
        ILFactory.getLoader().loadResource(mHead, R.drawable.icon_user_avatar_default,
                new RequestOptions().transforms(new CenterCrop(), new CircleCrop()));
        mUserDesc.setText(R.string.comic_desc_not_login_text);
        mTvCheckIn.setVisibility(View.INVISIBLE);
        //********************************
        //4.0第一版本隐藏个性签名
        mUserDesc.setVisibility(View.GONE);
        mGender.setVisibility(View.GONE);
        mUserId.setVisibility(View.INVISIBLE);
        mVipTime.setText(R.string.comic_click_open_vip_text);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loginEvent(LoginEvent loginEvent) {
        //登录成功的推送
//        getP().getUserInfo();
//        getP().getUserPayInfo();
//        getP().getFeedbackStatus();
//        getP().signAuto();
    }

    @Override
    public void onCheckedChanged(CompoundButton switchView, boolean isChecked) {
        getP().autoBuy(isChecked);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dealAutoBuy(UpdateAutoBuyStatusEvent updateAutoBuyStatusEvent) {
//        mBuy.setChecked(getP().hasAllSubscribe());
//        mBuy.setVisibility(View.VISIBLE);
        mineItemView_autoBuy.setChecked(getP().hasAllSubscribe());
    }

    @Override
    public void onDestroy() {
        mLoginCallBack.destroy();
        super.onDestroy();
    }

    @Override
    public boolean useEventBus() {
        return true;
    }


    @Override
    public void setCacheSize(String size) {
        mineItemView_clearCache.setRightText(size);
    }

    @Override
    public void onGetFeedbackStatus(int unReadCount) {
        if (unReadCount > 0 && mineItemView_feedback != null) {
            feedBackQBadgeView.bindTarget(mineItemView_feedback)
                    .setShowShadow(false).setBadgeNumber(unReadCount);
        } else {
            feedBackQBadgeView.hide(true);
        }

    }

    @Override
    public void fillSignAuto(SignAutoResponse response) {
        if (response.getData().getIs_check() == 0) {
            mTvCheckIn.setVisibility(View.VISIBLE);
        } else {
            mTvCheckIn.setVisibility(View.GONE);
        }

//        if (response.getData().getIs_autoby() == 0) {
//            getP().autoBuy(false);
//            mineItemView_autoBuy.setChecked(false);
//        } else {
//            getP().autoBuy(true);
//            mineItemView_autoBuy.setChecked(true);
//        }
    }

    @Override
    public void onGetTaskInfo(int count) {
        if (count > 0 && mineItemView_fuli != null) {
            taskQBadgeView.bindTarget(mineItemView_fuli)
                    .setShowShadow(false).setBadgeNumber(count);
        } else {
            taskQBadgeView.hide(true);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void signSuccess(SignResponse response) {
        mTvCheckIn.setVisibility(View.GONE);
    }

}
