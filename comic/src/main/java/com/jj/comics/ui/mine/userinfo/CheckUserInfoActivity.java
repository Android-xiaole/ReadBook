package com.jj.comics.ui.mine.userinfo;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.tabs.TabLayout;
import com.jj.base.imageloader.ILFactory;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.ViewPagerAdapter;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.util.LoginHelper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.COMIC_CHECK_USER_INFO_ACTIVITY)
public class CheckUserInfoActivity extends BaseActivity<CheckUserInfoPresenter> implements CheckUserInfoContract.ICheckUserInfoView{

    @BindView(R2.id.tv_userinfo_edit)
    TextView mTvEditUserInfo;
    @BindView(R2.id.btn_back_userinfo)
    ImageView mBtnBack;
    @BindView(R2.id.iv_userinfo_sex)
    ImageView mIvSex;
    @BindView(R2.id.comic_userinfo_head)
    ImageView mIvUserInfoAvatar;
    @BindView(R2.id.tv_userinfo_desc)
    TextView mTvUserInfoDesc;
    @BindView(R2.id.tv_userinfo_area)
    TextView mTvUserInfoArea;
    @BindView(R2.id.tv_userinfo_id)
    TextView mTvUserInfoID;
    @BindView(R2.id.tv_userinfo_name)
    TextView mTvUserInfoName;
    @BindView(R2.id.tl_userinfo)
    TabLayout mTbUserInfo;
    @BindView(R2.id.vp_userinfo)
    ViewPager mVpUserInfo;

    @Override
    public void initData(Bundle savedInstanceState) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), getP().getFragments(),
                getP().getTabNames());
        mVpUserInfo.setAdapter(adapter);
        mTbUserInfo.setupWithViewPager(mVpUserInfo);
        mTbUserInfo.setTabIndicatorFullWidth(false);
//        mTbUserInfo.setTabRippleColorResource(R.color.base_yellow_ffd850);
        updateUserInfoDisplay();
    }

    private void updateUserInfoDisplay() {
        UserInfo user = LoginHelper.getOnLineUser();
        if (user != null) {
            if (TextUtils.isEmpty(user.getAvatar()))
                ILFactory.getLoader().loadResource(mIvUserInfoAvatar, R.drawable.icon_user_avatar_default,
                        new RequestOptions().transforms(new CenterCrop(), new CircleCrop()));
            else ILFactory.getLoader().loadNet(mIvUserInfoAvatar, user.getAvatar(),
                    new RequestOptions().transforms(new CenterCrop(), new CircleCrop())
                            .error(R.drawable.img_loading)
                            .placeholder(R.drawable.img_loading));

            if (user.getSex() == -1) {
                mIvSex.setVisibility(View.GONE);
            } else {
                if (user.getSex() == 1) {
                    //男生
                    ILFactory.getLoader().loadResource(mIvSex, R.drawable.icon_user_sex_man, null);
                } else if (user.getSex() == 0) {
                    ILFactory.getLoader().loadResource(mIvSex, R.drawable.icon_user_sex_women, null);
                } else if (user.getSex() == 2) {
                    ILFactory.getLoader().loadResource(mIvSex, R.drawable.icon_user_sex_women, null);
                }
                mIvSex.setVisibility(View.VISIBLE);
            }

            mTvUserInfoName.setText(user.getNickname());

//            String area = user.getProvince() + " " + user.getCity();
//            mTvUserInfoArea.setText(area);
//            mTvUserInfoID.setText(String.format(getString(R.string.comic_id_format), user.getUserId() + ""));
//            mTvUserInfoDesc.setText(TextUtils.isEmpty(user.getCustomSignature()) ? getString(R.string.comic_no_signature) : user.getCustomSignature());
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_avtivity_check_userinfo;
    }

    @Override
    public CheckUserInfoPresenter setPresenter() {
        return new CheckUserInfoPresenter();
    }

    @OnClick({R2.id.tv_userinfo_edit, R2.id.btn_back_userinfo})
    void dealOnClick(View view) {
        if (view.getId() == R.id.tv_userinfo_edit) {
            ARouter.getInstance().build(RouterMap.COMIC_EDIT_USER_INFO_ACTIVITY).navigation(this);
        } else if (view.getId() == R.id.btn_back_userinfo) {
            finish();
        }
    }

    //修改用户信息界面的消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserInfoUpdated(UserInfo userInfo) {
        updateUserInfoDisplay();
    }

    @Override
    public boolean useEventBus() {
        return true;
    }


    @Override
    public boolean hasFragment() {
        return true;
    }
}
